package Ovulam.type.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.*;
import arc.math.Mathf;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Fonts;

import static arc.graphics.g2d.Draw.scl;
import static mindustry.Vars.tilesize;

public class MortarBulletType extends BulletType {
    public float rotation;
    public String name;
    public float height;

    public TextureRegion podRegion, podIconRegion, podThrustersRegion;

    public MortarBulletType(String name){
        this.name = name;

        fragBullet = new OvulamDynamicExplosionBulletType(0 , 0 , 0 , 0);
        fragBullets = 1;
        hittable = false;
        reflectable = false;
        absorbable = false;
        despawnHit = true;
        collidesTiles = false;
    }

    public MortarBulletType(){
        this("b");
    }


    @Override
    public void load(){
        super.load();

        podRegion = Core.atlas.find(name + "-pod");
        podIconRegion = Core.atlas.find(name + "-pod-icon");
        podThrustersRegion = Core.atlas.find(name + "-pod-thrusters");
    }

    public float progress(Bullet b){
        return Mathf.sqr(b.time / lifetime);
    }

    //代表单位高度的二次函数
    public float aFloat(float progress){
        return (-Mathf.sqr(progress) + progress) * 4;
    }

    //子弹的贴图真的不应该这么大
    @Override
    public void draw(Bullet b){
        float progress = progress(b);
        float sin = 0.95f + Mathf.absin(2f, 0.1f);

        Draw.reset();

        Draw.z(Layer.playerName + 1f);

        float scl = 1 + 2 * aFloat(progress);
        scl(scl);

        if(podRegion != null) Drawf.spinSprite(podRegion, b.x, b.y, rotation);
        if(podThrustersRegion != null) Drawf.spinSprite(podThrustersRegion, b.x, b.y, rotation);
        if(podIconRegion != null) Draw.rect(podIconRegion, b.x, b.y, rotation);

        Draw.z(Layer.playerName + 1f);
        for (int i = 0; i < 4; i++){
            Tmp.v1.trns(i * 90 + rotation, 1f);

            Tmp.v1.setLength(((Mathf.sqrt(hitSize) + 4f) * tilesize / 2f) * scl);
            Draw.color(b.team.color);
            Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, 2f * Mathf.sqrt(hitSize) * scl * sin);

            Tmp.v1.setLength((((Mathf.sqrt(hitSize) + 3f) * tilesize / 2f) * scl));
            Draw.color(Color.white);
            Fill.circle(Tmp.v1.x + b.x, Tmp.v1.y + b.y, 1.3f * Mathf.sqrt(hitSize) * scl * sin);
        }

        Font font = Fonts.outline;
        font.draw(String.valueOf(name), b.x, b.y - 20, Align.center);

        Draw.reset();
    }

    public Effect highlight = new Effect(120, e -> {
        Draw.color(Pal.accent);
        Draw.alpha(e.fout());
        Lines.circle(e.x, e.y, e.rotation);
    });

    public Effect podExplosion = new Effect(300, e -> {

        Draw.color(Pal.accent);
        Lines.stroke(10f * e.foutpow());
        Lines.circle(e.x, e.y, e.rotation * e.finpow());
    });

    @Override
    public void update(Bullet b){
        float progress = progress(b);
        rotation += progress + aFloat(progress) * Time.delta * (2f + Mathf.randomSeedRange(b.id(), 1f));

        //move控制变速
        //幸亏我高数没全忘了
        //效果并不好，改天再写个
        b.move((progress - 0.33333333f) * Time.delta, (progress - 0.33333333f) * (-1) * height * Time.delta);

        super.update(b);
    }


    @Override
    public void despawned(Bullet b){
        //还是直接写伤害好用，什么B碰撞
        Damage.damage(b.team, b.x, b.y, b.hitSize, b.damage);
        super.despawned(b);
    }
}