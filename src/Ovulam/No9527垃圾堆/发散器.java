package Ovulam.No9527垃圾堆;

import arc.Events;
import arc.func.Boolf;
import arc.math.geom.Rect;
import arc.util.Log;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Build;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.blocks.sandbox.ItemSource;

import static mindustry.Vars.tilesize;

public class 发散器 extends Block {
    public float range = 20f;
    public 发散器(String name) {
        super(name);
        hasItems = true;
        acceptsItems = true;
        //光传不需要容量
        itemCapacity = 0;
        destructible = true;
        canOverdrive = false;
        update = true;

    }

    public Rect rangeRect(float x, float y, float range) {
        return Tmp.r1.set(x - range, y - range, range * 2, range * 2);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * 8 + offset, y * 8 + offset, range * tilesize));
    }

    public class 发散器Building extends Building {

        public boolean vailBuild(Building target, Item item){
            //eff
            return target.block.hasItems
                    && (target.block instanceof GenericCrafter || target.block instanceof Separator)
                    && target.acceptItem(this, item)
                    && (!hasConsumers || efficiency == 1);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, item) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y)) != null;
        }

        @Override
        public void handleItem(Building source, Item item) {
            Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, item) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y)).handleItem(this, item);
        }

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
        }

    }
}
