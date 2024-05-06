package Ovulam.world.drawRecipePayload;

import arc.math.geom.Vec2;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.world.Block;

//绘制多载荷工厂内 工作区的载荷(包括未存在的产物载荷)
//记得draw影子
public abstract class DrawRecipePayload {
    public void init(Block block, UnlockableContent payload){}

    public void load(Block block){}

    public void draw(UnlockableContent payload, Building building, float progress){
        this.draw(payload, building, progress, 0, 0);
    }

    public void draw(UnlockableContent payload,Building building, float progress, Vec2 vec2) {
        this.draw(payload, building, progress, vec2.x, vec2.y);
    }

    public abstract void draw(UnlockableContent payload,Building building, float progress, float offsetX, float offsetY);

}
