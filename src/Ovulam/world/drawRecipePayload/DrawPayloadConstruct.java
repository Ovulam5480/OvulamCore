package Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;

//方块的建造效果
public class DrawPayloadConstruct extends DrawRecipePayload{
    @Override
    public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY) {
        Draw.draw(Layer.blockOver, () -> Drawf.construct(building.x + offsetX, building.y + offsetY, payload.fullIcon,
                0, progress, progress, Time.time));
    }
}
