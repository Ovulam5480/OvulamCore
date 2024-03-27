package Ovulam.world.block;

import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class ItemRepeater extends Block {
    public float range = 20;

    public ItemRepeater(String name){
        super(name);
        hasItems = true;
        acceptsItems = true;
        update = true;
        sync = true;
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashRect(Pal.accent,
                x * tilesize + offset - range * tilesize / 2,
                y * tilesize + offset - range * tilesize / 2,
                range * tilesize, range * tilesize);
    }

    public class ItemRepeaterBuild extends Building {
        @Override
        public void drawConfigure(){
        }

        @Override
        public void updateTile(){
        }

        @Override
        public void draw(){
        }
    }
}
