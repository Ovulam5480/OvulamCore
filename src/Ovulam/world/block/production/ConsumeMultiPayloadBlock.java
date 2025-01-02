package Ovulam.world.block.production;

import Ovulam.world.block.payload.MultiPayloadBlock;
import Ovulam.world.consumers.ConsumeLiquidsDynamicCompletely;
import Ovulam.world.consumers.ConsumePowerDynamicCanBeNegative;
import arc.struct.ObjectMap;
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
import Ovulam.world.type.PositionPayload;

//对载荷和其他资源进行消耗的方块
public class ConsumeMultiPayloadBlock extends MultiPayloadBlock {
    public ConsumeMultiPayloadBlock(String name) {
        super(name);
        size = 15;

        consume(new ConsumeItemDynamic((ConsumeMultiPayloadBuild e) -> e.getInputItems().toArray(ItemStack.class)));
        consume(new ConsumeLiquidsDynamic((ConsumeMultiPayloadBuild e) -> !e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));
        consume(new ConsumeLiquidsDynamicCompletely((ConsumeMultiPayloadBuild e) -> e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));
        consume(new ConsumePositionPayloadsDynamic((ConsumeMultiPayloadBuild e) -> e.getInputPayloads().toArray(PayloadStack.class)));
        consume(new ConsumePowerDynamicCanBeNegative(ConsumeMultiPayloadBuild::getInputPower));
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (ConsumeMultiPayloadBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    public abstract class ConsumeMultiPayloadBuild extends MultiPayloadBlockBuild{
        public float progress;
        public float totalProgress;
        public float warmup;

        @Override
        public float progress() {
            return progress;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack != null && items.get(stack.item) < getMaximumAccepted(item);
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

        public abstract boolean getInputLiquidsCompletely();

        public abstract Seq<PayloadStack> getInputPayloads();

        public abstract float getInputPower();

        public abstract ObjectMap<UnlockableContent, MovePayload> getInputMover();

        public abstract float getCraftTime();


        //只消耗载荷时, 所有的positionPayload都属于输入
        public void moveInPayloads(){
            positionPayloads.each(positionPayload -> {
                MovePayload movePayload = getInputMover().get(positionPayload.content());

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
