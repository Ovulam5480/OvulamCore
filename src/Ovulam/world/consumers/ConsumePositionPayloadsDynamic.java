package Ovulam.world.consumers;

import arc.func.Func;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.type.PayloadStack;
import mindustry.world.consumers.Consume;
import Ovulam.world.block.payload.MultiPayloadBlock;

public class ConsumePositionPayloadsDynamic extends Consume {
    public final Func<Building, Seq<PayloadStack>> positionPayloadStack;
    //todo build

    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumePositionPayloadsDynamic(Func<T, Seq<PayloadStack>> PositionPayloadStack){
        this.positionPayloadStack = (Func<Building, Seq<PayloadStack>>) PositionPayloadStack;
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
