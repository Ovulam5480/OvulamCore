package Ovulam.world.move;

import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class Moved4 extends MovePayload{
    public float distance;

    public Moved4(){
        this(16);
    }

    public Moved4(float distance){
        this.distance = distance;
    }

    @Override
    public int maxCapacity(Block block){return 4;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return new Vec2(Geometry.d4(index).x * distance,Geometry.d4(index).y * distance);
    }
}
