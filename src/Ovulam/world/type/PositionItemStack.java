package Ovulam.world.type;

import Ovulam.gen.Stackerc;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.Tile;

public class PositionItemStack implements Stackerc {

    @Override
    public String toString() {
        return item + ": " + amount;
    }
}
