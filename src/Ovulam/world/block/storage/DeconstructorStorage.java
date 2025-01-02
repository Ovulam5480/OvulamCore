package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.content;
import static mindustry.Vars.tilesize;

public class DeconstructorStorage extends PayloadStorageBlock {
    public float deconstructSpeed = 2.5f;

    public Effect DeconstructorEffect = new Effect(12, e -> {
        color(Pal.remove);
        randLenVectors(e.id, 1, 16 * e.finpow(), (x, y) ->
                Fill.square(e.x + x, e.y + y, 1f + e.fout() * (3f + e.rotation)));
    });


    public DeconstructorStorage(String name) {
        super(name);
    }

    public class DeconstructorStorageBuild extends PayloadStorageBuild {
        public @Nullable Payload deconstructing;
        public float progress;
        //输出目标, 通常是自身, 与核心相贴时是核心
        public Building target;
        public @Nullable float[] accum = new float[content.items().size];
        public float time, speedScl, lineX;
        public ItemSeq requirements = new ItemSeq();


        public boolean acceptUnitPayload(Unit unit) {
            return payload == null && deconstructing == null && unit.type.allowedInPayloads && !unit.spawnedByCore
                    && unit.hitSize / tilesize <= maxPayloadSize
                    && (unit.type().payloadCapacity == 0 || !(unit instanceof Payloadc p && p.hasPayload()));
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return this.payload == null && deconstructing == null && payload.fits(maxPayloadSize);
        }

        public void handlePayloadItem(Payload payload, Item item) {
            int itemAmount = 0;

            if (payload instanceof BuildPayload B && B.block().hasItems) {
                itemAmount = ((BuildPayload) deconstructing).build.items.get(item);
                ((BuildPayload) deconstructing).build.items.remove(item, itemAmount);
            } else if (payload instanceof UnitPayload U && U.unit.item() == item) {
                itemAmount = ((UnitPayload) deconstructing).unit.stack.amount;
                ((UnitPayload) deconstructing).unit.stack.set(item, 0);
            }

            int itemSpace = target.block.itemCapacity - target.items.get(item);
            target.items.add(item, Mathf.clamp(itemSpace, 0, itemAmount));
        }

        public boolean canAcceptItem(Item item) {
            return target.items.get(item) < target.getMaximumAccepted(item);
        }

        public int acceptItemAmount(Item item) {
            return Math.max(target.getMaximumAccepted(item) - target.items.get(item), 0);
        }

        @Override
        public void updateTile() {
            super.updateTile();

            boolean canProgress = deconstructing != null;

            payRotation = Angles.moveToward(payRotation, 90f, payloadRotateSpeed * edelta() * (coreAugment() ? 10 : 1));
            speedScl = Mathf.lerpDelta(speedScl, Mathf.sign(canProgress), 0.1f);

            if (hasCoreMerge()) target = getProximityCore().first();
            else target = this;

            if (canProgress) {
                //特效
                float ey = Mathf.range(size * tilesize / 2f);
                if (Mathf.chanceDelta(Math.abs(Mathf.cos(time, 20f, 1)))) {
                    DeconstructorEffect.at(x + lineX, y + ey, rotation);
                }
                //转化值
                float shift;
                float realShift;

                if(coreAugment()){
                    realShift = progress = 1f;
                }else {
                    shift = edelta() * deconstructSpeed / deconstructing.buildTime();
                    realShift = Math.min(shift, 1f - progress);
                    progress += shift;
                }
                time += edelta();

                for (Item item : content.items()) {
                    accum[item.id] += requirements.get(item) * (payload instanceof BuildPayload ?
                            Vars.state.rules.buildCostMultiplier : Vars.state.rules.unitCost(team)) * realShift;
                }

                //处理 解构载荷时 的物品获取
                for (Item item : content.items()) {
                    int has = Mathf.floor(accum[item.id]);
                    //todo state.rules.coreIncinerates
                    if(has == 0)continue;
                    //对于该物品的剩余空间, 确保不会溢出物品
                    int taken = Math.min(has, acceptItemAmount(item));
                    if (canAcceptItem(item)) {
                        target.items.add(item, taken);
                        accum[item.id] -= taken;
                    }
                }
            }else progress = 0f;

            if (progress >= 1f) {
                boolean checkErrors = true;

                content.items().each(item -> handlePayloadItem(deconstructing, item));

                for (ItemStack itemStack : requirements) {
                    Item item = itemStack.item;
                    //存在误差时，误差物品+1,tolerance越小,出现误差的可能越小
                    if (Mathf.equal(accum[item.id], 1f, 0.0001f)) {
                        checkErrors = false;
                        target.items.add(item, 1);
                    }
                    accum[item.id] = 0;
                }

                if (checkErrors) {
                    Fx.breakBlock.at(x, y, deconstructing.size() / tilesize);
                    deconstructing = null;
                }
                requirements.clear();
            }

            //输入载荷的开始拆解
            //否则，如果已经输入载荷，并且自身携带的载荷不为无
            else if (moveInPayload() && payload != null) {
                //将解构载荷设为携带的载荷
                deconstructing = payload;
                //自身携带的载荷设为无
                payload = null;
                progress = 0f;
                //requirements 解构载荷的建造消耗
                for (ItemStack itemStack : deconstructing.requirements()) {
                    requirements.add(itemStack);
                }
            }
        }

        @Override
        public void draw() {
            Draw.rect(region, x, y);

            Draw.z(Layer.blockOver);
            drawPayload();

            if (deconstructing != null) {
                deconstructing.set(x + payVector.x, y + payVector.y, payRotation);

                Draw.z(Layer.blockOver);
                deconstructing.drawShadow(1f - progress);
                Draw.draw(Layer.blockOver, () -> {
                    Drawf.construct(x, y, deconstructing.icon(), Pal.remove, deconstructing instanceof BuildPayload ? 0f : payRotation - 90f, 1f - progress, 1f - progress, time);
                    Draw.color(Pal.remove);
                    Draw.alpha(1f);

                    Draw.reset();
                });


                Draw.z(Layer.effect);

                lineX = Mathf.sin(time, 20f, tilesize / 2f * block.size - 3f);
                Draw.color(Pal.remove);

                Lines.lineAngleCenter(x + lineX, y, 90f, size * tilesize - 6f);
                Draw.reset();

            }
            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);

            //todo 颜色应当对齐  team和remove
            Draw.color(linkedCore != null ? team.color : Pal.remove);
            Draw.rect(Mathf.mod((rotation), 4) < 2 ? teamRegion1 : teamRegion2, x, y, rotation * 90);
            Draw.reset();

        }

        public boolean moveInPayload() {
            if (payload == null) return false;
            updatePayload();
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());
            return payVector.isZero(0.01f);
        }

        public void drawPayload() {
            if (payload != null) {
                updatePayload();

                Draw.z(Layer.blockOver);
                payload.draw();
            }
        }

        public void updatePayload() {
            if (payload != null) {
                payload.set(x + payVector.x, y + payVector.y, payRotation);
            }
        }

    }
}
