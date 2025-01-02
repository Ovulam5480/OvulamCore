package Ovulam.world.block.defense;

import Ovulam.entities.bullet.MortarBulletType;
import Ovulam.entities.bullet.OvulamDynamicExplosionBulletType;
import Ovulam.world.block.payload.MultiPayloadBlock;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.*;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.blocks.ControlBlock;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;

import static arc.math.Angles.randLenVectors;
import static mindustry.Vars.tilesize;

public class Mortar extends MultiPayloadBlock {
    //炮弹持续时间
    public float totalTime = 600f;
    public float minLaunch = 16;
    public float range = 9999;
    public TextureRegion region, arrowRegion, topRegion, topLightRegion, iconRegion, podThrustersRegion;
    public TextureRegion podRegion;
    public TextureRegion podIconRegion;
    public int podSize = 8;
    public float payloadCapacity = 64f;
    //集齐物资后的准备发射时间
    public float launchTime = 100f;

    public Mortar(String name){
        super(name);
        size = 15;
        rotate = false;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        arrowRegion = Core.atlas.find(name + "-arrow");
        topRegion = Core.atlas.find(name + "-top");
        topLightRegion = Core.atlas.find(name + "-top-light");
        iconRegion = Core.atlas.find(name + "-icon");
        podRegion = Core.atlas.find(name + "-pod");
        podIconRegion = Core.atlas.find(name + "-pod-icon");
        podThrustersRegion = Core.atlas.find(name + "-pod-thrusters");
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("ammo", (MortarBuild e) -> new Bar(
                "stat.ammo",
                Pal.ammo,
                () -> e.payloadUsed() / payloadCapacity
        ));

        addBar("111", (MortarBuild e) -> new Bar(
                "111111",
                Pal.ammo,
                () -> e.launchCounter / launchTime
        ));
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{iconRegion};
    }


    ///////////////////////////

    public class MortarBuild extends MultiPayloadBlockBuild implements ControlBlock {
        public BlockUnitc unit = (BlockUnitc) UnitTypes.block.create(team);
        public float launchCounter;
        public float flammability, explosiveness, radioactivity, charge, health;
        public float podUsed;

        @Override
        public Unit unit(){
            unit.tile(this);
            unit.team(team);
            return (Unit) unit;
        }


        @Override
        public boolean acceptUnitPayload(Unit unit){
            return false;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return payloadUsed() + Mathf.sqr(payload.size() / 8) <= payloadCapacity
                    && positionPayloads.size <= payloadAmountCapacity && launchCounter == 0;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            super.handlePayload(source, payload);
        }

        @Override
        public void draw(){
            Draw.reset();

            Draw.rect(region, x, y);
            Draw.rect(podRegion, x, y);
            Draw.rect(podIconRegion, x, y);

            Draw.mixcol(launchCounter > 0 ? Pal.remove : team.color, Mathf.absin(2f, 1f));
            Draw.rect(arrowRegion, x, y);

            Draw.reset();

            Draw.z(Layer.blockOver);
            drawPayload();

            //我想用buildBeam的。但是！没法解决图层太高的问题！保证顶部贴图覆盖就会让飞行单位子弹都穿过去！

            //现在这个效果并不好，太怪了
            /*
            if(launchCounter > 0){
                Lines.stroke(8f + Mathf.absin(2f, 2f),
                        Tmp.c3.set(Pal.remove).a(0.5f + Mathf.absin(2f, 0.25f)));
                Lines.rect(x-size*4,y-size*4,size*8,size*8);
            }
             */

            Draw.reset();
            Draw.z(Layer.blockOver + 1f);

            Draw.rect(topRegion, x, y);

            Draw.color(Color.valueOf("#eab678"));
            Draw.alpha(launchCounter * 0.65f / launchTime);
            Draw.rect(topLightRegion, x, y);

            Draw.reset();

        }

        @Override
        public float payloadUsed(){
            return positionPayloads.sumf(p -> Mathf.sqr(p.payload.size() / tilesize)) + podUsed;
        }

        //todo 特效
        //红色 爆炸性 冲击爆炸粒子。根据四个基础性质的最大值改变颜色
        //黄色 放电性 大量闪电特效
        //橘色 燃烧性 没有特效，大量火球子弹坠落？
        //紫色 放射性 ？？？ BUFF ？？？


        @Override
        public void updateTile(){
            positionPayloads.each(payload -> {
                Building build = ((BuildPayload) (payload.payload)).build;
                if(hasArrived(payload)){
                    health += build.maxHealth;
                    if(build.block.consPower != null && build.block.consPower.buffered){
                        charge += build.power.status * build.block.consPower.capacity / 80f;
                    }
                    if(build.block.hasItems){
                        for (Item item : Vars.content.items()){
                            flammability += item.flammability * build.items.get(item);
                            explosiveness += item.explosiveness * build.items.get(item);
                            radioactivity += item.radioactivity * build.items.get(item);
                            charge += item.charge * build.items.get(item);
                        }
                    }
                    podUsed += Mathf.sqr(payload.payload.size() / tilesize);
                    //感觉得改改
                    Fx.placeBlock.at(payload.x(this), payload.y(this), build.block.size);
                    Fx.payloadReceive.at(payload.x(this), payload.y(this));
                    positionPayloads.remove(payload);
                } else if(launchCounter >= launchTime){ //通常不会用到这个
                    Fx.breakBlock.at(payload.x(this), payload.y(this), build.block.size);
                    positionPayloads.remove(payload);
                } else movePayloads(payload);
            });

            Teamc target = Units.closestTarget(team, x, y, range);

            if(launchCounter < launchTime){
                if(((payloadUsed() >= minLaunch && positionPayloads.size == 0) || payloadUsed() >= payloadCapacity)
                        && target != null){
                    launchCounter += Time.delta;
                }
                return;
            }

            ///////////

            mortarPodLaunch.at(x, y, rotation, this);

            float angle = Mathf.angle(target.x() - x, target.y() - y);

            MortarBulletType pod = new MortarBulletType(block, range){{
                damage = health;
                fragBullet = new OvulamDynamicExplosionBulletType(flammability, explosiveness, radioactivity, charge);
                offsideMultiplier = 4f;
                shadowOffsideMultiplier = 2f;
                hasIcon = hasThrusters = true;
                hitSize = Mathf.sqr(podSize);
                lifetime = totalTime;
                podBulletRegion = podRegion;
                podBulletIconRegion = podIconRegion;
                podBulletThrustersRegion = podThrustersRegion;
            }};

            pod.create(this, team, x, y, angle, 1f, 1f, 1f, null, null, target.x(), target.y());

            flammability = explosiveness = radioactivity = charge = health = podUsed = launchCounter = 0;
        }

        public Effect mortarPodLaunch = new Effect(150, e -> {
            Draw.color(Pal.accent);
            Lines.stroke(10f * e.fout());
            Lines.circle(e.x, e.y, e.finpow() * 120);

            Lines.stroke(9f * e.fout());
            //虽然不是我想要的效果，但是这个效果更好哎
            randLenVectors(e.id, 120, 120f, (x, y) -> {
                float angle = Mathf.angle(x, y);
                Lines.lineAngle((float) (e.x + e.fin() * x + 40f * Math.sin(angle)),
                        (float) (e.y + e.fin() * y + 40f * Math.cos(angle)),
                        angle, e.fout() * e.finpow() * 10f);
            });
        });
    }
}
