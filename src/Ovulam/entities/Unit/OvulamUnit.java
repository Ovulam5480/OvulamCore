package Ovulam.entities.Unit;

import Ovulam.modContent.OvulamUnitTypes;
import arc.math.Mathf;
import mindustry.game.Team;
import mindustry.gen.UnitEntity;
import mindustry.graphics.Trail;

public class OvulamUnit extends UnitEntity {
    //当前复活阶段
    public int rebirthStage;
    public transient Trail insideTrail;
    public boolean resurrecting = false;

    public OvulamUnit() {
    }

    public OvulamUnitType getType(){
        return (OvulamUnitType) type;
    }

    public int classId() {
        return OvulamUnitTypes.getId(this.getClass());
    }

    //减免固定数值伤害, 包括穿甲伤害
    @Override
    public void rawDamage(float amount) {
        float damageReduction = getType().damageReduction;
        amount = Math.max(amount - damageReduction, 0);
        super.rawDamage(amount);
    }

    @Override
    public boolean hittable() {
        if (resurrecting) return false;
        return super.hittable();
    }

    @Override
    public boolean targetable(Team targeter) {
        if (resurrecting) return false;
        return super.targetable(targeter);
    }

    @Override
    public void kill() {
        if (rebirthStage < getType().rebirthTime) {
            //Events.fire(new OvulamEventType.invitationResurrection(this));
            resurrecting = true;
            rebirthStage++;
            health = 1f;
        } else {
            super.kill();
        }
    }

    public void resurrection() {
        health = Mathf.approachDelta(health, maxHealth, maxHealth * 0.003f);
        if (Mathf.equal(health, maxHealth)) resurrecting = false;
    }

    @Override
    public void update() {
        if (resurrecting) resurrection();
        super.update();
    }
}