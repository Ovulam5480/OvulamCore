package Ovulam.world.move;

import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveOut extends MovePayload{

    //todo 为啥
    @Override
    public int maxCapital(Block block){return (block.size - 1) / 2;}

    // 3:1   4:1   5:2
    @Override
    public Vec2 setTargetPosition(Building build, int index){
        float trns = build.block.size / 2f;
        int amount = (build.block.size - 1) / 2;
        float half = trns / 2;
        int row = index / amount;

        float x = Geometry.d4(build.rotation).x * (trns - row * 2) * 8;
        float y = Geometry.d4(build.rotation).y * (trns - row * 2) * 8;

        return new Vec2(
                x + (index % amount + 1 - half) * 16 * Geometry.d4(build.rotation).y,
                y + (index % amount + 1 - half) * 16 * Geometry.d4(build.rotation).x);
    }

}

