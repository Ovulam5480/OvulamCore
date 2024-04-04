package Ovulam.UI;

import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.ui.Image;
import arc.scene.ui.layout.Stack;
import arc.scene.ui.layout.Table;
import arc.util.Scaling;
import mindustry.core.UI;
import mindustry.gen.Icon;
import mindustry.type.LiquidStack;
import mindustry.ui.Styles;


public class LiquidImage extends Stack {
    public LiquidImage(TextureRegion region, float amount, boolean completely){
        int dig = Mathf.digits((int) amount);
        int pow = Mathf.pow(10, Math.max(2 - Mathf.sign(!completely) - dig, 0));

        float number = dig > 3 ? amount : (float)((int) amount * pow / pow);

        add(new Table(o -> {
            o.left();
            o.add(new Image(region)).size(32f).scaling(Scaling.fit);
        }));

        if(amount != 0){
            add(new Table(t -> {
                t.left().bottom();
                t.add((dig > 3 ? UI.formatAmount((long) number) : number + "") + (completely ? "" : "/")).style(Styles.outlineLabel);
                t.pack();
            }));
        }
    }

    public LiquidImage(LiquidStack stack, boolean completely){
        this(stack.liquid.uiIcon, stack.amount, completely);
    }

    public LiquidImage(float power){
        this(Icon.power.getRegion(), power, false);
    }
}
