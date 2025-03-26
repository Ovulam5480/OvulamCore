package Ovulam.UI;

import Ovulam.world.type.Recipe;
import arc.scene.ui.layout.Table;

public class RecipeTable {
    public static void addRecipeTable(Table table, Recipe recipe, float size) {
        table.defaults().size(size).pad(8);
        recipe.itemStacks.each(itemStack -> table.add(new ContentImage(itemStack)));
        table.row();
        recipe.liquidStacks.each(liquidStack -> table.add(new ContentImage(liquidStack, recipe.liquidCompletely)));
        table.row();
        recipe.payloadStacks().each(payloadStack -> table.add(new ContentImage(payloadStack)));
        if (recipe.power > 0){
            table.row();
            table.add(new ContentImage(recipe.power * 60));
        }
        table.defaults().reset();
    }

    public static void addRecipeTable(Table table, Recipe recipe) {
        addRecipeTable(table, recipe, 48);
    }
}
