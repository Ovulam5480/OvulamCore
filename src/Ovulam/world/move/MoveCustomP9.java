package Ovulam.world.move;

import arc.math.geom.Point2;

public class MoveCustomP9 extends MoveCustomPoint{
    public static Point2[] points9 = {
            new Point2(-2, 2),
            new Point2(0, 2),
            new Point2(2, 2),
            new Point2(-2, 0),
            new Point2(0, 0),
            new Point2(2, 0),
            new Point2(-2, -2),
            new Point2(0, -2),
            new Point2(2, -2),
    };

    @Override
    public void points(){
        points = points9;
    }

    public MoveCustomP9(int[] point2Index, float distance){
        super(point2Index, distance/2f);
    }

    public MoveCustomP9(float distance){
        super(distance/2f);
    }

}
