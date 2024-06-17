package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveOut extends MovePayload{
    public float distance;

    public MoveOut(){
        this(16);
    }

    public MoveOut(float distance){
        this.distance = distance;
    }

    @Override
    public int maxCapacity(Block block){
        return Mathf.floor(block.size * Vars.tilesize / distance - Mathf.FLOAT_ROUNDING_ERROR);
    }

    @Override
    public Vec2 setTargetPosition(Building build, int index){
        int amount = maxCapacity(build.block);
        int row = index / amount;
        index = Mathf.mod(index, amount);

        float trns = build.block.size * Vars.tilesize / 2f;

        float x = Geometry.d4(build.rotation).x * (trns - row * distance);
        float y = Geometry.d4(build.rotation).y * (trns - row * distance);

        boolean isOdd = amount % 2 == 1;

        return new Vec2(
                x + getAmazingNumber(isOdd, index) * distance * Geometry.d4(build.rotation).y,
                y + getAmazingNumber(isOdd, index) * distance * Geometry.d4(build.rotation).x
        );
    }

    public float getAmazingNumber(boolean isOdd, int index){
        float begin = 0;
        boolean b = true;
        //+1 -2 +3 -4 ......
        for (int i = 0; i < index; i++){
            begin += Mathf.sign(b) * (i + 1);
            b = !b;
        }

        if(!isOdd) begin -= 0.5f;
        return begin;
    }
}

