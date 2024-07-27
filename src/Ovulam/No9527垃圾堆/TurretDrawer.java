package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.ObjectMap;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.draw.DrawTurret;

public class TurretDrawer extends DrawTurret {
    //圆球位置
    public float cx = -16f, cy;
    //圆球半径
    public float radius = 8f;
    //小亮圆球半径
    public float smradius = 5f;
    //圆弧半径
    public float arcRadius = 14f;
    //闪电链半径
    public float lightingRadius = 16f;
    //闪电链间隔
    public float lightingTime = 15f;
    //闪电链数量
    public int lightingAmount = 5;
    //颜色
    public Color color = Color.cyan;

    public ObjectMap<Building, Float> timers = new ObjectMap<>();
    public Vec2 vec2A = new Vec2();
    public float sectorRad = 0.14f, rotateSpeed = 0.5f;
    public int sectors = 5;

    public TurretDrawer(String basePrefix) {
        super(basePrefix);
    }

    public TurretDrawer() {
        super();
    }

    @Override
    public void draw(Building build) {
        super.draw(build);
        Turret.TurretBuild tb = (Turret.TurretBuild) build;

        Draw.z(Layer.effect);
        Draw.color(color);
        vec2A.trns(tb.rotation, cx, cy);
        float x2 = build.x + tb.recoilOffset.x + vec2A.x;
        float y2 = build.y + tb.recoilOffset.y + vec2A.y;

        Fill.circle(x2, y2, radius);

        for(int i = 0; i < sectors; i++){
            float rot = build.rotation + i * 360f/sectors - Time.time * rotateSpeed;
            Lines.arc(x2, y2, arcRadius, sectorRad, rot);
        }

        if (timers.containsKey(build)){
            float timer = timers.get(build);
            timer += Vars.state.isPaused() ? 0 : Time.delta;
            if(timer > lightingTime){
                for (int i = 0; i < lightingAmount; i++) Fx.chainLightning.at(x2, y2, 0, color, new Vec2().trns(Mathf.random(360), lightingRadius).add(x2,y2));
                timer -= lightingTime;
            }
            timers.put(build, timer);
        }else timers.put(build, 0f);

        Draw.color();
        Fill.circle(x2, y2, smradius);
    }
}
