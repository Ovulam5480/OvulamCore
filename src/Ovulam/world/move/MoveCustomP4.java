package Ovulam.world.move;

import arc.math.geom.Point2;

public class MoveCustomP4 extends MoveCustomPoint{
    Point2[] point4 = {
            new Point2(-1, 1),
            new Point2(1, 1),
            new Point2(-1, -1),
            new Point2(1, -1)
    };

    @Override
    public void points(){
        points = point4;
    }

    public MoveCustomP4(int[] point2Index, float distance){
        super(point2Index, distance/2f);
    }

    public MoveCustomP4(float distance){
        super(distance/2f);
    }
}