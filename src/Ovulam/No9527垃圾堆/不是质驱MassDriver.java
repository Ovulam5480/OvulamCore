package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.distribution.MassDriver;

import static arc.graphics.g2d.Draw.color;
import static arc.graphics.g2d.Lines.stroke;

public class 不是质驱MassDriver extends MassDriver {
    public float fadeTime = 16f;
    public float lightStroke = 40f;
    public int divisions = 13;
    public Color[] colors = new Color[]{Pal.heal.cpy().a(.2f), Pal.heal.cpy().a(.5f), Pal.heal.cpy().mul(1.2f), Color.white};
    public float strokeFrom = 2f, strokeTo = 0.5f, pointyScaling = 0.75f;
    public float backLength = 7f, frontLength = 35f;
    public float width = 9f, oscScl = 0.8f, oscMag = 1.5f;

    public 不是质驱MassDriver(String name) {
        super(name);
        //最少发射物品数量
        minDistribute = 1;
        //子弹速度
        bulletSpeed = 5f;
        //质驱装填时间
        reload = 5f;
        itemCapacity = 120;
        range = 440f;

        bullet = new MassDriverBolt() {
            {
                intervalBullets = 1;
                //特效和闪电出现间隔
                bulletInterval = 5f;

                pierce = true;
                //穿透数量
                pierceCap = Integer.MAX_VALUE;

                intervalBullet = new BasicBulletType(3f, 35) {{
                    lifetime = 1f;
                    //特效
                    despawnEffect = new Effect(40f, e -> {
                        color(Pal.heal);
                        stroke(e.fout() * 2f);
                        float circleRad = 4f + e.fin() * 65f;
                        Lines.circle(e.x, e.y, circleRad);
                    });
                    //闪电伤害
                    lightningDamage = 0;
                    lightning = 5;
                    lightningLength = 14;
                    lightningColor = Pal.heal;
                }};
            }

            @Override
            public void update(Bullet b) {
                super.update(b);
                updateBulletInterval(b);

            }

            @Override
            public void draw(Bullet b) {
                super.draw(b);
                if (!(b.data() instanceof MassDriver.DriverBulletData data)) {
                    hit(b);
                    return;
                }
                Draw.z(Layer.effect);
                float realLength = Damage.findPierceLength(b, pierceCap, Mathf.dst(data.from.x, data.from.y, data.to.x, data.to.y));
                float fout = Mathf.clamp(b.time > b.lifetime - fadeTime ? 1f - (b.time - (lifetime - fadeTime)) / fadeTime : 1f);
                float baseLen = realLength * fout;
                float rot = b.rotation();

                for (int i = 0; i < colors.length; i++) {
                    Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

                    float colorFin = i / (float) (colors.length - 1);
                    float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                    float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * fout * baseStroke;
                    float ellipseLenScl = Mathf.lerp(1 - i / (float) (colors.length), 1f, pointyScaling);

                    Lines.stroke(stroke);
                    Lines.lineAngle(data.from.x, data.from.y, rot, baseLen - frontLength, false);

                    //back ellipse
                    Drawf.flameFront(data.from.x, data.from.y, divisions, rot + 180f, backLength, stroke / 2f);

                    //front ellipse
                    Tmp.v1.trnsExact(rot, baseLen - frontLength);
                    Drawf.flameFront(data.from.x + Tmp.v1.x, data.from.y + Tmp.v1.y, divisions, rot, frontLength * ellipseLenScl, stroke / 2f);
                }

                Drawf.light(data.from.x, data.from.y, data.to.x, data.to.y, lightStroke, lightColor, 0.7f);
                Draw.reset();
            }
        };
    }
}
