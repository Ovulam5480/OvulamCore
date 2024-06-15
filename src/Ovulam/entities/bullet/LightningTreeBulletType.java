package Ovulam.entities.bullet;

import Ovulam.entities.LightningTree;
import arc.graphics.Color;
import mindustry.content.Fx;
import mindustry.content.StatusEffects;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;

public class LightningTreeBulletType extends BulletType {
    public float lightningRange = 120f;
    public int amount = 8;
    public Color lightningRoot = Color.pink;
    public Color lightningLeaf = Color.cyan;
    public LightningTreeBulletType(){
        damage = 1f;
        speed = 0f;
        lifetime = 1;
        despawnEffect = Fx.none;
        hitEffect = Fx.hitLancer;
        keepVelocity = false;
        hittable = false;
        status = StatusEffects.shocked;
    }

    @Override
    protected float calculateRange(){
        return lightningRange;
    }

    @Override
    public float estimateDPS(){
        return damage * lightningRange / 10 * (amount + amount * 0.5f);
    }

    @Override
    public void draw(Bullet b){
    }

    @Override
    public void init(Bullet b){
        LightningTree.create(b, lightningRoot, lightningLeaf, damage, amount, lightningRange);
    }
}
