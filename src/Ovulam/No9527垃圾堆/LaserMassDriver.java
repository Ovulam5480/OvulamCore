package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.bullet.MassDriverBolt;
import mindustry.gen.Building;
import mindustry.gen.Sounds;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.world.blocks.distribution.MassDriver;
import static mindustry.Vars.world;

public class LaserMassDriver extends MassDriver {
    public float 圆环间隔 = 32*2f;//改大就间隔更小, 改小就间隔更大
    public float 圆环大小 = 0.8f;
    //////////////////////////////
    public float lightStroke = 40f;
    public int divisions = 13;
    public Color[] colors = new Color[]{Pal.heal.cpy().a(.2f), Pal.heal.cpy().a(.5f), Pal.heal.cpy().mul(1.2f), Color.white};
    public float strokeFrom = 2f;
    public float strokeTo = 0.5f;
    public float backLength = 7f, frontLength = 7f;
    public float width = 4f, oscScl = 2f, oscMag = 1.5f;
    public Effect chain = Fx.chainLightning;
    //////////////////////////////////
    public LaserMassDriver(String name) {
        super(name);
        clipSize=500*8;
        itemCapacity = 120;
        range = 440f;
        reload = 3f;//发射延迟
        size = 4;
        knockback = 0;
        minDistribute = 2;//最少发射数量
        shootEffect=  receiveEffect = Fx.none;
        shootSound = Sounds.none;
        bulletLifetime = 10*60f;//子弹存在时间
        bullet = new MassDriverBolt() {{
            pierce = true;
            despawnEffect =
            hitEffect = Fx.none;
        }};
    }

    public void init(){
        super.init();
        chain.clip = clipSize;
    }

    public class LaserMassDriverBuilding extends MassDriverBuild {
        public int timer;
        public float prv, alpha;

        @Override
        public void updateTile() {
            super.updateTile();
            if (this.state == MassDriver.DriverState.idle || this.state == MassDriver.DriverState.accepting) {
                var Times = 20;//输出速度倍率写 2 总输出速度为默认的 200%
                for (var i = 0; i < Times; i++) {
                    this.dumpAccumulate();
                }
            }


            if (!linkValid()) return;
            alpha = Mathf.approachDelta(alpha, 1, 0.03f);
            ///////////闪电////////////
            int t = (int) (Time.time / 1000 * 60) % 6;
            if (t != timer) {
                timer = t;
                Building link = world.build(this.link);
                float angle = Mathf.angle(link.x - x, link.y - y);

                for (int i = 0; i < 4; i++){
                    float dog = Mathf.random(-0.75f, 0.75f) / 2f;
                    float offX = (float) (Math.sin(Mathf.degreesToRadians * angle) * size * 8 * dog);
                    float offY = (float) (Math.cos(Mathf.degreesToRadians * angle) * size * 8 * dog);
                    Vec2 vec2 = new Vec2(link.x + offX, link.y + offY);
                    chain.at(x + offX, y + offY, rotation, Color.pink, vec2);
                }
            }

            if (prv != this.link) {
                alpha = 0;
                prv = this.link;
            }
        }

        @Override
        public void draw() {
            super.draw();
            if (!linkValid()) return;
            Building link = world.build(this.link);

            /////////////激光////////////
            Draw.z(Layer.effect);
            float baseLen = Mathf.dst(link.x - x, link.y - y);
            float rot = Mathf.angle(link.x - x, link.y - y);

            for (int i = 0; i < colors.length; i++) {
                Draw.color(Tmp.c1.set(colors[i]).mul(1f + Mathf.absin(Time.time, 1f, 0.1f)));

                float colorFin = i / (float) (colors.length - 1);
                float baseStroke = Mathf.lerp(strokeFrom, strokeTo, colorFin);
                float stroke = (width + Mathf.absin(Time.time, oscScl, oscMag)) * baseStroke * efficiency;

                Lines.stroke(stroke);
                Lines.lineAngle(x, y, rot, baseLen - frontLength, false);

                //back ellipse
                Drawf.flameFront(x, y, divisions, rot + 180f, backLength, stroke / 2f);

                //front ellipse
                Tmp.v1.trnsExact(rot, baseLen - frontLength);
                Drawf.flameFront(x + Tmp.v1.x, y + Tmp.v1.y, divisions, rot, frontLength, stroke / 2f);
            }

            Drawf.light(x, y, link.x, link.y, lightStroke, lightColor, 0.7f);
            Draw.reset();

            Lines.stroke(1.7f);
            Draw.color(Color.valueOf("b2ffe1")
                   // Pal.remove
            );
            float lfin = (Time.time % 60f) / 60f;
            int amount = (int) (baseLen / (圆环间隔)) + 2;
            float len = baseLen / amount;

            for (int i = 0; i < amount; i++) {
                float scl = 1;
                if (i == 0) scl = lfin;
                else if (i == amount - 1) scl = 1 - lfin;

                float fx = (float) (x + (i + lfin) * len * Math.cos(Mathf.degreesToRadians * rot));
                float fy = (float) (y + (i + lfin) * len * Math.sin(Mathf.degreesToRadians * rot));

                Draw.alpha(alpha);
                Lines.ellipse(fx, fy, scl*圆环大小, size * 8 / 3f, size * 8 / 2f, rotation);
            }
            Draw.reset();
        }

    }
}
