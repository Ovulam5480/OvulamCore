package Ovulam.type.unit;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Align;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;

public class OvulamUnit extends UnitType {
    public TextureRegion[] partRegionA = new TextureRegion[4],partRegionB = new TextureRegion[4];

    public OvulamUnit(String name) {
        super(name);
    }

    @Override
    public void load(){
        for (int i = 0; i < 4; i++){
            partRegionA[i] = Core.atlas.find(name + "-A-" + i);
            partRegionB[i] = Core.atlas.find(name + "-B-" + i);
        }
        super.load();
    }

    @Override
    public void drawBody(Unit unit){
        applyColor(unit);
        Draw.rect(region, unit.x, unit.y, unit.rotation - 90);
        Draw.reset();

        float alphaA = Mathf.mod(unit.rotation, 90f) / 90f;
        int rotA = (int) (unit.rotation / 90);

        Draw.alpha(1 - alphaA);
        Draw.rect(partRegionA[Mathf.mod(rotA, 4)], unit.x, unit.y,unit.rotation - 90);
        Draw.alpha(alphaA);
        Draw.rect(partRegionA[Mathf.mod(rotA + 1, 4)], unit.x, unit.y,unit.rotation - 90);

        Font font = Fonts.outline;


        float alphaB = Mathf.mod(unit.rotation - 45f, 90f) / 90f;
        int rotB = Math.round(unit.rotation / 90f);

        Draw.alpha(1 - alphaB);
        Draw.rect(partRegionB[Mathf.mod(rotB, 4)], unit.x, unit.y,unit.rotation - 90);
        Draw.alpha(alphaB);
        Draw.rect(partRegionB[Mathf.mod(rotB + 1, 4)], unit.x, unit.y,unit.rotation - 90);

        font.draw(String.valueOf(unit.rotation), unit.x, unit.y - 20, Align.center);
        font.draw(String.valueOf(alphaB), unit.x, unit.y - 40, Align.center);
        font.draw(String.valueOf(rotB), unit.x, unit.y - 60, Align.center);

    }
}
