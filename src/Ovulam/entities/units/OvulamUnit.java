package Ovulam.entities.units;

import Ovulam.gen.EntityRegistry;
import mindustry.Vars;
import mindustry.entities.Damage;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Trail;

public class OvulamUnit extends UnitEntity {
    public transient Trail insideTrail;

    public OvulamUnit() {
    }

    public OvulamUnitType asType(){
        return (OvulamUnitType) type;
    }

    public int classId() {
        return EntityRegistry.getID(this.getClass());
    }

    public void damage(float amount) {
        this.rawDamage(Damage.applyArmor(amount, armor()) / this.healthMultiplier / Vars.state.rules.unitHealth(this.team));
    }

    //减免固定数值伤害, 包括穿甲伤害
    @Override
    public void rawDamage(float amount) {
        float damageReduction = asType().damageReduction;
        amount = Math.max(amount - damageReduction, 0);
        super.rawDamage(amount);
    }

    @Override
    public void wobble() {
        if(asType().wobble)super.wobble();
    }
}