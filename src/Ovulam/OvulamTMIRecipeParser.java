package Ovulam;

/*
import tmi.recipe.Recipe;
import tmi.recipe.RecipeParser;
import tmi.recipe.RecipeType;

public class OvulamTMIRecipeParser {
    //名字真的好长啊
    public static class MultiPayloadCrafterRecipeParser extends RecipeParser<MultiPayloadCrafter> {

        public void planToRecipe(Recipe recipe, float craftTime, Ovulam.world.other.Recipe planIn,
                                 Ovulam.world.other.Recipe planOut){
            recipe.setTime(craftTime);

            planIn.itemStacks.forEach(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
            planIn.liquidStacks.forEach(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid),
                    planIn.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
            planIn.payloadStacks.forEach(payloadStack -> recipe.addMaterial(getWrap(payloadStack.item), payloadStack.amount));

            planOut.itemStacks.forEach(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
            planOut.liquidStacks.forEach(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid),
                    planOut.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
            planOut.payloadStacks.forEach(payloadStack -> recipe.addMaterial(getWrap(payloadStack.item), payloadStack.amount));
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
}

 */

