package Ovulam.world.draw;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.util.Align;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawMixer extends DrawBlock {
    public TextureRegion region, topRegion, rotatorRegion, iconRegion;
    public float rotation = 0;
    public int d8Index = 0;
    private final int[] index = new int[]{4,7,2,5,0,3,6,1};
    public Vec2 vec2 = new Vec2(1, 1);
    public float approach = 0;

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name);
        topRegion = Core.atlas.find(block.name + "-top");
        rotatorRegion = Core.atlas.find(block.name + "-rotator");
        iconRegion = Core.atlas.find(block.name + "-icon");
    }
    @Override
    public void draw(Building build){
        Draw.rect(region, build.x, build.y);

        Vec2 target = new Vec2(Geometry.d8[index[d8Index]].x, Geometry.d8[index[d8Index]].y);

        approach = Mathf.approachDelta(approach, build.efficiency, 0.01f);

        rotation += build.delta() * 3 * approach;
        vec2.approach(target,0.01f * build.delta() * approach);

        Font font = Fonts.outline;
        font.draw(String.valueOf(build.efficiency), build.x, build.y - 20, Align.center);

        if(vec2.epsilonEquals(target, 0.01f)){
            d8Index++;
            if(d8Index == 8)d8Index = 0;
        }

        Draw.z(Layer.blockOver + 2f);
        for (int i = 0; i < 4; i++){
            Drawf.spinSprite(rotatorRegion, build.x + 24 * Geometry.d8edge(i).x * vec2.x,
                    build.y + 24 * Geometry.d8edge(i).y * vec2.y, rotation);
        }
        Draw.rect(topRegion, build.x, build.y);
    }


    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{iconRegion};
    }
}
