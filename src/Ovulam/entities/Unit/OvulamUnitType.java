package Ovulam.entities.Unit;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Unit;
import mindustry.graphics.Trail;
import mindustry.type.UnitType;

public class OvulamUnitType extends UnitType {
    //复活次数
    public int rebirthTime;
    //损坏的贴图数量
    public int wornStage = 0;

    //固定伤害减免
    public float damageReduction = 0;

    public int insideTrailLength;

    public float insideTrailScl = 1f;

    public Color insideTrailColor;

    ////////////////////
    //从0到stage, 单位的血量降至0
    public TextureRegion[] wornRegion;

    public OvulamUnitType(String name) {
        super(name);
    }

    @Override
    public void init(){
        super.init();
    }
    @Override
    public void load() {
        super.load();

        if (wornStage > 0) {
            wornRegion = new TextureRegion[wornStage];
            for (int i = 0; i < wornStage; i++) {
                wornRegion[i] = Core.atlas.find(name + "-stage-" + i);
            }
        }
    }

    @Override
    public void drawBody(Unit unit) {
        //只有在最后一段生命显示损坏贴图
        if(!(unit instanceof OvulamUnit ovulamUnit) || wornStage == 0 || ovulamUnit.rebirthStage < rebirthTime)super.drawBody(unit);
        else {
            int per = Mathf.floor(unit.health / unit.maxHealth * wornStage);
            Draw.rect(wornRegion[per], unit.x, unit.y, unit.rotation - 90);
        }
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
