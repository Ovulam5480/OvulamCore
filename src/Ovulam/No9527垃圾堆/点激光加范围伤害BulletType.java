package Ovulam.No9527垃圾堆;

import mindustry.entities.bullet.PointLaserBulletType;
import mindustry.gen.Bullet;

public class 点激光加范围伤害BulletType extends PointLaserBulletType {
    public 点激光加范围伤害BulletType(){
        splashDamage = 30;
        splashDamageRadius = 30f;
    }

    @Override
    public void update(Bullet b){
        if(b.timer.get(0, damageInterval)){
            createSplashDamage(b, b.aimX, b.aimY);
            despawnEffect.at(b.x, b.y, b.rotation(), hitColor);
        }
        super.update(b);
    }
}
