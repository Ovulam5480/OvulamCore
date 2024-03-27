package Ovulam.world.draw;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Interp;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;
import Ovulam.world.block.production.PayloadCrafter;

public class DrawMetaglassCrafter extends DrawBlock {
    public TextureRegion light;

    public void draw(Building build){

        if(!(build instanceof PayloadCrafter.PayloadCrafterBuild b)){
            return;
        }
        float a = build.progress() < 0.7f ? Interp.pow3.apply(build.progress() / 0.7f) : Interp.fastSlow.apply((1 - build.progress()) / 0.3f);

        Draw.color(Color.valueOf("#fcd36a"));
        Draw.alpha(a);
        Draw.rect(light, build.x, build.y);

        Draw.reset();

        if(b.getPlanTo() == null){
            return;
        }
        TextureRegion textureRegion = b.getPlanTo().fullIcon;
        Draw.alpha(build.progress());
        Draw.rect(textureRegion, build.x, build.y);

        Draw.reset();


    }

    @Override
    public void load(Block block){
        light = Core.atlas.find(block.name + "-lightBlock");
    }
}
