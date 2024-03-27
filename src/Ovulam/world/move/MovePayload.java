package Ovulam.world.move;

import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public abstract class MovePayload {
    public int maxCapital(Block block){return 1;}
    public Vec2 setTargetPosition(Building build, int index){
        return Vec2.ZERO;
    }

}
