package Ovulam.UI;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.core.UI;
import mindustry.gen.Icon;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Styles;


public class RecipeItemImage extends Stack {
    public RecipeItemImage(TextureRegion region, float amount, boolean completely){
        int dig = Mathf.digits((int) amount);
        int number = Mathf.floor(amount);
        float fraction = (float) ((int)amount * 10) / 10;

        add(new Table(o -> {
            o.left();
            o.add(new Image(region)).size(40f).scaling(Scaling.fit);
        }));

        if(amount != 0){
            add(new Table(t -> {
                t.left().bottom();
                t.add((dig > 3 ? UI.formatAmount(number) : (!completely && dig <= 1) ? fraction : number + "") + (completely ? "" : "/")).style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

    public RecipeItemImage(ItemStack stack){
        this(stack.item.uiIcon, stack.amount, true);
    }

    public RecipeItemImage(LiquidStack stack, boolean completely){
        this(stack.liquid.uiIcon, stack.amount, completely);
    }

    public RecipeItemImage(PayloadStack stack){
        this(stack.item.uiIcon, stack.amount, true);
    }

    public RecipeItemImage(float power){
        this(Icon.power.getRegion(), power, false);
    }
}
