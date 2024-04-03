package Ovulam.type.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
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
}
