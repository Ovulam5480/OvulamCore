package Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.Draw;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;

public class DrawPayloadDefault extends DrawRecipePayload{

    @Override
    public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY) {
        Draw.alpha(progress);
        Draw.rect(payload.fullIcon, building.x + offsetX, building.y + offsetY);
        Draw.reset();
    }
}
