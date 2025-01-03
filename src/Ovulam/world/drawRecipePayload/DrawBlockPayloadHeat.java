package Ovulam.world.drawRecipePayload;

import Ovulam.OvulamMod;
import Ovulam.math.OvulamMath;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.world.Block;

//载荷附加一圈光晕
public class DrawBlockPayloadHeat extends DrawRecipePayload{
    TextureRegion heat;

    public void init(Block block, UnlockableContent payload){
        float size = 0;
        if(payload instanceof Block blockP) size = blockP.size;
        heat.setWidth(size);
        heat.setHeight(size);
    }

    @Override
    public void load(Block block) {
        this.heat = Core.atlas.find(OvulamMod.modName() + "payload-heat");
    }

    @Override
    public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY) {
        if(!(payload instanceof Block block))return;
        heat.setWidth(block.size * 32);

        Draw.alpha(OvulamMath.fparabola(progress));
        Draw.rect(heat, building.x + offsetX, building.y + offsetY);
        Draw.alpha(progress);
        Draw.rect(payload.fullIcon, building.x + offsetX, building.y + offsetY);
        Draw.reset();
    }
}
