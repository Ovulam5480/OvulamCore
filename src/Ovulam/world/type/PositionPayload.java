package Ovulam.world.type;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;

import static mindustry.Vars.emptyTile;

public class PositionPayload{
    public Payload payload;
    public Vec2 currentPosition;
    public Vec2 targetPosition;
    public boolean isBuildPayload;

    public PositionPayload(Payload payload, Vec2 currentPosition, Vec2 targetPosition){
        this.payload = payload;
        this.currentPosition = currentPosition;
        this.targetPosition = targetPosition;
        isBuildPayload = payload instanceof BuildPayload;
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

    public void draw(){
        if (isBuildPayload) {
            drawBuildPayload((BuildPayload) payload);
        } else {
            drawUnitPayload((UnitPayload) payload);
        }
    }

    //与建筑载荷的渲染相同, 但是没有drawShadow()
    public void drawBuildPayload(BuildPayload buildPayload){
        Building building = buildPayload.build;
        float prevZ = Draw.z();
        Draw.z(prevZ - 0.0001f);
        Draw.z(prevZ);
        Draw.zTransform(z -> z >= Layer.flyingUnitLow + 1f ? z : 0.0011f + Math.min(Mathf.clamp(z, prevZ - 0.001f, prevZ + 0.9f), Layer.flyingUnitLow - 1f));
        building.tile = emptyTile;
        building.payloadDraw();
        Draw.zTransform();
        Draw.z(prevZ);
    }

    public void drawUnitPayload(UnitPayload unitPayload){
        unitPayload.draw();
    }
    @Override
    public String toString(){
        return payload.content().localizedName + " at " + currentPosition.toString();
    }

}