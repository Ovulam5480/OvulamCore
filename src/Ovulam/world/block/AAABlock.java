package Ovulam.world.block;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.scene.ui.layout.Table;
import mindustry.content.UnitTypes;
import mindustry.gen.Building;
import mindustry.world.Block;

public class AAABlock extends Block {
    public AAABlock(String name) {
        super(name);
        update = true;
        sync = true;
        configurable = true;
    }

    @Override
    public void init() {
    }

    public class AAABuild extends Building{
        public FrameBuffer buffer = new FrameBuffer();
        public boolean once = true;

        @Override
        public void configure(Object value) {
            super.configure(value);
        }

        @Override
        public void updateTile(){
        }

        @Override
        public void buildConfiguration(Table table){
            table.defaults().width(216f);
            table.button("set true", () -> once = true);
        }

        @Override
        public void draw(){
            buffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            buffer.begin();
            Draw.rect(UnitTypes.mono.fullIcon, x, y);
            buffer.end();

            Draw.rect(new TextureRegion(buffer.getTexture()), x, y);
            Draw.rect(UnitTypes.mono.fullIcon, x, y);
        }
    }
}
