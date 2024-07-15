package Ovulam.No9527垃圾堆;

import arc.func.Cons2;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Buildingc;
import mindustry.gen.Bullet;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.Ranged;
import mindustry.world.Block;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;

import static mindustry.Vars.*;

public class NH的LaserWallBlock extends Block{
    public float range = 480f;
    public float warmupSpeed = 0.075f;
    public float minActivate = 0.3f;
    public Shooter generateType = new Shooter(100f); //Should be continuous.
    public static Color shootColor = Pal.accent;
    public static float sinScl = 1f;
    public static float widthMultiplier = 1f;

    public NH的LaserWallBlock(String name){
        super(name);
        config(Integer.class, (Cons2<NH的LaserWallBuild, Integer>)Linkablec::linkPos);

        update = true;
        configurable = true;
        solid = true;
        hasPower = true;
        hasShadow = true;

        ambientSound = loopSound = Sounds.pulse;
        consumePowerCond(30f, NH的LaserWallBuild::canActivate);
    }

    @Override
    public void init(){
        super.init();

        generateType.drawSize = Math.max(generateType.drawSize, range * 2);
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.damage, generateType.estimateDPS(), StatUnit.perSecond);
        stats.add(Stat.range, (int)(range / tilesize), StatUnit.blocks);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        float xB = x * tilesize + offset, yB = y * tilesize + offset;

        Color color = Vars.player.team().color;

        Drawf.dashCircle(xB, yB, range, color);

        if(!control.input.config.isShown()) return;
        Building selected = control.input.config.getSelected();
        if(selected == null || !(selected.block instanceof NH的LaserWallBlock) || !(selected.within(xB, yB, range))) return;

        float sin = Mathf.absin(Time.time, 6f, 1f);
        Tmp.v1.set(xB, yB).sub(selected.x, selected.y).limit((size / 2f + 1) * tilesize + sin + 0.5f);
        float x2 = xB - Tmp.v1.x, y2 = yB - Tmp.v1.y,
                x1 = selected.x + Tmp.v1.x, y1 = selected.y + Tmp.v1.y;
        int segs = (int)(selected.dst(xB, yB) / tilesize);

        Drawf.select(x2, y2, size * tilesize / 2f + 2f, Pal.accent);

        Lines.stroke(4f, Pal.gray);

        Lines.dashLine(x1, y1, x2, y2, segs);
        Lines.stroke(2f, color);
        Lines.dashLine(x1, y1, x2, y2, segs);

        Draw.reset();
    }

    public class NH的LaserWallBuild extends Building implements Linkablec{
        protected transient NH的LaserWallBuild target;
        protected int linkPos = -1;
        protected Bullet shooter;

        public float warmup;

        @Override
        public void updateTile(){
            if(!linkValid()){
                target = null;
                linkPos = -1;
            }

            if(power.status > 0.5f && canActivate())warmup = Mathf.lerpDelta(warmup, 1, warmupSpeed);
            else                                    warmup = Mathf.lerpDelta(warmup, 0, warmupSpeed);

            if(warmup > minActivate && canActivate()){
                if(shooter == null)shooter = generateType.create(this, x, y, angleTo(target));
                shooter.data(target);
                shooter.damage = generateType.damage * warmup;
                shooter.time(0);
            }else shooter = null;

            if(shooter != null)shooter.fdata = warmup;
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if (this == other || linkPos() == other.pos()) {
                configure(-1);
                return false;
            }
            if (other.within(this, range())) {
                configure(other.pos());
                return false;
            }
            return true;
        }



        @Override
        public void drawConfigure(){
            Drawf.select(x, y, tile.block().size * tilesize / 2f + 2f, Pal.accent);

            if(target != null){
                Drawf.square(target.x, target.y, target.block.size * tilesize / 2f, Pal.accent);
            }
        }

        @Override
        public void read(Reads read, byte revision){
            linkPos = read.i();
            warmup = read.f();
        }

        @Override
        public void write(Writes write){
            write.i(linkPos);
            write.f(warmup);
        }

        @Override
        public boolean linkValid(Building b){
            return b instanceof NH的LaserWallBuild && b.team == team && b.isValid() && ((NH的LaserWallBuild)b).link() != this;
        }

        public boolean canActivate(){return target != null;}

        @Override
        public Building link(){
            return target;
        }

        @Override
        public int linkPos(){
            return linkPos;
        }

        @Override
        public void linkPos(int value){
            linkPos = value;
            if(linkValid(world.build(linkPos))){
                target = (NH的LaserWallBuild) world.build(linkPos);
            }else{
                target = null;
                linkPos = -1;
            }
        }

        @Override
        public float range(){
            return range;
        }
    }

    public static class Shooter extends BulletType{
        public float width = 6f, oscScl = 1.25f, oscMag = 0.85f;

        public Shooter(float damage){
            super(0, damage);

            hitEffect = Fx.hitBeam;
            despawnEffect = Fx.none;
            hitSize = 4;
            drawSize = 420f;
            lifetime = 36f;

            incendAmount = 3;
            incendSpread = 8;
            incendChance = 0.6f;
            hitColor = lightColor =  shootColor;
            impact = true;
            keepVelocity = false;
            collides = false;
            pierce = true;
            hittable = false;
            absorbable = false;

            status = StatusEffects.shocked;
            statusDuration = 300f;

            hitShake = 0.25f;
        }

        @Override
        public float estimateDPS(){
            return damage * 100f / 5f * 3f;
        }

        @Override
        public void update(Bullet b){
            if(!(b.data instanceof Building))return;
            Building build = (Building)b.data();

            //damage every 5 ticks
            if(b.timer(1, 5f)){
                Damage.collideLine(b, b.team, hitEffect, b.x, b.y, b.rotation(), b.dst(build), true, false);
            }

            if(hitShake > 0){
                Effect.shake(hitShake, hitShake, b);
            }
        }

        @Override
        public void draw(Bullet b){
            if(!(b.data instanceof NH的LaserWallBuild))return;
            NH的LaserWallBuild build = (NH的LaserWallBuild)b.data();

            Draw.color(Tmp.c1.set(shootColor).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));
            Draw.z(Layer.bullet);
            Lines.stroke((width + Mathf.absin(Time.time, oscScl, oscMag)) * b.fdata * b.fout() * widthMultiplier);
            Lines.line(b.x, b.y, build.x, build.y, false);

            Draw.z(Layer.bullet + 0.1f);
            Fill.circle(b.x, b.y, Lines.getStroke() * 0.75f);
            Fill.circle(build.x, build.y, Lines.getStroke() * 0.75f);

            Drawf.light(b.x, b.y, build.x, build.y, width * width * 1.5f, lightColor, 0.7f);
            Draw.reset();
        }

        @Override
        public void drawLight(Bullet b){}
    }

    public interface Linkablec extends Buildingc, Ranged {

        @Override default boolean onConfigureBuildTapped(Building other){
            if (this == other || linkPos() == other.pos()) {
                configure(Tmp.p1.set(-1, -1));
                return false;
            }
            if (other.within(this, range()) && other.team == team()) {
                configure(Point2.unpack(other.pos()));
                return false;
            }
            return true;
        }

        default Building link(){return world.build(linkPos()); }
        default boolean linkValid(){ return linkValid(link()); }
        default boolean linkValid(Building b){ return b != null; }

        int linkPos();
        void linkPos(int value);
    }
}
