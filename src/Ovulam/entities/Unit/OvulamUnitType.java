package Ovulam.entities.Unit;

import arc.graphics.Color;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;

public class OvulamUnitType extends UnitType {
    //固定伤害减免
    public float damageReduction = 0;

    public int insideTrailLength;

    public float insideTrailScl = 1f;

    public Color insideTrailColor;

    public boolean wobble = true;


    public OvulamUnitType(String name) {
        super(name);
    }

    @Override
    public void init(){
        super.init();
    }

    @Override
    public void drawTrail(Unit unit) {
        if(unit.trail == null) unit.trail = new Trail(trailLength);

        Trail trail = unit.trail;
        trail.draw(trailColor == null ? unit.team.color : trailColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * (useEngineElevation ? unit.elevation : 1f)) * trailScl);

        if (insideTrailLength == 0 || insideTrailColor == null || !(unit instanceof OvulamUnit ovulamUnit)) return;

        if (ovulamUnit.insideTrail == null) ovulamUnit.insideTrail = new Trail(insideTrailLength);

        Trail trail2 = ovulamUnit.insideTrail;
        trail2.draw(insideTrailColor, (engineSize + Mathf.absin(Time.time, 2f, engineSize / 4f) * (useEngineElevation ? unit.elevation : 1f)) * insideTrailScl);
    }

    public void updateInsideTrail(OvulamUnit ovulamUnit){
        if(ovulamUnit.insideTrail != null){
            ovulamUnit.insideTrail.length = insideTrailLength;

            float scale = ovulamUnit.type.useEngineElevation ? ovulamUnit.elevation : 1f;
            float offset = ovulamUnit.type.engineOffset/2f + ovulamUnit.type.engineOffset/2f * scale;

            float cx = ovulamUnit.x + Angles.trnsx(ovulamUnit.rotation + 180, offset), cy = ovulamUnit.y + Angles.trnsy(ovulamUnit.rotation + 180, offset);
            ovulamUnit.insideTrail.update(cx, cy);
        }
    }

    @Override
    public void update(Unit unit){
        if (!(unit instanceof OvulamUnit ovulamUnit)) return;

        updateInsideTrail(ovulamUnit);
    }
}
