package Ovulam.UI;

import Ovulam.world.other.Recipe;
import arc.scene.ui.layout.Table;
import mindustry.ui.ItemImage;

public class RecipeTable {
    public static void addRecipeTable(Table table, Recipe recipe, int Row){
        for (int i = 0; i < recipe.itemStacks.size; i++) {
            ItemImage itemImage = new ItemImage(recipe.itemStacks.get(i));

            table.add(itemImage).center();
            if (i % Row == Row - 1) table.row();
        }
        table.row();
        for (int i = 0; i < recipe.liquidStacks.size; i++) {
            LiquidImage liquidImage = new LiquidImage(recipe.liquidStacks.get(i), recipe.liquidCompletely);

            table.add(liquidImage).center();
            if (i % Row == Row - 1) table.row();
        }

        table.row();
        for (int i = 0; i < recipe.payloadStacks.size; i++) {
            ItemImage payloadImage = new ItemImage(recipe.payloadStacks.get(i));

            table.add(payloadImage).center();
            if (i % Row == Row - 1) table.row();
        }
        table.row();

        if (recipe.Power > 0) {
            LiquidImage powerImage = new LiquidImage(recipe.Power);

            table.add(powerImage).center();
            table.row();
        }
    }
}
