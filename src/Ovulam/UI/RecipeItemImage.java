package Ovulam.UI;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import arc.util.Strings;
import mindustry.gen.Icon;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Styles;

import static mindustry.core.UI.*;


public class RecipeItemImage extends Stack {
    public float cellSize = 48;
    public RecipeItemImage(TextureRegion region, float amount, boolean completely){
        float decimal = amount % 1;

        add(new Table(o -> {
            o.left();
            o.add(new Image(region)).size(cellSize).scaling(Scaling.fit);
        }));

        if(amount == 0)return;

        add(new Table(t -> {
            t.left().bottom();
            t.add((formatAmount((long) amount, decimal)) + (completely ? "" : "/")).style(Styles.outlineLabel);
        }));
    }

    public String formatAmount(long number, float decimal){
        long mag = Math.abs(number);
        String sign = number < 0 ? "-" : "";

        if(mag >= 1_000_000_000){
            return sign + Strings.fixed(mag / 1_000_000_000f, 1) + "[gray]" + billions + "[]";
        }else if(mag >= 1_000_000){
            return sign + Strings.fixed(mag / 1_000_000f, 1) + "[gray]" + millions + "[]";
        }else if(mag >= 10_000){
            return number / 1000 + "[gray]" + thousands + "[]";
        }else if(mag >= 1000){
            return sign + Strings.fixed(mag / 1000f, 1) + "[gray]" + thousands + "[]";
        }else if(mag >= 10){
            return Mathf.ceil(number + decimal) + "";
        }else {
            return decimal == 0 ? number + "" : sign + Strings.fixed(mag + decimal, 1) + "[]";
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

    //todo timeImage
}
