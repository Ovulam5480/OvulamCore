package Ovulam;

import Ovulam.world.block.block.ManufacturerBlock;
import Ovulam.world.block.production.MultiPayloadCrafter;
import arc.struct.Seq;
import mindustry.world.Block;
import tmi.recipe.Recipe;
import tmi.recipe.RecipeParser;
import tmi.recipe.RecipeType;
import tmi.recipe.types.PowerMark;

public class OvulamTMIRecipeParser{

    //名字真的好长啊
    public static class MultiPayloadCrafterRecipeParser extends RecipeParser<MultiPayloadCrafter> {

        public void planToRecipe(Recipe recipe, float craftTime, Ovulam.world.type.Recipe planIn,
                                 Ovulam.world.type.Recipe planOut){
            recipe.setTime(craftTime);

            planIn.itemStacks.forEach(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
            planIn.liquidStacks.forEach(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid),
                    planIn.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
            planIn.payloadStacks().forEach(payloadStack -> recipe.addMaterial(getWrap(payloadStack.item), payloadStack.amount));
            if(planIn.power > 0) recipe.addMaterialPresec(PowerMark.INSTANCE, planIn.power);

            planOut.itemStacks.forEach(itemStack -> recipe.addProduction(getWrap(itemStack.item), itemStack.amount));
            planOut.liquidStacks.forEach(liquidStack -> recipe.addProduction(getWrap(liquidStack.liquid),
                    planOut.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
            planOut.payloadStacks().forEach(payloadStack -> recipe.addProduction(getWrap(payloadStack.item), payloadStack.amount));
            if(planOut.power > 0)recipe.addProductionPresec(PowerMark.INSTANCE, planOut.power);
        }

        @Override
        public boolean isTarget(Block block) {
            return block instanceof MultiPayloadCrafter;
        }

        @Override
        public Seq<Recipe> parse(MultiPayloadCrafter block) {
            Seq<Recipe> recipes = new Seq<>();

            MultiPayloadCrafter.plans.each(plan -> {
                Recipe recipe = new Recipe(RecipeType.factory);
                recipe.setBlock(getWrap(block));
                planToRecipe(recipe, plan.craftTime, plan.inputRecipe, plan.outputRecipe);
                recipes.add(recipe);
            });
            return recipes;
        }
    }

    public static class ManufacturerBlockRecipeParser extends RecipeParser<ManufacturerBlock> {

        public void planToRecipe(Recipe recipe, float craftTime, Ovulam.world.type.Recipe planIn){
            recipe.setTime(craftTime);

            planIn.itemStacks.forEach(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
            planIn.liquidStacks.forEach(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid),
                    planIn.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
            planIn.payloadStacks().forEach(payloadStack -> recipe.addMaterial(getWrap(payloadStack.item), payloadStack.amount));
            if(planIn.power > 0) recipe.addMaterialPresec(PowerMark.INSTANCE, planIn.power);
        }

        @Override
        public boolean isTarget(Block block) {
            return block instanceof ManufacturerBlock;
        }

        @Override
        public Seq<Recipe> parse(ManufacturerBlock block) {
            Seq<Recipe> recipes = new Seq<>();

            ManufacturerBlock.stages.each(stage -> {
                Recipe recipe = new Recipe(RecipeType.building);
                recipe.setBlock(getWrap(block.targetBlock));
                planToRecipe(recipe, stage.craftTime, stage.inputRecipe);
                recipes.add(recipe);
            });
            return recipes;
        }
    }
}

