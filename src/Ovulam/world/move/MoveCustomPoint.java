package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Point2;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public abstract class MoveCustomPoint extends MovePayload{
    public int[] index;
    public float distance;
    public static Point2[] points = {
            new Point2(0, 0),
    };

    public abstract void points();

    public MoveCustomPoint(int[] point2Index, float distance){
        this.index = point2Index;
        this.distance = distance;
    }

    public MoveCustomPoint(float distance){
        this.distance = distance;
    }

    public Point2[] currentPoints(){
        points();
        if(index == null) return points;

        Point2[] point2s = new Point2[index.length];
        for (int i = 0; i < index.length; i++) point2s[i] = points[index[i]];
        return point2s;
    }


    @Override
    public int maxCapacity(Block block){return currentPoints().length;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        return new Vec2(
                currentPoints()[Mathf.mod(index,currentPoints().length)].x * distance,
                currentPoints()[Mathf.mod(index,currentPoints().length)].y * distance);
    }
}
