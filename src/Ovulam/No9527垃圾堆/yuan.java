package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.entities.abilities.Ability;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;

public class yuan extends Ability {
    //半径
    public float radius = 100f;
    //时间间隔
    public float radiusTime = 60f;
    //显示时间
    public float showshowTime = 40f;
    //颜色
    public Color color = Color.pink;
    //粗细倍率
    public float multi = 1f;

    @Override
    public void draw(Unit unit){
        float progress = (Time.time % radiusTime) / radiusTime;
        float showProgress = (Time.time % radiusTime) / showshowTime;

        Draw.z(Layer.effect);

        Lines.stroke(Math.min(4f, (1 - showProgress) * 16f) * multi);
        color.a(Mathf.clamp((1 - showProgress) * 4f, 0f, 1f));
        Draw.color(color);

        Lines.circle(unit.x, unit.y, progress * radius);
        Draw.reset();
        Lines.stroke(1f);
    }
}
