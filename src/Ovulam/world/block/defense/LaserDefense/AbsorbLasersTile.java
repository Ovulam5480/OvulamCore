package Ovulam.world.block.defense.LaserDefense;

import mindustry.world.Tile;

public class AbsorbLasersTile extends Tile {
    public AbsorbLasersTile(int x, int y) {
        super(x, y);
        this.block = new AbsorbLasersBlock("000");
    }
}
