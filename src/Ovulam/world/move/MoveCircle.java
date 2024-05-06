package Ovulam.world.move;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;

public class MoveCircle extends MovePayload{
    public float radius;
    public int amount;
    @Override
    public int maxCapacity(Block block) {
        return amount;
    }

    @Override
    public Vec2 setTargetPosition(Building build, int index) {
        float rad = Mathf.degreesToRadians * index / amount * 360;
        return new Vec2((float) Math.cos(rad) * radius, (float) Math.sin(rad) * radius);
    }

    public MoveCircle(float radius){
        this(radius, 16);
    }

    public MoveCircle(float radius, int amount){
        this.radius = radius;
        this.amount = amount;
    }
}
