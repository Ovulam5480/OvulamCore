package Ovulam.world.block.block.EventBlock;

import arc.graphics.g2d.Draw;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.Block;

import static arc.Core.camera;
import static mindustry.Vars.tilesize;

public class EventBlock extends Block {
    public EventBlock(String name) {
        super(name);
        update = true;
        //destructible = true;
        clipSize = 65536 * tilesize;
    }
    public class EventBlockBuild extends Building{
        @Override
        public void updateTile(){

        }

        @Override
        public void draw(){
            super.draw();
            float cameraScl = 1 / Vars.renderer.getDisplayScale() * tilesize;

            Draw.scl(cameraScl);
            Draw.z(Layer.fogOfWar + 1f);

            drawCamera(camera.position.x, camera.position.y, cameraScl);

            Draw.reset();
        }

        public void drawCamera(float cameraX, float cameraY, float tilesizeScl){
        }
    }
}
