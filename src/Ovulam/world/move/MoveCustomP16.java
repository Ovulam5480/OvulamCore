package Ovulam.world.move;

import arc.math.geom.Point2;

public class MoveCustomP16 extends MoveCustomPoint{
    Point2[] point16 = {
            new Point2(-3, 3),
            new Point2(-1, 3),
            new Point2(1, 3),
            new Point2(3, 3),
            new Point2(-3, 1),
            new Point2(-1, 1),
            new Point2(1, 1),
            new Point2(3, 1),
            new Point2(-3, -1),
            new Point2(-1, -1),
            new Point2(1, -1),
            new Point2(3, -1),
            new Point2(-3, -3),
            new Point2(-1, -3),
            new Point2(1, -3),
            new Point2(3, -3),
    };


    @Override
    public void points(){
        points = point16;
    }

    public MoveCustomP16(int[] point2Index, float distance){
        super(point2Index,distance/2f);
    }

    public MoveCustomP16(float distance){
        super(distance/2f);
    }

}