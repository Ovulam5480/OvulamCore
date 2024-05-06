package Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.Draw;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;

//载荷有小到大膨胀
public class DrawPayloadExpansion extends DrawRecipePayload{
    @Override
    public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY) {
        Draw.scl(progress);
        Draw.rect(payload.fullIcon, building.x + offsetX, building.y + offsetY);
        Draw.reset();
    }
}
