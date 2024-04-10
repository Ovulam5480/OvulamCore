package Ovulam.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawKnitter extends DrawBlock {
    public TextureRegion region, spindleRegion, iconRegion, lineRegion;

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name);
        spindleRegion = Core.atlas.find(block.name + "-spindle");
        iconRegion = Core.atlas.find(block.name + "-icon");
        lineRegion = Core.atlas.find(block.name + "-line");
    }
    @Override
    public void draw(Building build){
        Draw.rect(region, build.x, build.y);
        Font font = Fonts.outline;
        //font.draw(String.valueOf(360f * i / 8), build.x + sx, build.y + sy, Align.center);

        for (int i = 0; i < 8; i++){
            int pow = Mathf.pow(-1, i);
            float rotate = pow * build.totalProgress() + 22.5f;

            float sx = (float) (Math.cos(360f * i / 8 * Mathf.degreesToRadians) * 32);
            float sy = (float) (Math.sin(360f * i / 8 * Mathf.degreesToRadians) * 32);

            Drawf.spinSprite(spindleRegion, build.x + sx, build.y + sy, rotate);

            Draw.z(Layer.blockOver + 1);

            float lineRotate = rotate + 45;

            //if(Mathf.mod(lineRotate - 360f * i / 8, 360))

            float lx = (float) (Math.cos(lineRotate * Mathf.degreesToRadians) * 12);
            float ly = (float) (Math.sin(lineRotate * Mathf.degreesToRadians) * 12);

            //0.25 6 6 10

            Draw.rect(lineRegion, build.x + sx + pow * lx, build.y + sy + pow * ly);
            //Draw.rect(lineRegion, build.x + sx - lx, build.y + sy - ly);
        }
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{iconRegion};
    }
}