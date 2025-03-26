package Ovulam.world.consumers;

import Ovulam.UI.ContentImage;
import Ovulam.world.block.payload.MultiPayloadBlock;
import arc.func.Func;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.PayloadStack;
import mindustry.ui.ReqImage;
import mindustry.world.consumers.Consume;

public class ConsumePositionPayloadsDynamic extends Consume {
    public final Func<Building, PayloadStack[]> positionPayloadStack;

    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumePositionPayloadsDynamic(Func<T, PayloadStack[]> PositionPayloadStack){
        this.positionPayloadStack = (Func<Building, PayloadStack[]>) PositionPayloadStack;
    }

    @Override
    public void build(Building build, Table table){
        PayloadStack[][] current = {positionPayloadStack.get(build)};

        table.table(cont -> {
            table.update(() -> {
                if(current[0] != positionPayloadStack.get(build)){
                    rebuild(build, cont);
                    current[0] = positionPayloadStack.get(build);
                }
            });

            rebuild(build, cont);
        });
    }

    private void rebuild(Building build, Table table){
        table.clear();
        int i = 0;

        for(PayloadStack stack : positionPayloadStack.get(build)){
            int amount = ((MultiPayloadBlock.MultiPayloadBlockBuild)build).getPayloadAmount(stack.item);
            table.add(new ReqImage(new ContentImage(stack),
                    () -> amount >= stack.amount)).size(Vars.iconMed).padRight(8);
            if(++i % 4 == 0) table.row();
        }
    }

    @Override
    public float efficiency(Building build){
        for (PayloadStack payloadStack : positionPayloadStack.get(build)){
            if(((MultiPayloadBlock.MultiPayloadBlockBuild) build).getPayloadAmount(payloadStack.item) < payloadStack.amount){
                return 0;
            }
        }
        return 1f;
    }

    @Override
    public void trigger(Building build){
        for (PayloadStack payloadStack : positionPayloadStack.get(build)){
            for (int i = 0; i < payloadStack.amount; i++){
                ((MultiPayloadBlock.MultiPayloadBlockBuild) build).positionPayloads.remove
                        (positionPayload -> positionPayload.content() == payloadStack.item);
            }
        }
    }
}
