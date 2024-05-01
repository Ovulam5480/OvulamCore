package Ovulam.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static arc.graphics.g2d.Draw.scl;
import static mindustry.Vars.tilesize;

public class MortarBulletType extends BulletType {
    private float rotation;

    public String name;
    public float height;
    public float offsideMultiplier = 1f;

    public TextureRegion podBulletRegion, podBulletIconRegion, podBulletThrustersRegion;

    public MortarBulletType(Block block){
        this.name = block.name;

        fragBullet = new OvulamDynamicExplosionBulletType(0 , 0 , 0 , 0);
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        fragBullets = 1;
        hittable = false;
        reflectable = false;
        despawnHit = true;
        collidesTiles = false;
    }

    @Override
    public void init(Bullet b){
        float speed = Mathf.dst(b.x, b.y, b.aimX, b.aimY) / lifetime;
        float angle = Mathf.angle(b.aimX - b.x,b.aimY - b.y);

        b.initVel(angle, speed);
        super.init(b);
    }

    @Override
    public void load(){
        super.load();
        if(podBulletRegion == null)podBulletRegion = Core.atlas.find(name + "-pod");
        if(podBulletIconRegion == null)podBulletIconRegion = Core.atlas.find(name + "-pod-icon");
        if(podBulletThrustersRegion == null)podBulletThrustersRegion = Core.atlas.find(name + "-pod-thrusters");
    }

    public float progress(Bullet b){
        return Mathf.sqr(b.time / lifetime);
    }

    //代表单位高度的二次函数
    public float aFloat(float progress){
        return (-Mathf.sqr(progress) + progress) * height;
    }

    //子弹的贴图真的不应该这么大
    @Override
    public void draw(Bullet b){
        float progress = progress(b);
        float sin = 0.95f + Mathf.absin(2f, 0.1f);

        Draw.reset();

        Draw.z(Layer.playerName + height);

        float scl = 1 + height * aFloat(progress);
        scl(scl);

        for (int i = 0; i < 4; i++){
            Tmp.v1.trns(i * 90 + rotation, 1f);

            Tmp.v1.setLength(((Mathf.sqrt(hitSize) * 1.5f) * tilesize / 2f) * scl);
            Draw.color(b.team.color);
            Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, 2f * Mathf.sqrt(hitSize) * scl * sin);

            Tmp.v1.setLength((((Mathf.sqrt(hitSize) * 1.4f) * tilesize / 2f) * scl));
            Draw.color(Color.white);
            Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, 1.3f * Mathf.sqrt(hitSize) * scl * sin);
        }

        Drawf.spinSprite(podBulletRegion, b.x, b.y, rotation);
        if(podBulletThrustersRegion.found()) Drawf.spinSprite(podBulletThrustersRegion, b.x, b.y, rotation);
        if(podBulletIconRegion.found()) Draw.rect(podBulletIconRegion, b.x, b.y, rotation);

        Draw.reset();
    }

    public Effect podExplosion = new Effect(300, e -> {
        Draw.color(Pal.accent);
        Lines.stroke(10f * e.foutpow());
        Lines.circle(e.x, e.y, e.rotation * e.finpow());
    });

    @Override
    public void update(Bullet b){
        float progress = progress(b);
        rotation += progress + aFloat(progress) * Time.delta * (2f + Mathf.randomSeedRange(b.id(), 1f));

        //效果并不好，改天再写个
        b.move(0.5f * (progress - 0.33333333f) * Time.delta, -offsideMultiplier * (progress - 0.33333333f) * height * Time.delta);

        super.update(b);
    }


    @Override
    public void despawned(Bullet b){
        //还是直接写伤害好用，什么B碰撞
        Damage.damage(b.team, b.x, b.y, b.hitSize, b.damage);
        podExplosion.at(b.x, b.y, Mathf.sqrt(b.hitSize) * tilesize);

        super.despawned(b);
    }
}