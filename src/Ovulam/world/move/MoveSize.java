package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.IntSeq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveSize extends MovePayload{
    public float distance;

    private final IntSeq seq = IntSeq.with(1, -1, 2);

    public MoveSize(){
        this(16);
    }

    public MoveSize(float distance){
        this.distance = distance;
    }

    //总共的容量
    @Override
    public int maxCapacity(Block block){
        return sizeCapital(block) * 3;
    }

    //每个边的容量
    public int sizeCapital(Block block){
        return Mathf.floor(block.size * Vars.tilesize / distance - Mathf.FLOAT_ROUNDING_ERROR);
    }

    @Override
    public Vec2 setTargetPosition(Building build, int index){
        int amount = sizeCapital(build.block);
        //位于第几个边
        int rotation = build.rotation + seq.get(index / amount);

        index = Mathf.mod(index, amount);
        float trns = build.block.size * Vars.tilesize / 2f;

        float x = Geometry.d4(rotation).x * (trns - distance / 2f);
        float y = Geometry.d4(rotation).y * (trns - distance / 2f);

        boolean isOdd = amount % 2 == 1;

        return new Vec2(
                x + getAmazingNumber(isOdd, index) * distance * Geometry.d4(rotation).y,
                y + getAmazingNumber(isOdd, index) * distance * Geometry.d4(rotation).x
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