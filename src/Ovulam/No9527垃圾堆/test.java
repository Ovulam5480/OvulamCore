package Ovulam.No9527垃圾堆;

import mindustry.ai.types.BuilderAI;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

public class test {
    public static class 武器1 extends Weapon {
        public 武器1(
                String 贴图, float 射速, float 武器角度, float X横, float Y竖,
                float 伤害, float 子弹速度, float 子弹存在时间,
                float 子弹宽, float 子弹长
        ) {
            {
                rotate = mirror = top = false;
                reload = 射速;
                x = X横;
                y = Y竖;
                baseRotation = 武器角度;
                shootCone = 90f;

                shoot = new ShootAlternate(){{
                    barrels = shots = 10;
                    spread = 3f;
                }};
                bullet = new BasicBulletType() {{
                    speed=子弹速度;
                    damage=伤害;
                    width = 子弹宽;
                    height = 子弹长;
                    lifetime = 子弹存在时间;
                    sprite =贴图;
                    shrinkY = 0;
                    shootEffect = Fx.shootSmall;
                    smokeEffect = Fx.shootSmallSmoke;
                    buildingDamageMultiplier = 0.01f;
                }};
            }
        }
    }
    {
        new UnitType("飞机"){{
            aiController = BuilderAI::new;
            isEnemy = false;
            constructor = UnitTypes.gamma.constructor;
            lowAltitude = true;
            flying = true;
            mineSpeed = 8f;
            mineTier = 2;
            buildSpeed = 1f;
            drag = 0.05f;
            speed = 3.55f;
            rotateSpeed = 19f;
            accel = 0.11f;
            fogRadius = 0f;
            itemCapacity = 70;
            health = 220f;
            engineOffset = 6f;
            hitSize = 11f;

            weapons.add(new 武器1(//所有子弹垂直，不倾斜
                    "bullet",8,0,0,20,20,
                    15,40,12,30
            ));
            weapons.add(new 武器1(//所有子弹垂直，不倾斜
                    "bullet",8,0,0,20,20,
                    15,40,12,30
            ));
            weapons.add(new 武器1(//所有子弹垂直，不倾斜
                    "bullet",8,0,12,0,8,
                    15,40,8,30
            ));
            weapons.add(new 武器1(//所有子弹垂直，不倾斜
                    "bullet",8,0,-12,0,8,
                    15,40,8,30
            ));
    }};
    }}

