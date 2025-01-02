package Ovulam.No9527垃圾堆;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.content.Fx;
import mindustry.graphics.Drawf;
import mindustry.world.blocks.production.Drill;

import static mindustry.Vars.tilesize;

public class Drill9527 extends Drill {

    public TextureRegion region;
    public TextureRegion laser;
    public TextureRegion laserEnd;

    public Drill9527(String name){
        super(name);
        size = 9;
        hasShadow = false;
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{region};
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        laser = Core.atlas.find("laser");
        laserEnd = Core.atlas.find("laser-end");
    }

    public class PayloadDrillBuild extends DrillBuild {
        float i;

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            float time = Time.time / 100;

            if(efficiency > 0){
                //dog
                Draw.color(Color.valueOf("ff0000").shiftHue((float) ((double) Time.time * 0.4 + 22.0)));
                Draw.alpha(0.6f * (1f - 0.3f + Mathf.absin(Time.time, 3f, 0.3f)));
                Draw.rect(region, x, y);
                Draw.color();
            }
            float x1 = x - 3.5f * 8 + 1;
            float y1 = y + 2.5f * 8;
            float x2 = x - 2f * 8 - 1;
            float y2 = y + 3 * 8 - 1;

            float width = 1 + Mathf.absin(Time.time + 5 + (id % 9) * 9, 2f, 0.07f);

            float pfx = x - tilesize + tilesize * Math.abs(time % 8 - 4);
            i = Mathf.lerpDelta(i, pfx, 0.05f);
            float pfyA = (float) (y + 0.675f * tilesize - 1.75f * tilesize * Math.sin(time * 2));
            float pfyB = (float) (y + 0.675f * tilesize + 1.75f * tilesize * Math.sin(time * 2));
            if(efficiency > 0){
                Drawf.laser(laser, laserEnd, x1, y1, i, pfyA, width);
                Drawf.laser(laser, laserEnd, x2, y2, i, pfyB, width);
                Draw.color();
                Fx.turbinegenerate.at(i, pfyA);
                Fx.turbinegenerate.at(i, pfyB);
            }

        }
    }
}
