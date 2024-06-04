package Ovulam.world.block;

import Ovulam.world.graphics.OvulamShaders;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.world.Block;

public class AAABlock extends Block {
    public AAABlock(String name) {
        super(name);
        update = true;
        sync = true;
    }

    @Override
    public void init() {
    }

    public class AAABuild extends Building{
        public FrameBuffer buffer = new FrameBuffer();

        @Override
        public void configure(Object value) {
            super.configure(value);
        }

        @Override
        public void updateTile(){
        }

        @Override
        public void draw(){
            OvulamShaders.alpha.alpha = 0.5f + Mathf.sin(Time.time / 10f) / 2;
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            buffer.begin();
            Draw.color(Color.pink);
            Draw.flush();
            Draw.color();
            buffer.end();
        }
    }
}
