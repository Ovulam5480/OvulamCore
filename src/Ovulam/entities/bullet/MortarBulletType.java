package Ovulam.entities.bullet;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
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

import static mindustry.Vars.tilesize;

//该子弹的寿命是固定的
public class MortarBulletType extends BulletType {
    public float bulletRange;
    //炮弹在Y轴的位移倍率
    public float offsideMultiplier = 1f;
    //炮弹的贴图缩放倍率
    public float sclMultiplier = 1f;
    //子弹旋转速度倍率
    public float rotateMultiplier = 1f;
    //阴影的X轴的位移倍率
    public float shadowOffsideMultiplier = 2f;
    //是否有图标贴图
    public boolean hasIcon = false;
    //是否有推进器贴图
    public boolean hasThrusters = false;

    public TextureRegion podBulletRegion, podBulletIconRegion, podBulletThrustersRegion;

    public Effect podExplosion = new Effect(300, e -> {
        Draw.color(Pal.accent);
        Lines.stroke(10f * e.foutpow());
        Lines.circle(e.x, e.y, e.rotation * e.finpow());
    });

    public ObjectMap<Bullet, Float> rotations = new ObjectMap<>();
    public ObjectMap<Bullet, Vec2> beginning = new ObjectMap<>();

    private final String blockName;

    public MortarBulletType(Block block, float bulletRange){
        //用于贴图
        this.blockName = block.name;
        hitEffect = Fx.none;
        despawnEffect = Fx.none;
        hittable = false;
        reflectable = false;
        despawnHit = true;
        collidesTiles = false;
        //总不能给影子写一个实体吧
        this.bulletRange = drawSize = bulletRange;
    }

    @Override
    public void init(Bullet b){

        rotations.put(b, 0f);
        beginning.put(b, new Vec2(b.x, b.y));

        float angle = Mathf.angle(b.aimX - b.x,b.aimY - b.y);

        Tmp.v1.trns(angle, Math.min(bulletRange, Mathf.dst(b.x, b.y, b.aimX, b.aimY)));

        b.aimX = b.x + Tmp.v1.x;
        b.aimY = b.y + Tmp.v1.y;

        float speed = Mathf.dst(b.x, b.y, b.aimX, b.aimY) / lifetime;

        b.initVel(angle, speed);
        super.init(b);
    }

    @Override
    public void load(){
        super.load();
        podBulletRegion = Core.atlas.find(blockName + "-pod");
        if(hasIcon)podBulletIconRegion = Core.atlas.find(blockName + "-pod-icon");
        if(hasThrusters)podBulletThrustersRegion = Core.atlas.find(blockName + "-pod-thrusters");
    }

    //子弹的进度函数, 确保这个函数在0到1的积分为0
    public float heightProgress(Bullet b){
        return progress(b) - 1/3f;
    }

    public float progress(Bullet b){
        //return b.fin() - 1/2f;
        return Mathf.sqr(b.fin());
    }

    //假定炮弹高度的二次函数, 用于贴图缩放
    public float heightScl(float progress){
        //0到1到0, 开口朝下的二次函数
        return (-Mathf.sqr(progress) + progress) * 4;
    }

    //子弹的贴图真的不应该这么大
    @Override
    public void draw(Bullet b){
        Drawf.dashLine(Color.pink, b.x, b.y, b.aimX, b.aimY);

        float sin = 0.95f + Mathf.absin(2f, 0.1f);
        float progress = progress(b);
        float height = heightScl(progress);

        float rotation = rotations.get(b);

        //使"更高"的炮弹, 显示在更高的图层
        Draw.z(Layer.playerName + height);

        float scl = 1 + height * sclMultiplier;
        Draw.scl(scl);

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
        if(hasIcon) Draw.rect(podBulletIconRegion, b.x, b.y, rotation);
        if(hasThrusters) Drawf.spinSprite(podBulletThrustersRegion, b.x, b.y, rotation);

        Draw.scl(scl * 1.3f);
        drawShadow(b, b.fin(), rotation);

        Draw.scl();
        Draw.reset();
    }

    public void drawShadow(Bullet b, float progress, float rotation){
        Tmp.v2.set(beginning.get(b)).lerp(b.aimX, b.aimY, progress);
        float distance = Math.abs(b.y - Tmp.v2.y) * shadowOffsideMultiplier;

        float sx = Tmp.v2.x - distance;
        float sy = Tmp.v2.y - distance * 0.6f;

        Draw.z(Layer.blockUnder);

        Drawf.shadow(podBulletRegion, sx, sy, rotation);
        if(hasThrusters) Drawf.shadow(podBulletThrustersRegion, sx, sy, rotation);
    }

    @Override
    public void update(Bullet b){
        float progress = progress(b);
        rotations.put(b, rotations.get(b) + (1f + heightScl(progress)) * rotateMultiplier * Time.delta);

        float moveDelta = -offsideMultiplier * heightProgress(b) * Time.delta;
        b.move(0f , moveDelta);

        super.update(b);
    }


    @Override
    public void despawned(Bullet b){
        //还是直接写伤害好用，什么B碰撞
        rotations.remove(b);
        beginning.remove(b);

        Damage.damage(b.team, b.x, b.y, b.hitSize, b.damage);
        podExplosion.at(b.x, b.y, Mathf.sqrt(b.hitSize) * tilesize);

        super.despawned(b);
    }
}