package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class Moved8edge extends MovePayload{
    public float distance;

    public Moved8edge(){
        this(16);
    }

    public Moved8edge(float distance){
        this.distance = distance;
    }
    @Override
    public int maxCapacity(Block block){return 4;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return new Vec2(Geometry.d8edge[Mathf.mod(index, 4)].x * distance,Geometry.d8edge[Mathf.mod(index, 4)].y * distance);
    }
}