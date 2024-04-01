package Ovulam.type.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class InvitationUnitType extends UnitType {
    public TextureRegion[] stageRegion;

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
        for (int i = 0; i < 4; i++) {
            stageRegion[i] = Core.atlas.find(name + "-stage-" + i);
        }
    }
    @Override
    public void drawBody(Unit unit){

        applyColor(unit);
        if(!hasResurrection(unit)) Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        else {
            int stage = 4 - Mathf.ceil(unit.health / unit.maxHealth * 4);
            if(stage == 3)Draw.rect(stageRegion[stage], unit.x, unit.y, unit.rotation - 90);
            else {
                float alpha = Math.min((unit.health/unit.maxHealth - 0.7f + stage * 0.25f) / 0.1f, 1);

                Draw.alpha(alpha);
                Draw.rect(stageRegion[stage], unit.x, unit.y, unit.rotation - 90);
                Draw.alpha(1 - alpha);
                Draw.rect(stageRegion[stage + 1], unit.x, unit.y, unit.rotation - 90);

                Draw.reset();

            }
        }
    }
}
