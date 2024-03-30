package Ovulam.world.other;

import arc.struct.Seq;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;

public class Recipe {
    public Seq<ItemStack> itemStacks;
    public Seq<LiquidStack> liquidStacks;
    public Seq<PayloadStack> payloadStacks;
    public float Power;
    public boolean liquidCompletely;

    public Recipe(Seq<ItemStack> itemStacks, Seq<LiquidStack> liquidStacks, Seq<PayloadStack> payloadStacks,
                  float Power, boolean liquidCompletely) {
        this.itemStacks = itemStacks;
        this.liquidStacks = liquidStacks;
        this.payloadStacks = payloadStacks;
        this.Power = Power;
        this.liquidCompletely = liquidCompletely;
    }
}