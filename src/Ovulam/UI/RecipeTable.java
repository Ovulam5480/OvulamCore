package Ovulam.UI;

import Ovulam.world.other.Recipe;
import arc.scene.ui.layout.Table;

public class RecipeTable {
    public static void addRecipeTable(Table table, Recipe recipe) {
        recipe.itemStacks.each(itemStack -> table.add(new RecipeItemImage(itemStack)));
        table.row();
        recipe.liquidStacks.each(liquidStack -> table.add(new RecipeItemImage(liquidStack, recipe.liquidCompletely)));
        table.row();
        recipe.payloadStacks.each(payloadStack -> table.add(new RecipeItemImage(payloadStack)));
        table.row();
        if (recipe.power > 0) table.add(new RecipeItemImage(recipe.power));
    }
}
