package Ovulam;

import tmi.RecipeEntry;
import tmi.TooManyItems;

public class OvulamTMIRecipes extends RecipeEntry {
    @Override
    public void init() {
        TooManyItems.recipesManager.registerParser(new OvulamTMIRecipeParser.MultiPayloadCrafterRecipeParser());
    }

    @Override
    public void afterInit() {
    }
}
