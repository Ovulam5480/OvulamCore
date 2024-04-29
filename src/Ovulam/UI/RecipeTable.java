package Ovulam.UI;

import Ovulam.world.type.Recipe;
import arc.scene.ui.layout.Table;

public class RecipeTable {
    public static void addRecipeTable(Table table, Recipe recipe) {
        table.defaults().size(48).pad(8);
        recipe.itemStacks.each(itemStack -> table.add(new RecipeItemImage(itemStack)));
        table.row();
        recipe.liquidStacks.each(liquidStack -> table.add(new RecipeItemImage(liquidStack, recipe.liquidCompletely)));
        table.row();
        recipe.payloadStacks().each(payloadStack -> table.add(new RecipeItemImage(payloadStack)));
        if (recipe.power > 0){
            table.row();
            table.add(new RecipeItemImage(recipe.power));
        }
    }
}
