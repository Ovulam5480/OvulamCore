package Ovulam.world.drawRecipePayload;

import arc.graphics.g2d.Draw;
import arc.util.Time;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;

public class DrawPayloadBuild extends DrawRecipePayload{
    @Override
    public void draw(UnlockableContent payload, Building building, float progress, float offsetX, float offsetY) {
        Draw.draw(Layer.blockBuilding, () -> {
            Draw.color(Pal.accent);

            Shaders.blockbuild.region = payload.fullIcon;
            Shaders.blockbuild.time = Time.time;
            Shaders.blockbuild.progress = progress;

            Draw.rect(payload.fullIcon, building.x + offsetX, building.y + offsetY);
            Draw.flush();

            Draw.color();
        });
    }
}
