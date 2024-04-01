package Ovulam.type.unit;

import Ovulam.type.OvulamEventType;
import arc.Events;
import arc.math.Mathf;
import mindustry.game.Team;
import mindustry.gen.UnitEntity;

public class InvitationUnitEntity extends UnitEntity {
    private float damageReduction = 0;
    public boolean hasResurrection = false;
    public boolean resurrecting = false;

    @Override
    public void rawDamage(float amount) {
        amount = Math.max(amount - damageReduction, 0);
        super.rawDamage(amount);
    }

    @Override
    public void kill() {
        if(!(hasResurrection)){
            Events.fire(new OvulamEventType.invitationResurrection(this));
            resurrecting = true;
            health = 1f;
        } else {
            super.kill();
        }
    }

    @Override
    public boolean targetable(Team targeter) {
        //todo
        if(resurrecting)return false;
        return this.type.targetable(this, targeter);
    }

    public void resurrection(){
        health = Mathf.approachDelta(health, maxHealth, maxHealth * 0.003f);
        if(Mathf.equal(health, maxHealth)){
            resurrecting = false;
            hasResurrection = true;
            damageReduction = 0;
        }
    }

    @Override
    public void update() {
        if(resurrecting) resurrection();
        super.update();
    }
}
