package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class Moved8c extends MovePayload{
    public final static Point2[] d8c = {
            new Point2(1, 0),
            new Point2(1, 1),
            new Point2(0, 1),
            new Point2(-1, 1),
            new Point2(-1, 0),
            new Point2(-1, -1),
            new Point2(0, -1),
            new Point2(1, -1),
            new Point2(0, 0),
    };
    @Override
    public int maxCapacity(Block block){return 9;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return new Vec2(d8c[Mathf.mod(index, 9)].x * 16,d8c[Mathf.mod(index, 9)].y * 16);
    }
}
