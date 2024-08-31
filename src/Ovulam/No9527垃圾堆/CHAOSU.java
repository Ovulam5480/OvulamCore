package Ovulam.No9527垃圾堆;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.util.Time;
import mindustry.graphics.Layer;
import mindustry.world.blocks.defense.OverdriveProjector;

public class CHAOSU extends OverdriveProjector {
    public float 间隔 = 180f;
    public CHAOSU(String name) {
        super(name);
    }

    public class CHAOSUBuild extends OverdriveBuild{
        @Override
        public void draw() {
            super.draw();

            float realRange = range + phaseHeat * phaseRangeBoost;
            float progress = (Time.time % 间隔) / 间隔;

            Lines.stroke((1 - progress) * 4f, baseColor.a(efficiency));
            Draw.z(Layer.effect);

            Lines.circle(x,y,realRange * progress);

            Draw.reset();
            Lines.stroke(1);
        }
    }
}
