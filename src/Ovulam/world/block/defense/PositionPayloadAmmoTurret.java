package Ovulam.world.block.defense;

import Ovulam.world.block.payload.MultiPayloadBlock;
import Ovulam.world.type.PositionPayload;
import arc.math.Angles;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.world.blocks.defense.turrets.PayloadAmmoTurret;
import mindustry.world.blocks.payloads.Payload;

import static mindustry.Vars.tilesize;

public class PositionPayloadAmmoTurret extends PayloadAmmoTurret {
    //当建筑的旋转为0时，相对的目标位置
    public Vec2 targetPosition = new Vec2(20, 20);
    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;

    public PositionPayloadAmmoTurret(String name) {
        super(name);
        maxAmmo = 3;
        acceptsPayload = true;
        rotate = true;
        quickRotate = true;
    }

    public static Vec2 rotateTargetPosition(Vec2 targetPosition, int rotation){
        return new Vec2(targetPosition.x * Geometry.d8edge(rotation).x, targetPosition.y * Geometry.d8edge(rotation).y);
    }

    public class PositionPayloadAmmoTurretBuild extends PayloadTurretBuild {
        public Seq<PositionPayload> positionPayloads = new Seq<>();
        public float payRotation;

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return payloads.total() + positionPayloads.size < maxAmmo && ammoTypes.containsKey(payload.content());
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            Vec2 currentPosition = new Vec2().set(source).sub(this).clamp(
                    -size * tilesize / 2f, -size * tilesize / 2f,
                    size * tilesize / 2f, size * tilesize / 2f);
            positionPayloads.add(new PositionPayload(payload,currentPosition,
                    rotateTargetPosition(targetPosition, rotation())));
        }

        @Override
        public void updateTile(){
            Seq<PositionPayload> removes = new Seq<>();

            positionPayloads.each(positionPayload -> {
                movePayloads(positionPayload);
                if(MultiPayloadBlock.hasArrived(positionPayload)) {
                    Fx.placeBlock.at(positionPayload.payload.x(), positionPayload.payload.y(), positionPayload.payload.size());
                    removes.add(positionPayload);
                }
            });

            removes.each(positionPayload -> {
                payloads.add(positionPayload.content());
                positionPayloads.remove(positionPayload);
            });

            super.updateTile();
        }

        @Override
        public void draw(){
            super.draw();
            drawer.draw(this);
            drawPayload();
        }

        //更新载荷（位置）
        public void updatePayload(PositionPayload payload) {
            payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * delta());
            payload.currentPosition.approach(payload.targetPosition, payloadSpeed * delta());
            payload.payload.set(payload.x(this), payload.y(this), payRotation);
        }

        //设置载荷的目标位置
        public void movePayloads(PositionPayload payload) {
            payload.targetPosition = rotateTargetPosition(targetPosition, rotation());
            updatePayload(payload);
        }

        //绘制载荷
        public void drawPayload() {
            if (positionPayloads == null) return;
            positionPayloads.each(PositionPayload -> PositionPayload.payload.draw());
        }

    }
}
