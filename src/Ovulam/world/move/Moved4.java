package Ovulam.world.move;

import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class Moved4 extends MovePayload{
    @Override
    public int maxCapital(Block block){return 4;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return new Vec2(Geometry.d4(index).x * 16,Geometry.d4(index).y * 16);
    }
}
