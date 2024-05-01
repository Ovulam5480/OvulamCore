package Ovulam.entities.unit;

import mindustry.gen.CrawlUnit;

public class 伤害溢出无效Unit extends CrawlUnit {
    @Override
    public void rawDamage(float amount) {
        if(amount > maxHealth)return;
        super.rawDamage(amount);
    }
}
