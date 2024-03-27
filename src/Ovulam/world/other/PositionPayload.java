package Ovulam.world.other;

import arc.math.geom.Vec2;
import mindustry.ctype.UnlockableContent;
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

}