package Ovulam.world.block.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.EnumSet;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.gen.Payloadc;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadDeconstructor;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import java.util.Arrays;

import static arc.graphics.g2d.Draw.color;
import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.*;
import static mindustry.world.blocks.storage.StorageBlock.incinerateEffect;

public class PayloadDeconstructorStorage extends PayloadDeconstructor {
    public TextureRegion region, topRegion, teamRegion1, teamRegion2, iconRegion;

    public PayloadDeconstructorStorage(String name){
        super(name);
        size = 3;
        itemCapacity = 1000;
        rotate = true;
        hasItems = true;
        solid = true;
        destructible = true;
        group = BlockGroup.transportation;
        separateItemCapacity = false;
        flags = EnumSet.of(BlockFlag.storage);
        allowResupply = true;
        envEnabled = Env.any;
        rotateDraw = false;

    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        teamRegion1 = Core.atlas.find(name + "-team-1");
        teamRegion2 = Core.atlas.find(name + "-team-2");
        iconRegion = Core.atlas.find(name + "-icon");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{iconRegion};
    }

    //草，写完发现怎么这么多
    @Override
    public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid){
        Draw.reset();
        Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
        Draw.reset();
        Draw.mixcol(faceCore(plan.drawx() / 8, plan.drawy() / 8, plan.rotation) != null ? player.team().color : Pal.remove,
                !valid ? Pal.breakInvalid : Color.white,
                (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
        Draw.rect(Mathf.mod((plan.rotation), 4) < 2 ? teamRegion1 : teamRegion2, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.reset();

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Building core = faceCore(x + offset / 8, y + offset / 8, rotation);
        if(core != null){
            Drawf.circles(core.x, core.y, core.block.size * 5, Pal.accent);
        }
    }

    public Building faceCore(float x, float y, int rotation){
        float trns = size / 2f + 0.1f + (Mathf.mod((rotation), 4) < 2 ? 1f : 0);
        Building building = world.build((int) (x + Geometry.d4(rotation).x * trns), (int) (y + Geometry.d4(rotation).y * trns));
        return building instanceof CoreBlock.CoreBuild ? building : null;
    }


//////////////////////////////////////////////////////////////////////////////////

    public class PayloadDeconstructorStorageBuild extends PayloadDeconstructorBuild {
        public float linesin;
        public @Nullable Building linkedCore;
        public boolean canProgress;
        public @Nullable float[] accum = new float[content.items().size];

        @Override
        public boolean acceptUnitPayload(Unit unit){
            return payload == null && deconstructing == null && unit.type.allowedInPayloads && !unit.spawnedByCore
                    && unit.type.getTotalRequirements().length > 0 && unit.hitSize / tilesize <= maxPayloadSize
                    && (unit.type().payloadCapacity == 0 || !(unit instanceof Payloadc p && p.hasPayload()));
        }

        //////////
        //下面代码都是仓库的
        @Override
        public boolean acceptItem(Building source, Item item){
            return canAcceptItem(item) && source.block != block;
        }

        public boolean canAcceptItem(Item item){
            if(linkedCore == null){
                return items.get(item) < getMaximumAccepted(item);
            } else return linkedCore.items.get(item) < linkedCore.getMaximumAccepted(item);
        }

        @Override
        public void handleItem(Building source, Item item){
            if(linkedCore != null){
                if(linkedCore.items.get(item) >= ((CoreBlock.CoreBuild) linkedCore).storageCapacity){
                    incinerateEffect(this, source);
                }
                ((CoreBlock.CoreBuild) linkedCore).noEffect = true;
                linkedCore.handleItem(source, item);
            } else {
                super.handleItem(source, item);
            }
        }

        @Override
        public void itemTaken(Item item){
            if(linkedCore != null){
                linkedCore.itemTaken(item);
            }
        }

        @Override
        public int removeStack(Item item, int amount){
            int result = super.removeStack(item, amount);

            if(linkedCore != null && team == Vars.state.rules.defaultTeam && Vars.state.isCampaign()){
                Vars.state.rules.sector.info.handleCoreItem(item, -result);
            }

            return result;
        }


        @Override
        public int getMaximumAccepted(Item item){
            return linkedCore != null ? linkedCore.getMaximumAccepted(item) : itemCapacity;
        }

        @Override
        public int explosionItemCap(){
            //when linked to a core, containers/vaults are made significantly less explosive.
            return linkedCore != null ? Math.min(itemCapacity / 60, 6) : itemCapacity;
        }

        @Override
        public void drawSelect(){
            if(linkedCore != null){
                linkedCore.drawSelect();
            }
        }

        @Override
        public void overwrote(Seq<Building> previous){
            //only add prev items when core is not linked
            if(linkedCore == null){
                for (Building other : previous){
                    if(other.items != null && other.items != items){
                        items.add(other.items);
                    }
                }

                items.each((i, a) -> items.set(i, Math.min(a, itemCapacity)));
            }
        }

        @Override
        public boolean canPickup(){
            return linkedCore == null;
        }

        //只链接一个核心
        public void linkCore(){
            float trns = size / 2f + 0.1f + (Mathf.mod((rotation), 4) < 2 ? 1f : 0);
            Building link = nearby((int) (trns * Geometry.d4[rotation].x), (int) (trns * Geometry.d4[rotation].y));
            if(link != null && link.block instanceof CoreBlock){
                linkedCore = link;
                items = linkedCore.items;
            }
        }


        public void moveItem(Payload payload, Item item){
            int itemAmount;

            if(payload instanceof BuildPayload B && B.block().hasItems){
                itemAmount = ((BuildPayload) deconstructing).build.items.get(item);
                ((BuildPayload) deconstructing).build.items.remove(item, itemAmount);
            } else if(payload instanceof UnitPayload U && U.unit.item() == item){
                itemAmount = ((UnitPayload) deconstructing).unit.stack.amount;
                ((UnitPayload) deconstructing).unit.stack.set(item, 0);
            } else {
                return;
            }


            if(itemAmount == 0){
                return;
            }

            int itemSpace;

            if(linkedCore == null){
                itemSpace = getMaximumAccepted(item) - items.get(item);
                items.add(item, Math.min(itemSpace, itemAmount));
            } else {
                itemSpace = linkedCore.block.itemCapacity - linkedCore.items.get(item);
                linkedCore.items.add(item, Math.min(itemSpace, itemAmount));
            }
        }

        //代码还可以改优化，但是我懒得改
        @Override
        public void updateTile(){
            float trns = size / 2f + 0.1f;

            //设定上未链接核心时，如果物品到达上限，应当暂停拆解.但是这样也不错是吧（
            //todo 应该允许玩家选择 在物品到达上限时，是否仍然进行拆解

            linkCore();
            if(linkedCore == null){
                proximity.each(building -> {
                    int ro = (((int) Mathf.angle(building.x - x, building.y - y) + 405) / 90) % 4;
                    if(ro == rotation){
                        if(items.total() > 0 && linkedCore == null){
                            for (Item item : content.items()){

                                //没有链接核心，并且或者物品到达上限
                                if(linkedCore == null && !canAcceptItem(item)) canProgress = false;

                                //dump太卡了
                                if(items.get(item) == 0 || !building.block.hasItems){
                                    continue;
                                }
                                int itemSpace = building.acceptStack(item, items.get(item), this);
                                building.handleStack(item, itemSpace, this);
                                items.remove(item, itemSpace);

                            }
                        }
                    }
                });
            }


            if(items.total() > 0 && linkedCore == null){
                Building b = front();
                for (Item item : content.items()){

                    //没有链接核心，并且或者物品到达上限
                    if(linkedCore == null && !canAcceptItem(item)) canProgress = false;

                    if(items.get(item) == 0 || b == null || !b.block.hasItems){
                        continue;
                    }
                    int itemSpace = b.acceptStack(item, items.get(item), this);
                    b.handleStack(item, itemSpace, this);
                    items.remove(item, itemSpace);

                }
            }


            //解构载荷为无
            if(deconstructing == null){
                //进程为0
                progress = 0f;
            }

            payRotation = Angles.moveToward(payRotation, 90f, payloadRotateSpeed * edelta());

            //如果存在解构载荷
            if(deconstructing != null){

                canProgress = true;
                ItemSeq reqs = new ItemSeq();

                //reqs 为解构载荷消耗的物品
                for (ItemStack itemStack : deconstructing.requirements()){
                    if(itemStack.amount > 0) reqs.add(itemStack);
                }

                //如果accum的某个值大于1，停止工作，用于taken
                //开启核心焚烧时不停止工作
                if(canProgress){
                    for (float ac : accum){
                        if(ac >= 1f && !state.rules.coreIncinerates){
                            canProgress = false;
                            break;
                        }
                    }
                }


                speedScl = Mathf.lerpDelta(speedScl, canProgress ? 1f : 0f, 0.1f);

                for (Item item : content.items()){
                    //核心对于该物品的剩余空间
                    int taken = Math.min((int) accum[item.id], linkedCore != null ?
                            ((CoreBlock.CoreBuild) linkedCore).storageCapacity - linkedCore.items.get(item) : getMaximumAccepted(item) - items.get(item));
                    if(taken > 0){
                        if(linkedCore == null){
                            items.add(item, taken);
                        } else {
                            linkedCore.items.add(item, taken);
                        }
                        accum[item.id] -= taken;
                    }
                }

                if(progress >= 1f){
                    canProgress = false;
                    boolean checkErrors = true;

                    for (Item item : content.items()){
                        moveItem(deconstructing, item);
                    }

                    for (ItemStack itemStack : reqs){
                        Item item = itemStack.item;
                        //存在误差时，误差物品+1,浮点数tolerance越小,出现误差的可能越小
                        if(Mathf.equal(accum[item.id], 1f, 0.0000001f)){
                            checkErrors = false;
                            if(linkedCore == null && canAcceptItem(item)){
                                items.add(item, 1);
                            } else if(linkedCore != null && linkedCore.items.get(item) < linkedCore.block.itemCapacity){
                                linkedCore.items.add(item, 1);
                            }
                            accum[item.id] = 0;
                        }
                    }


                    if(checkErrors){
                        Fx.breakBlock.at(x, y, deconstructing.size() / tilesize);
                        deconstructing = null;
                    }
                }

                //如果能进行
                if(canProgress){

                    //特效
                    float ey = Mathf.range(size * tilesize / 2f);
                    if(Mathf.chanceDelta(Math.abs(Mathf.cos(time, 20f, 1)))){
                        DeconstructorEffect().at(x + linesin, y + ey, rotation);
                    }
                    //转化值
                    float shift = edelta() * deconstructSpeed / deconstructing.buildTime();
                    float realShift = Math.min(shift, 1f - progress);

                    progress += shift;
                    time += edelta();

                    for (Item item : content.items()){
                        accum[item.id] += reqs.get(item) * (payload instanceof BuildPayload ?
                                Vars.state.rules.buildCostMultiplier : Vars.state.rules.unitCost(team)) * realShift;
                    }
                }
            }

            //输入载荷的开始拆解
            //否则，如果已经输入载荷，并且自身携带的载荷不为无
            else if(moveInPayload(false) && payload != null){
                //将解构载荷设为携带的载荷
                deconstructing = payload;
                //自身携带的载荷设为无
                payload = null;
                progress = 0f;
            }
        }

        //////////////////////////////////////////////////
        public Effect DeconstructorEffect(){
            return new Effect(12, e -> {

                color(Pal.remove);
                randLenVectors(e.id, 1, 16 * e.finpow(), (x, y) -> {
                    Fill.square(e.x + x, e.y + y, 1f + e.fout() * (3f + e.rotation));
                });

            });
        }


        @Override
        public void draw(){
            Font font = Fonts.outline;
            Draw.rect(region, x, y);

            font.draw(Arrays.toString(accum), x, y - 20, Align.center);

            for (int i = 0; i < 4; i++){
                if(blends(i)){
                    Draw.rect(inRegion, x, y, i * 90);
                }
            }

            Draw.z(Layer.blockOver);
            drawPayload();

            if(deconstructing != null){
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

                linesin = Mathf.sin(time, 20f, tilesize / 2f * block.size - 3f);
                Draw.color(Pal.remove);

                Lines.lineAngleCenter(x + linesin, y, 90f, size * tilesize - 6f);
                Draw.reset();

            }
            Draw.z(Layer.blockOver);
            Draw.rect(topRegion, x, y);

            //todo 颜色应当对齐  team和remove
            Draw.color(linkedCore != null ? team.color : Pal.remove);
            Draw.rect(Mathf.mod((rotation), 4) < 2 ? teamRegion1 : teamRegion2, x, y, rotation * 90);

        }

    }
}
