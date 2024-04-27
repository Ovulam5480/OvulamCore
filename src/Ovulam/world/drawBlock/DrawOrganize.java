package Ovulam.world.drawBlock;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawOrganize extends DrawBlock {
    public TextureRegion region, podRegion;
    public DrawOrganize(){}
    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name);
        podRegion = Core.atlas.find(block.name + "-pod");
    }

    @Override
    public void draw(Building build){
        Draw.rect(region, build.x, build.y);
        Draw.rect(podRegion, build.x, build.y);
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{region, podRegion};
    }
}
