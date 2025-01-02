package Ovulam.entities.bullet;

import Ovulam.entities.OvulamDynamicExplosion;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class OvulamDynamicExplosionBulletType extends BulletType {
    float flammability, explosiveness, radioactivity, charge;
    public OvulamDynamicExplosionBulletType(
            float flammability, float explosiveness,
            float radioactivity, float charge){
        this.flammability = flammability;
        this.explosiveness = explosiveness;
        this.radioactivity = radioactivity;
        this.charge = charge;
        lifetime = 1f;
        speed = 1f;
    }

    public OvulamDynamicExplosionBulletType(){
        this(0,0,0,0);
    }

    public void setAttribute(float flammability, float explosiveness,
                             float radioactivity, float charge){
        this.flammability = flammability;
        this.explosiveness = explosiveness;
        this.radioactivity = radioactivity;
        this.charge = charge;
    }

    @Override
    public void despawned(Bullet b){
        new OvulamDynamicExplosion(b.x, b.y, b.team, flammability, explosiveness, radioactivity, charge);
        super.despawned(b);
    }
}
