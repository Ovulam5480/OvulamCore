package Ovulam.world.block.defense;

import arc.Core;
import arc.func.Floatc4;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Intersector;
import arc.math.geom.Vec2;
import arc.struct.FloatSeq;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.content.Items;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Groups;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.logic.LAccess;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;

import static mindustry.Vars.tilesize;

public class AblationTower extends Block {
    //正多边形的边数，只能是偶数
    public int side = 4;
    public float ablationDamage = 20;

    public float radius = 203.4f;
    public int segmentPoint = 2;
    public float alphaTime = 180f;

    public Consume phaseConsumer = consumeItem(Items.phaseFabric).boost();
    public float phaseUseTime = 350f;
    public float phaseRangeBoost = 203.4f;

    public Consume waveConsumer = consumeItem(Items.silicon).boost();
    public float waveUseTime = 350f;
    public int wavePointBoost = 2;

    public Consume surgeConsumer = consumeItem(Items.surgeAlloy).boost();
    public float surgeUseTime = 350f;
    public float surgeDamageBoost = 20f;

    public TextureRegion region;
    public TextureRegion nodeRegion, nodeShadowRegion;

    public AblationTower(String name) {
        super(name);
        clipSize = 800f;
        update = true;
        sync = true;
        acceptsPayload = true;
        destructible = true;
    }

    public static float[] polygon(float x, float y, int sides, float radius) {
        FloatSeq polygon = new FloatSeq(sides * 2);
        Vec2 s = new Vec2();

        for (int i = 0; i < sides; i++) {
            s.trns(i * 360f / sides, radius);
            polygon.add(x + s.x, y + s.y);
        }

        return polygon.items;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        nodeRegion = Core.atlas.find(name + "-nodeUnit");
        nodeShadowRegion = Core.atlas.find(name + "-nodeUnit");
    }

    @Override
    public void init() {
        //只允许偶数
        if (side % 2f != 0f) side = side + 1;
        //至少六边形
        if (side < 6) side = 6;
        super.init();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Font font = Fonts.outline;
        //font.apply(String.valueOf(), x, y - 20, Align.center);

        Geometry.iteratePolySegments(polygon(x * tilesize + offset, y * tilesize + offset, side, radius),
                (x1, y1, x2, y2) -> Drawf.line(Color.white, x1, y1, x2, y2));
    }


    public class AblationTowerBuild extends Building {
        public ObjectMap<Bullet, int[]> bulletAndPoint = new ObjectMap<>();
        public int color;
        public float lerpDeltaRadius = 0;
        public int previousPoint = segmentPoint;
        public float alphaTimer;

        public int waveEfficiency;
        public float phaseEfficiency,surgeEfficiency;
        public float phaseTimer,waveTimer,surgeTimer;

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4){
            if(type == LAccess.config){
                color = -1;
            }else if(type == LAccess.color){
                color = Tmp.c1.fromDouble(p1).rgba8888();
            }
            super.control(type, p1, p2, p3, p4);
        }


        public Effect ablationEffect = new Effect(60, (e) -> {
            Draw.color(Color.valueOf("e89629"));
            Lines.stroke(e.rotation * e.fout());
            Lines.circle(e.x, e.y, e.rotation * e.fin());
        });

        public float realRadius() {
            return radius + phaseRangeBoost * phaseEfficiency;
        }

        public int realSegmentPoint() {
            return segmentPoint + waveEfficiency * wavePointBoost;
        }

        public float realAblationDamage() {
            return ablationDamage + surgeEfficiency * surgeDamageBoost;
        }

        public int setEfficiency(boolean valid){
            return Mathf.num(valid);
        }

        public float setEfficiency(float efficiency, float progress, boolean valid){
            return Mathf.lerpDelta(efficiency, Mathf.num(valid), progress);
        }

        public float setConsume(Consume consumer, float timer, float useTime){
            timer += delta();
            if(timer > useTime) {
                consumer.trigger(this);
                timer -= useTime;
            }
            return timer;
        }

        @Override
        public void updateTile() {
            if(previousPoint != realSegmentPoint()) alphaTimer += delta();
            lerpDeltaRadius = Mathf.lerpDelta(lerpDeltaRadius, realRadius(), 0.05f);

            boolean phaseValid = phaseConsumer.efficiency(this) > 0;
            phaseEfficiency = setEfficiency(phaseEfficiency, 0.08f, phaseValid);
            if(phaseValid) phaseTimer = setConsume(phaseConsumer, phaseTimer, phaseUseTime);

            boolean waveValid = waveConsumer.efficiency(this) > 0;
            waveEfficiency = setEfficiency(waveValid);
            if(waveValid) waveTimer = setConsume(waveConsumer, waveTimer, waveUseTime);

            boolean surgeValid = surgeConsumer.efficiency(this) > 0;
            surgeEfficiency = setEfficiency(surgeEfficiency, 0.08f, surgeValid);
            if(surgeValid) surgeTimer = setConsume(surgeConsumer, surgeTimer, surgeUseTime);

            Seq<Bullet> bulletSeq = Groups.bullet.intersect(x - lerpDeltaRadius, y - lerpDeltaRadius,
                    lerpDeltaRadius * 2f, lerpDeltaRadius * 2f).select(bullet ->
                    Intersector.isInPolygon(polygon(x, y, side, lerpDeltaRadius), 0, side * 2, bullet.x, bullet.y));

            ObjectMap<Bullet, int[]> bulletPositions = new ObjectMap<>();
            bulletSeq.each(bullet -> {
                int[] positions = new int[side / 2];
                for (int i = 0; i < side / 2; i++) {
                    float position = (float) (Math.cos(Mathf.pi * 2f * (i + 0.5f) / side) * (bullet.x - x) +
                            Math.sin(Mathf.pi * 2f * (i + 0.5f) / side) * (bullet.y - y));
                    for (int j = 0; j < polygonSegment(side, lerpDeltaRadius, previousPoint).length - 1; j++) {
                        if (polygonSegment(side, lerpDeltaRadius, previousPoint)[j + 1] > position) {
                            positions[i] = j;
                            break;
                        }
                    }
                }
                bulletPositions.put(bullet, positions);
            });

            bulletPositions.each(this::damageBullet);

            Seq<Bullet> bullets = new Seq<>();
            bulletAndPoint.each((bullet, ints) -> {
                if (!bulletPositions.containsKey(bullet)) bullets.add(bullet);
            });
            bullets.each(bulletAndPoint::remove);
        }

        //多边形的内径
        public float insideRadiusLength(int side, float radius) {
            return (float) Math.cos(Mathf.pi / side) * radius;
        }

        //多边形三条边对应的弦长
        public float midSegmentLength(int side, float radius) {
            return (float) Math.sin(Mathf.pi * 3f / side) * radius * 2f;
        }

        //多边形的边长
        public float sideLength(int side, float radius) {
            return (float) Math.sin(Mathf.pi / side) * radius * 2f;
        }

        //各个间隔
        public float[] polygonSegment(int side, float radius, int segmentPoint) {
            float midLength = (float) (Math.cos(Mathf.pi * 3f / side) * radius * 2f);
            float insideRadius = insideRadiusLength(side, radius);
            float sideSegments = (insideRadius * 2f - midLength) / (2 * (segmentPoint + 1));

            float[] segments = new float[2 * segmentPoint + 4];
            float position = insideRadius;

            for (int i = 0; i < (2 * segmentPoint + 4) / 2; i++) {
                segments[i] = -position;
                segments[2 * segmentPoint + 3 - i] = position;

                position -= sideSegments;
            }

            return segments;
        }

        //todo 电力消耗
        @Override
        public void draw() {
            Draw.rect(region,x,y);
            Draw.z(Layer.flyingUnit);
            Geometry.iteratePolygon((x,y) -> {
                Drawf.spinSprite(nodeRegion, x,y,Time.time/10f);
                Drawf.shadow(nodeShadowRegion, x,y,Time.time/10f);
                }, polygon(x,y,side,realRadius()));

            if(color == -1) Draw.color(Color.valueOf("ff0000").shiftHue((float) ((double) Time.time * 0.4 + 22.0)));
            else if(color != 0) Draw.color(Tmp.c1.set(color));
            else Draw.color(Color.valueOf("e89629"),Color.valueOf("53d3f9"),surgeEfficiency);

            Draw.alpha(1 - Mathf.sqr(alphaTimer / alphaTime));
            drawAblationLine(Layer.shields, 2f, previousPoint);
            drawAblationLine(Layer.bullet, 4f, previousPoint);

            if(previousPoint != realSegmentPoint()){
                Draw.alpha(1 - Mathf.sqr(1 - alphaTimer / alphaTime));
                drawAblationLine(Layer.shields, 2f, realSegmentPoint());
                drawAblationLine(Layer.bullet, 4f, realSegmentPoint());
                Draw.reset();

                if(alphaTimer > alphaTime){
                    previousPoint = realSegmentPoint();
                    alphaTimer = 0f;
                }
            }

            Font font = Fonts.outline;
            font.draw(String.valueOf(phaseEfficiency), x, y - 20, Align.center);
            font.draw(String.valueOf(phaseTimer), x, y - 40, Align.center);
        }

        public void drawAblationLine(float layer, float stick, int point){
            Draw.z(layer);
            Lines.stroke(stick);
            ablationLine(point, Lines::line);
        }

        public void ablationLine(int segmentPoint, Floatc4 floatc4) {
            for (int i = 0; i < side; i++) {
                for (int j = 0; j <= segmentPoint + 1; j++) {
                    float polygonSegment = polygonSegment(side, lerpDeltaRadius, segmentPoint)[j];

                    float sin = (float) Math.sin(Mathf.pi * 2 * (i + 0.5f) / side);
                    float cos = (float) Math.cos(Mathf.pi * 2 * (i + 0.5f) / side);

                    //每个方向上面每条线的长度
                    float length = Mathf.lerp(sideLength(side, lerpDeltaRadius), midSegmentLength(side, lerpDeltaRadius),
                            (float) j / (segmentPoint + 1));

                    floatc4.get(
                            x + cos * polygonSegment + sin * length / 2f,
                            y + sin * polygonSegment - cos * length / 2f,
                            x + cos * polygonSegment - sin * length / 2f,
                            y + sin * polygonSegment + cos * length / 2f
                    );
                }
            }
        }

        public void damageBullet(Bullet bullet, int[] ints) {
            float sumDamage = 0;

            //子弹每换一次区域就造成一次伤害,用于抵消低帧影响
            //todo 低帧测试
            if (bulletAndPoint.containsKey(bullet)) {
                for (int i = 0; i < ints.length; i++) {
                    sumDamage += Math.abs(ints[i] - bulletAndPoint.get(bullet)[i]) * realAblationDamage();
                }
            } else bulletAndPoint.put(bullet, ints);

            if (sumDamage == 0) return;

            //todo 加个音效？
            ablationEffect.at(bullet.x, bullet.y, 8);

            if (bullet.damage() > sumDamage) {
                bullet.damage(bullet.damage() - sumDamage);
                bulletAndPoint.put(bullet, ints);
            } else {
                bullet.remove();
                bulletAndPoint.remove(bullet);
            }
        }

    }
}
