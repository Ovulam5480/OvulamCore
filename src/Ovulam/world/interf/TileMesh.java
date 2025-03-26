package Ovulam.world.interf;

import arc.math.geom.Position;
import arc.struct.Seq;

public interface TileMesh<T extends Position> {
    Seq<T> movers();


}
