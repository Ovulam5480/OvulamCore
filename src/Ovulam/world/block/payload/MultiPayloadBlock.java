package Ovulam.world.block.payload;

import Ovulam.world.move.MoveDefault;
import Ovulam.world.move.MovePayload;
import Ovulam.world.type.PositionPayload;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.type.PayloadSeq;
import mindustry.type.PayloadStack;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.Payload;

import static mindustry.Vars.tilesize;

//能够接收多个载荷的方块
public class MultiPayloadBlock extends Block {

    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;
    public int payloadAmountCapacity = 32;
    public float payloadCapacity = 512f;
    public MovePayload moveInMover = new MoveDefault();
    public boolean dumpFacing = false;

    public MultiPayloadBlock(String name) {
        super(name);
        update = true;
        sync = true;
        acceptsPayload = true;
        rotate = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("liquid");
    }

    @Override
    public void load() {
        super.load();
    }

    //是否已经到达目标位置
    public static boolean hasArrived(PositionPayload payload) {
        return payload.currentPosition.epsilonEquals(payload.targetPosition, 0.01f);
    }

    public static class PositionBuild extends Building {
    }

    /////////////////////
    public class MultiPayloadBlockBuild extends Building {
        public Seq<PositionPayload> positionPayloads = new Seq<>();
        public float payRotation;

        public boolean acceptUnitPayload(Unit unit) {
            return true;
        }

        @Override
        public boolean canControlSelect(Unit unit) {
            return !unit.spawnedByCore
                    && unit.type.allowedInPayloads
                    && acceptUnitPayload(unit)
                    && unit.tileOn() != null
                    && unit.tileOn().build == this
                    && payloadUsed() + Mathf.sqr(unit.hitSize) <= payloadCapacity
                    && positionPayloads.size <= payloadAmountCapacity;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return payloadUsed() + Mathf.sqr(payload.size() / 8) < payloadCapacity &&
                    positionPayloads.size < payloadAmountCapacity;
        }

        //返回第一个载荷
        @Override
        public Payload takePayload() {
            if (positionPayloads.size == 0) {
                return null;
            }
            Payload t = positionPayloads.get(0).payload;
            positionPayloads.remove(0);
            return t;
        }

        public Payload getIndexPayload(int index) {
            return positionPayloads.get(index).payload;
        }

        //返回第一个的载荷
        @Override
        public Payload getPayload() {
            return positionPayloads.get(0).payload;
        }

        @Override
        public PayloadSeq getPayloads() {
            PayloadSeq payloads = new PayloadSeq();
            this.positionPayloads.each(payload -> {
                payloads.add(payload.content());
            });
            return payloads;
        }

        public Seq<PositionPayload> getPositionPayloads() {
            return positionPayloads;
        }

        public int getMoverLimit(Seq<PayloadStack> payloadStacks, Payload payload) {
            PayloadStack ps = payloadStacks.find(payloadStack -> payloadStack.item == payload.content());
            return ps == null ? 0 : ps.amount;
        }

        public PayloadSeq toPayloadSeq(Seq<PositionPayload> payloads) {
            PayloadSeq payloadSeq = new PayloadSeq();
            payloads.each(PositionPayload -> {
                payloadSeq.add(PositionPayload.content());
            });
            return payloadSeq;
        }

        public int getPayloadAmount(Payload payload) {
            return getPayloadAmount(payload.content());
        }

        public int getPayloadAmount(UnlockableContent payload) {
            return positionPayloads.count(positionPayload -> positionPayload.content() == payload);
        }

        @Override
        public void onControlSelect(Unit player) {
            float x = player.x, y = player.y;
            Vec2 currentPosition = new Vec2().set(x, y).sub(this).clamp(
                    -size * tilesize / 2f, -size * tilesize / 2f,
                    size * tilesize / 2f, size * tilesize / 2f);
            handleUnitPayload(player, p -> positionPayloads.add(new PositionPayload(p, currentPosition, setTargetPosition(positionPayloads.size + 1, moveInMover))));
        }

        @Override
        public void handlePayload(Building source, Payload payload) {
            Vec2 currentPosition = new Vec2().set(source).sub(this).clamp(
                    -size * tilesize / 2f, -size * tilesize / 2f,
                    size * tilesize / 2f, size * tilesize / 2f);
            positionPayloads.add(new PositionPayload(payload, currentPosition, setTargetPosition(positionPayloads.size + 1, moveInMover)));
        }

        public void handlePositionPayload(PositionPayload positionPayload) {
            positionPayloads.add(positionPayload);
            //立即更新一次, 很奇怪的时, 不更新就不会渲染载荷
            updatePayload(positionPayload);
        }

        public float payloadUsed() {
            return positionPayloads.sumf(p -> Mathf.sqr(p.payload.size() / tilesize));
        }

        public boolean facing(Building building) {
            return Mathf.mod(Mathf.angle(building.x - x, building.y - y) + 45 - rotdeg(), 360) < 90;
        }

        //载荷应当输出到的建筑, 哪怕这个建筑不能输入载荷
        public @Nullable Building dumpTargetBuilding(PositionPayload todump) {
            return proximity.find(building -> {
                float ang = Mathf.angle(building.x - todump.x(this), building.y - todump.y(this));
                return Math.abs(ang - rotdeg()) < 45f;
            });
        }

        public boolean dumpPositionPayload(PositionPayload todump) {
            Building target = dumpTargetBuilding(todump);
            if (target == null || !target.acceptPayload(this, todump.payload)) return false;

            Log.info(target.x + " and " + target.y);
            Building position = PositionBuild.create();
            position.set(todump.x(this), todump.y(this));

            target.handlePayload(position, todump.payload);
            positionPayloads.remove(todump);
            position.remove();
            return true;
        }

        //更新载荷（位置）
        public void updatePayload(PositionPayload payload) {
            payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * delta());
            payload.currentPosition.approach(payload.targetPosition, payloadSpeed * delta());
            payload.payload.set(payload.x(this), payload.y(this), payRotation);
        }

        //输入的目标位置
        public Vec2 setTargetPosition(int index, MovePayload mover) {
            return mover.setTargetPosition(this, index);
        }

        //设置载荷的目标位置
        public void movePayloads(PositionPayload payload) {
            payload.targetPosition = setTargetPosition(positionPayloads.indexOf(payload), moveInMover);
            updatePayload(payload);
        }

        //////////////////////////

        //绘制载荷
        public void drawPayload() {
            if (positionPayloads == null) return;
            positionPayloads.each(PositionPayload::draw);
        }
    }
}
