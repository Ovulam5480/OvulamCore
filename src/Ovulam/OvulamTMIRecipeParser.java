package Ovulam;

//import Ovulam.world.block.block.ManufacturerBlock;
//import Ovulam.world.block.production.MultiPayloadCrafter;
//import arc.struct.Seq;
//import mindustry.world.Block;
//import org.jetbrains.annotations.NotNull;
//import tmi.recipe.Recipe;
//import tmi.recipe.RecipeParser;
//import tmi.recipe.RecipeType;
//import tmi.recipe.types.PowerMark;
//
//public class OvulamTMIRecipeParser{
//
//    //名字真的好长啊
//    public static class MultiPayloadCrafterRecipeParser extends RecipeParser<MultiPayloadCrafter> {
//
//        public void planToRecipe(Recipe recipe, float craftTime, Ovulam.world.type.Recipe planIn,
//                                 Ovulam.world.type.Recipe planOut){
//            recipe.setTime(craftTime);
//
//            planIn.itemStacks.each(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
//            planIn.liquidStacks.each(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid), planIn.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
//            planIn.payloadStacks().each(ps -> recipe.addMaterial(getWrap(ps.item), ps.amount));
//            if(planIn.power > 0) recipe.addMaterialPersec(PowerMark.INSTANCE, planIn.power);
//
//            planOut.itemStacks.each(itemStack -> recipe.addProduction(getWrap(itemStack.item), itemStack.amount));
//            planOut.liquidStacks.each(liquidStack -> recipe.addProduction(getWrap(liquidStack.liquid), planOut.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
//            planOut.payloadStacks().each(ps -> recipe.addProduction(getWrap(ps.item), ps.amount));
//            if(planOut.power > 0)recipe.addProductionPersec(PowerMark.INSTANCE, planOut.power);
//        }
//
//        @Override
//        public boolean isTarget(@NotNull Block block) {
//            return block instanceof MultiPayloadCrafter;
//        }
//
//        @NotNull
//        @Override
//        public Seq<Recipe> parse(@NotNull MultiPayloadCrafter block) {
//            Seq<Recipe> recipes = new Seq<>();
//
//            block.plans.each(plan -> {
//                Recipe recipe = new Recipe(RecipeType.factory);
//                recipe.setBlock(getWrap(block));
//                planToRecipe(recipe, plan.craftTime, plan.inputRecipe, plan.outputRecipe);
//                recipes.add(recipe);
//            });
//
//            return recipes;
//        }
//    }
//
//    public static class ManufacturerBlockRecipeParser extends RecipeParser<ManufacturerBlock> {
//
//        public void planToRecipe(Recipe recipe, float craftTime, Ovulam.world.type.Recipe planIn){
//            recipe.setTime(craftTime);
//
//            planIn.itemStacks.each(itemStack -> recipe.addMaterial(getWrap(itemStack.item), itemStack.amount));
//            planIn.liquidStacks.each(liquidStack -> recipe.addMaterial(getWrap(liquidStack.liquid),
//                    planIn.liquidCompletely ? liquidStack.amount : liquidStack.amount / craftTime));
//            planIn.payloadStacks().each(ps -> recipe.addMaterial(getWrap(ps.item), ps.amount));
//            if(planIn.power > 0) recipe.addMaterialPersec(PowerMark.INSTANCE, planIn.power);
//        }
//
//        @Override
//        public boolean isTarget(@NotNull Block block) {
//            return block instanceof ManufacturerBlock;
//        }
//
//        @NotNull
//        @Override
//        public Seq<Recipe> parse(ManufacturerBlock block) {
//            Seq<Recipe> recipes = new Seq<>();
//
//            block.stages.each(stage -> {
//                Recipe recipe = new Recipe(RecipeType.building);
//                recipe.setBlock(getWrap(block.targetBlock));
//                planToRecipe(recipe, stage.craftTime, stage.inputRecipe);
//                recipes.add(recipe);
//            });
//            return recipes;
//        }
//    }
//}

