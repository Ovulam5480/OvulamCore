package Ovulam.world.block.payload;

import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import Ovulam.world.consumers.ConsumePositionPayloadsDynamic;
import Ovulam.world.move.MovePayload;
import Ovulam.world.other.PositionPayload;
import Ovulam.world.other.RecipeMover;

public class ConsumeMultiPayloadBlock extends MultiPayloadBlock{
    public ConsumeMultiPayloadBlock(String name) {
        super(name);
        size = 15;
        hasLiquids = true;
        hasItems = true;
        acceptsItems = true;
        solid = true;
        itemCapacity = 10;
        liquidCapacity = 100f;

        consume(new ConsumeItemDynamic((ConsumeMultiPayloadBuild e) -> e.getInputItems().toArray()));
        consume(new ConsumeLiquidsDynamic((ConsumeMultiPayloadBuild e) -> e.getInputLiquids().toArray()));
        consume(new ConsumePositionPayloadsDynamic(ConsumeMultiPayloadBuild::getInputPayloads));
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (ConsumeMultiPayloadBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    public abstract class ConsumeMultiPayloadBuild extends MultiPayloadBlockBuild{

        @Override
        public boolean acceptItem(Building source, Item item){
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack != null && stack.amount < getMaximumAccepted(item);
        }


        //todo 潜在的空指针异常问题
        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            return getInputLiquids().contains(liquidStack -> liquidStack.liquid == liquid);
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            PayloadStack stack = getInputPayloads().find(payloadStack -> payloadStack.item == payload.content());
            return stack != null && getPayloadAmount(payload) < stack.amount;
        }

        public abstract Seq<ItemStack> getInputItems();

        public abstract Seq<LiquidStack> getInputLiquids();

        public abstract Seq<PayloadStack> getInputPayloads();

        public abstract RecipeMover[] getMoveInMover();

        public MovePayload findMovePayload(RecipeMover[] getRecipeMovers,UnlockableContent payload) {
            MovePayload recipeMover = null;
            for (RecipeMover moveInMover1 : getRecipeMovers) {
                if (moveInMover1.unlockableContent == payload) {
                    recipeMover = moveInMover1.movePayload;
                    break;
                }
            }
            return recipeMover;
        }

        public void moveInPayloads(){
            positionPayloads.forEach(positionPayload -> {
                MovePayload movePayload = findMovePayload(getMoveInMover(), positionPayload.content());

                int index = 0;
                for (PositionPayload positionPayload1 : positionPayloads){
                    if (positionPayload1.content() == positionPayload.content()) {
                        if (positionPayload1 == positionPayload) break;
                        index++;
                    }
                }
                positionPayload.targetPosition = setTargetPosition(index, movePayload);
                updatePayload(positionPayload);
            });
        }
    }

}
