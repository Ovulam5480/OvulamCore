package Ovulam.world.type;

import arc.math.geom.Vec2;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.world.blocks.payloads.Payload;

public class PositionPayload{
    public Payload payload;
    public Vec2 currentPosition;
    public Vec2 targetPosition;

    public PositionPayload(Payload payload, Vec2 currentPosition, Vec2 targetPosition){
        this.payload = payload;
        this.currentPosition = currentPosition;
        this.targetPosition = targetPosition;
    }

    public UnlockableContent content(){
        return payload.content();
    }

    public float x(Building building){
        return currentPosition.x + building.x;
    }

    public float y(Building building){
        return currentPosition.y + building.y;
    }

}