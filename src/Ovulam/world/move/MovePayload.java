package Ovulam.world.move;

import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

//默认载荷2边长
public abstract class MovePayload {
    public abstract int maxCapacity(Block block);
    public abstract Vec2 setTargetPosition(Building build, int index);

}
