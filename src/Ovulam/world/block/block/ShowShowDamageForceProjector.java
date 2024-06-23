package Ovulam.world.block.block;

import arc.func.Cons;
import arc.graphics.Blending;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Intersector;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.gen.Sounds;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.logic.Ranged;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.BlockGroup;
import mindustry.world.meta.Env;

import static mindustry.Vars.*;

public class ShowShowDamageForceProjector extends Block{
    public final int timerUse = timers++;
    //半径
    public float radius = 10170f;
    //边长
    public int sides = 6;

    public Effect absorbEffect = Fx.absorb;

    protected static ForceBuild paramEntity;
    protected static Effect paramEffect;
    protected static final Cons<Bullet> shieldConsumer = bullet -> {
        if(bullet.team != paramEntity.team 
                && bullet.type.absorbable 
                && Intersector.isInRegularPolygon(((ShowShowDamageForceProjector)(paramEntity.block)).sides,
                paramEntity.x, paramEntity.y, paramEntity.realRadius() * 2f, 0, bullet.x, bullet.y)){
            bullet.absorb();
            paramEffect.at(bullet);
            paramEntity.buildup += bullet.damage;
        }
    };

    public ShowShowDamageForceProjector(String name){
        super(name);
        update = true;
        sync = true;
        solid = true;
        group = BlockGroup.projectors;
        hasPower = true;
        hasLiquids = true;
        hasItems = true;
        envEnabled |= Env.space;
        ambientSound = Sounds.shield;
        ambientSoundVolume = 0.08f;
    }

    @Override
    public void init(){
        updateClipRadius(radius + 3f);
        super.init();
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("shield", (ForceBuild entity) -> new Bar(String.valueOf(entity.buildup), Pal.accent, () -> entity.buildup));
    }

    @Override
    public boolean outputsItems(){
        return false;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        //六边形范围，队伍颜色线夹在灰色线中间

        Draw.color(Pal.gray);
        Lines.stroke(3f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, 0);
        Draw.color(player.team().color);
        Lines.stroke(1f);
        Lines.poly(x * tilesize + offset, y * tilesize + offset, sides, radius, 0);
        Draw.color();
    }

    public class ForceBuild extends Building implements Ranged{
        public float buildup, warmup, phaseHeat;

        @Override
        public void buildConfiguration(Table table){
            table.defaults().width(216f);
            table.button("set zero", () -> buildup = 0);
        }


        //实际范围，
        @Override
        public float range(){
            return realRadius();
        }


        @Override
        public void onRemoved(){
            float radius = realRadius();
            if(radius > 1f) Fx.forceShrink.at(x, y, radius, team.color);
            super.onRemoved();
        }

        @Override
        public void pickedUp(){
            super.pickedUp();
            warmup = 0f;
        }

        @Override
        public boolean inFogTo(Team viewer){
            return false;
        }

        @Override
        public void updateTile(){

            //用来“慢慢”增强到布提升的状态，不至于一下子提升
            //慢慢到1，或者慢慢到0
            phaseHeat = 1;

            //提升至效率
            warmup = Mathf.lerpDelta(warmup, efficiency, 0.1f);

            deflectBullets();
        }

        public void deflectBullets(){
            float realRadius = realRadius();

            if(realRadius > 0){
                paramEntity = this;
                paramEffect = absorbEffect;
                Groups.bullet.intersect(x - realRadius, y - realRadius, realRadius * 2f, realRadius * 2f, shieldConsumer);
            }
        }

        public float realRadius(){
            return radius * warmup;
        }

        @Override
        public double sense(LAccess sensor){
            if(sensor == LAccess.heat) return buildup;
            return super.sense(sensor);
        }

        @Override
        public void draw(){
            super.draw();

            if(buildup > 0f){
                Draw.alpha(buildup * 0.75f);
                Draw.z(Layer.blockAdditive);
                Draw.blend(Blending.additive);
                Draw.blend();
                Draw.z(Layer.block);
                Draw.reset();
            }

            drawShield();
        }

        public void drawShield(){
                float radius = realRadius();

                Draw.color(team.color);

                if(renderer.animateShields){
                    Draw.z(Layer.shields + 0.001f);
                    Fill.poly(x, y, sides, radius, 0);
                }else{
                    Draw.z(Layer.shields);
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f));
                    Fill.poly(x, y, sides, radius, 0);
                    Draw.alpha(1f);
                    Lines.poly(x, y, sides, radius, 0);
                    Draw.reset();
                }
            

            Draw.reset();
        }

    }
}
