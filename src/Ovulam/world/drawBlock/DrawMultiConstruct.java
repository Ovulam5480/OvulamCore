package Ovulam.world.drawBlock;

import arc.graphics.g2d.Draw;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.PayloadStack;
import mindustry.world.draw.DrawBlock;
import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.move.MovePayload;

//在方块内造着很多奇怪的东西
public class DrawMultiConstruct extends DrawBlock {
    public void draw(Building build){
        if(!(build instanceof MultiPayloadCrafter.MultiPayloadCrafterBuild crafterBuild)){return;}
        Seq<PayloadStack> outputs = crafterBuild.getCurrentPlan().outputRecipe.payloadStacks;

        if(outputs == null) return;

        for (var output : outputs){
            for (int i = 0; i < output.amount; i++){
                MovePayload movePayload = crafterBuild.findMovePayload(output.item);
                Vec2 vec2 = movePayload == null ? Vec2.ZERO : crafterBuild.setTargetPosition(i, movePayload);

                //todo 单位方向和方块的方向
                Draw.draw(Layer.blockOver + 1f,() ->
                        Drawf.construct(vec2.x + build.x, vec2.y + build.y,output.item.fullIcon,
                        0, build.progress(),1f, Time.time));

            }
        }

    }
}
