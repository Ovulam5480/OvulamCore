package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveSize extends MovePayload{
    @Override
    public int maxCapital(Block block){return sizeCapital(block) * 3;}
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        int sideAmount = Mathf.mod(index, sizeCapital(build.block));
        int rotation = build.rotation + index / sizeCapital(build.block) + 1;
        float offsize = - sizeCapital(build.block) / 2f + sideAmount + 0.5f;

        float trns = build.block.size / 2f - 1;
        return new Vec2(
                (trns * Geometry.d4x(rotation) + Mathf.pow(-1, rotation) *(offsize * 2) * Geometry.d4y(rotation)) * 8,
                (trns * Geometry.d4y(rotation) + Mathf.pow(-1, rotation) *(offsize * 2) * Geometry.d4x(rotation)) * 8
        );
    }

    //4 : 0   5 : 1   6 : 1   7 : 2
    public int sizeCapital(Block block){return Math.max((block.size - 3) / 2, 0);}
}