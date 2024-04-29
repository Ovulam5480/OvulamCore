package Ovulam.world.drawRecipePayload;

import Ovulam.OvulamMod;
import Ovulam.math.OvulamScaled;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;

public class DrawPayloadHeat extends DrawRecipePayload{
    TextureRegion heat;

    @Override
    public void load(Block block) {
        TextureRegion heat = Core.atlas.find(OvulamMod.ovulamName() + "payload-heat");
        heat.setHeight(payload.size() / 8 * 32);
        heat.setWidth(payload.size() / 8 * 32);
        this.heat = heat;
    }

    @Override
    public void draw(Building building, float offsetX, float offsetY) {
        Draw.alpha(OvulamScaled.fparabola(building.progress(), 0.8f));
        Draw.rect(heat, building.x + offsetX, building.y + offsetY);
        Draw.alpha(building.progress());
        Draw.rect(payload.icon(), building.x + offsetX, building.y + offsetY);
        Draw.reset();
    }
}
