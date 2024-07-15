package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import mindustry.content.Fx;
import mindustry.entities.abilities.StatusFieldAbility;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.StatusEffect;

public class RoundBUFFAbility extends StatusFieldAbility {
    public Color color = Color.pink;
    public RoundBUFFAbility(StatusEffect effect, float duration, float reload, float range) {
        super(effect, duration, reload, range);
        activeEffect = Fx.none;
    }

    @Override
    public void draw(Unit unit){
        Draw.color(color);
        Draw.z(Layer.shields);
        Fill.circle(unit.x, unit.y, range);
        Draw.reset();
    }
}
