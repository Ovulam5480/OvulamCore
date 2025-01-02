package Ovulam.entities.Item;

import arc.graphics.Color;
import mindustry.type.Item;
import mindustry.world.meta.Stat;

public class OvulamItem extends Item {
    //强度
    public float strength = 0f;
    //有序值
    public float orderedValue = 0f;

    public OvulamItem(String name, Color color) {
        super(name, color);
    }

    public OvulamItem(String name) {
        super(name);
    }

    @Override
    public void setStats() {
        stats.addPercent(Stat.health, strength);
        super.setStats();
        stats.addPercent(Stat.armor, orderedValue);
    }

}
