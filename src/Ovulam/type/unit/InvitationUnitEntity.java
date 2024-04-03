package Ovulam.type.unit;

import Ovulam.type.OvulamEventType;
import Ovulam.world.block.storage.BaseCoreBlock;
import arc.Events;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.UnitEntity;

public class InvitationUnitEntity extends UnitEntity {
    public boolean hasResurrection = false;
    public boolean resurrecting = false;
    private float damageReduction = 0;

    public boolean targetMove = true;
    public float startingX, startingY;
    public float moveTime = 3000;

    public float moveTimer;
    public Building target;

    @Override
    public void rawDamage(float amount) {
        amount = Math.max(amount - damageReduction, 0);
        super.rawDamage(amount);
    }

    @Override
    public void kill() {
        if (!(hasResurrection)) {
            Events.fire(new OvulamEventType.invitationResurrection(this));
            resurrecting = true;
            health = 1f;
        } else {
            super.kill();
        }
    }

    public boolean hittable() {
        if (resurrecting) return false;
        return super.hittable();
    }

    @Override
    public boolean targetable(Team targeter) {
        if (resurrecting) return false;
        return super.targetable(targeter);
    }

    public void resurrection() {
        health = Mathf.approachDelta(health, maxHealth, maxHealth * 0.003f);
        if (Mathf.equal(health, maxHealth)) {
            resurrecting = false;
            hasResurrection = true;
            damageReduction = 0;
            setTarget();
        }
    }

    public boolean setTarget() {
        startingX = x;
        startingY = y;
        target = Vars.state.teams.get(Vars.state.rules.defaultTeam).cores.find(coreBuild -> coreBuild instanceof BaseCoreBlock.BaseCoreBuild b && b.destroyGameOver());
        if (target == null) target = Vars.state.teams.closestEnemyCore(x, y, team);
        if (target != null) {
            moveTimer /= 2f;
            return true;
        }
        return false;
    }

    @Override
    public void update() {
        if (resurrecting) resurrection();
        if (targetMove) {
            if (target == null) {
                if (!setTarget()) {
                    targetMove = false;
                }
            } else {
                moveTimer += Time.delta;
                float fin = moveTimer / moveTime;
                set(Mathf.lerp(startingX, target.x, fin), Mathf.lerp(startingY, target.y, fin));
                rotation(Mathf.angle(startingX - target.x, startingY - target.y));

                if(Mathf.equal(startingX, target.x, 8) && Mathf.equal(startingY, target.y, 8)){
                    Call.buildDestroyed(target);
                }
            }
        }

        super.update();
    }
}
