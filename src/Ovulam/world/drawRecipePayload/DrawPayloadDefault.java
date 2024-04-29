package Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.Draw;
import mindustry.gen.Building;

public class DrawPayloadDefault extends DrawRecipePayload{

    @Override
    public void draw(Building building, float offsetX, float offsetY) {
        Draw.rect(payload.icon(), building.x + offsetX, building.y + offsetY);
    }
}
