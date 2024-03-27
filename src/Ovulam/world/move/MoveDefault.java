package Ovulam.world.move;

import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveDefault extends MovePayload{
    @Override
    public int maxCapital(Block block){
        return super.maxCapital(block);
    }
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return super.setTargetPosition(build, index);
    }
}
