package Ovulam.No9527垃圾堆;

import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Bullet;
import mindustry.gen.Healthc;
import mindustry.gen.Hitboxc;

public class PerBulletType extends BasicBulletType {
    //百分比, 填写40表示造成40%的血量上限的伤害
    float percent;

    public PerBulletType(float percent){
        this.percent = percent;
    }

    @Override
    public void hitEntity(Bullet b, Hitboxc entity, float health) {
        if(entity instanceof Healthc h){
            h.damagePierce(h.health() * percent / 100);
        }
        super.hitEntity(b, entity, health);
    }
}
