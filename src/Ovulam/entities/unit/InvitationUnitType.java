package Ovulam.entities.unit;

import Ovulam.entities.OvulamEventType;
import Ovulam.world.block.storage.BaseCoreBlock;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

public class InvitationUnitType extends UnitType {
    public TextureRegion[] stageRegion, dropRegion;
    public int previousStage = 0;
    public int currentStage;
    public float dropTime = 300f;
    public float dropTimer = 0;
    public float dropX, dropY, dropR;

    public InvitationUnitType(String name) {
        super(name);
        constructor = InvitationUnitEntity::new;
    }

    public boolean hasResurrection(Unit unit){
        return ((InvitationUnitEntity)unit).hasResurrection;
    }

    @Override
    public void load(){
        super.load();
        stageRegion = new TextureRegion[4];
        dropRegion = new TextureRegion[3];
        for (int i = 0; i < 4; i++) {
            stageRegion[i] = Core.atlas.find(name + "-stage-" + i);
        }
        for (int i = 0; i < 3; i++) {
            dropRegion[i] = Core.atlas.find(name + "-drop-" + i);
        }
    }
    @Override
    public void drawBody(Unit unit){
        applyColor(unit);
        if(!hasResurrection(unit)) Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        else {
            if(currentStage == 3)Draw.rect(stageRegion[currentStage], unit.x, unit.y, unit.rotation - 90);
            else {
                float alpha = Math.min((unit.health/unit.maxHealth - 0.7f + currentStage * 0.25f) / 0.1f, 1);

                Draw.alpha(alpha);
                Draw.rect(stageRegion[currentStage], unit.x, unit.y, unit.rotation - 90);

                if(dropTimer > 0){
                    Draw.alpha(1 - dropTimer/dropTime);
                    Draw.rect(dropRegion[previousStage], dropX, dropY, dropR - 90);
                }

                Draw.reset();

            }
        }
    }

    @Override
    public void update(Unit unit){
        currentStage = 4 - Mathf.ceil(unit.health / unit.maxHealth * 4);
        if(previousStage < currentStage){
            dropTimer += Time.delta;

            if(dropTimer >= dropTime){
                previousStage = currentStage;
                dropTimer = 0;
            }
        }else {
            dropX = unit.x;
            dropY = unit.y;
            dropR = unit.rotation;
        }
    }

    public static class InvitationUnitEntity extends UnitEntity {
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
}
