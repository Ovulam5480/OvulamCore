package Ovulam.world.drawRecipePayload;

import arc.math.geom.Vec2;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.Payload;

//绘制多载荷工厂内 工作区的载荷(包括未存在的产物载荷)
public abstract class DrawRecipePayload {
    Payload payload;

    public void load(Block block){}

    public void draw(Building building){
        this.draw(building, 0, 0);
    }

    public void draw(Building building, Vec2 vec2) {
        this.draw(building, vec2.x, vec2.y);
    }

    public abstract void draw(Building building, float offsetX, float offsetY);

}
