package Ovulam.world.block.defense;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.Stat;

import static mindustry.Vars.renderer;
import static mindustry.Vars.tilesize;

public class AerialExclusionWall extends Wall {
    public float shieldHealth = 3000f;
    public float breakCooldown = 60f * 10f;
    public float regenSpeed = 2f;

    public AerialExclusionWall(String name) {
        super(name);
        update = true;
        sync = true;
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.shieldHealth, shieldHealth);
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("shield", (AerialExclusionWallBuild entity) -> new Bar(
                entity.getShield() + "/" + shieldHealth,
                entity.broken() ? Pal.darkerGray : Pal.accent,
                entity::getShieldProgress)
        );
    }

    public class AerialExclusionWallBuild extends WallBuild{
        public float shield = shieldHealth, shieldRadius = 0f;
        public float breakTimer;

        public float getShieldProgress(){
            return broken() ? 1 - breakTimer / breakCooldown : shield / shieldHealth;
        }

        public float getShield(){
            return broken() ? (1 - breakTimer / breakCooldown) * shieldHealth : shield;
        }

        public float radius(){
            return shieldRadius * tilesize * size / 2f;
        }

        @Override
        public void draw(){
            Draw.rect(block.region, x, y);

            if(shieldRadius > 0){
                float radius = shieldRadius * tilesize * size / 2f;

                Draw.z(Layer.shields);

                Draw.color(team.color, Color.white, Mathf.clamp(hit));

                if(renderer.animateShields){
                    Fill.square(x, y, radius);
                }else{
                    Lines.stroke(1.5f);
                    Draw.alpha(0.09f + Mathf.clamp(0.08f * hit));
                    Fill.square(x, y, radius);
                    Draw.alpha(1f);
                    Lines.poly(x, y, 4, radius, 45f);
                    Draw.reset();
                }

                Draw.reset();
            }
        }

        @Override
        public void updateTile(){
            if(breakTimer > 0){
                breakTimer -= Time.delta;
            }else{
                //regen when not broken
                shield = Mathf.clamp(shield + regenSpeed * edelta(), 0f, shieldHealth);
            }

            //todo hit
            if(hit > 0){
                hit -= Time.delta / 10f;
                hit = Math.max(hit, 0f);
            }

            shieldRadius = Mathf.lerpDelta(shieldRadius, broken() ? 0f : 1f, 0.12f);

            if(!broken()){
                deflectBullets();
                exclusionAirs();
            }
        }

        public void deflectBullets(){
            float radius = radius();
            Groups.bullet.intersect(
                    x - radius, y - radius, radius * 2, radius * 2, bullet -> {
                if(bullet.team != team && (bullet.type.collidesAir) && bullet.type.absorbable){
                    bullet.absorb();
                    float shieldTaken = broken() ? 0f : Math.min(shield, bullet.damage);

                    shield -= shieldTaken;
                    if(shieldTaken > 0){
                        hit = 1f;
                    }

                    if(shield <= 0.00001f && shieldTaken > 0){
                        breakTimer = breakCooldown;
                    }
                }
            });
        }

        public void exclusionAirs(){
            float radius = radius();
            Groups.unit.intersect(
                    x - radius, y - radius, radius * 2, radius * 2, unit -> {
                if(unit.team != team && unit.isFlying()){
                    float vel = unit.vel.len();
                    float volume = Mathf.pow(unit.type.hitSize, 2);
                    float momentum = volume * unit.vel.len();

                    if(shield >= momentum){
                        shield -= momentum;
                        unit.vel.setZero();
                    }else {
                        breakTimer = breakCooldown;
                        //todo 乘算
                        unit.vel.setLength(vel - shield / volume);
                        shield = 0f;
                    }
                }
            });
        }



        public boolean broken(){
            return breakTimer > 0 || !canConsume();
        }

    }
}
