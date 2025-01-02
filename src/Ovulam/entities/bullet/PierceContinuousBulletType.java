package Ovulam.entities.bullet;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import mindustry.entities.Damage;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Layer;

public class PierceContinuousBulletType extends BulletType {
    public float damageInterval = 5f;

    //todo heal治疗方块
    //todo 目标位置有方块时，炮台无法发射
    public PierceContinuousBulletType(){
        pierce = true;
        collides = false;
        hitSize = 16;

        trailLength = 32;
    }
    
    public float damageRadius(){
        return Mathf.sqrt(hitSize) / 2f;
    }

    public void init(){
        trailWidth = damageRadius();
        super.init();
    }

    public void init(Bullet b){
        super.init(b);
    }

    public float continuousDamage(){
        return damage / damageInterval * 60f;
    }

    @Override
    public float estimateDPS(){
        if(!pierce) return -1f;
        return damage / damageInterval / 2;
    }

    @Override
    public void draw(Bullet b){
        super.draw(b);
        Draw.z(Layer.bullet);
        Fill.circle(b.x, b.y, damageRadius());
    }

    @Override
    public void update(Bullet b){
        super.update(b);

        if(b.timer.get(0, damageInterval)){
            Damage.damage(b.team, b.x, b.y, damageRadius(), damage * b.fin(), true, collidesAir, collidesGround, scaledSplashDamage, b);
        }
    }
}
