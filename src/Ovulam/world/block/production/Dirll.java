package Ovulam.world.block.production;

import arc.math.geom.Geometry;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.production.Drill;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class Dirll extends Drill {
    public int areaSize = 5;
    public Dirll(String name) {
        super(name);
    }

    public Rect getRect(Rect rect, float x, float y, int rotation){
        rect.setCentered(x, y, areaSize * tilesize);
        float len = tilesize * (areaSize + size)/2f;

        rect.x += Geometry.d4x(rotation) * len;
        rect.y += Geometry.d4y(rotation) * len;

        return rect;
    }

    @Override
    protected void countOre(Tile tile){
        returnItem = null;
        returnCount = 0;

        oreCount.clear();
        itemArray.clear();

        for(Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other)){
                oreCount.increment(getDrop(other), 0, 1);
            }
        }

        for(Item item : oreCount.keys()){
            itemArray.add(item);
        }

        itemArray.sort((item1, item2) -> {
            int type = Boolean.compare(!item1.lowPriority, !item2.lowPriority);
            if(type != 0) return type;
            int amounts = Integer.compare(oreCount.get(item1, 0), oreCount.get(item2, 0));
            if(amounts != 0) return amounts;
            return Integer.compare(item1.id, item2.id);
        });

        if(itemArray.size == 0){
            return;
        }

        returnItem = itemArray.peek();
        returnCount = oreCount.get(itemArray.peek(), 0);
    }

    protected Seq<Tile> getLinkedTilesAs(Tile tile, int size, Rect rect){
        tempTiles.clear();

        for(int dx = 0; dx < size; dx++){
            for(int dy = 0; dy < size; dy++){
                //Tile other = world.tile(x + dx + o, y + dy + o);
                //if(other != null) tempTiles.add(other);
            }
        }

        return tempTiles;
    }
}
