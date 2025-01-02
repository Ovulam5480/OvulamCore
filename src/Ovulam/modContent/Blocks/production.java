package Ovulam.modContent.Blocks;

import Ovulam.No9527垃圾堆.Drill9527;
import Ovulam.world.block.block.PayloadOre;
import Ovulam.world.block.production.PayloadDrill;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class production {
    public static Block PayloadDrill, Drill9527;
    public static Block PayloadOreLarge;

    public void load() {
        PayloadOreLarge = new PayloadOre("PayloadOre-large") {{
            size = 2;
            health = 4000;
            armor = 40;
            itemCapacity = 1000;
            oreAmount = 12;
            requirements(Category.production, new ItemStack[]{new ItemStack(Items.sand, 1000)});
        }};

        PayloadDrill = new PayloadDrill("PayloadDrill") {{
            requirements(Category.production, new ItemStack[]{});
            oreBlock = (PayloadOre) PayloadOreLarge;
            tier = 5;
            size = 5;
        }};

        Drill9527 = new Drill9527("9527") {{
            consumePower(1145f / 60f);
            requirements(Category.production, new ItemStack[]{});
            consumeLiquid(Liquids.slag, 0.1f).boost();
            tier = 9527;
            liquidBoostIntensity = 2f;
        }};
    }
}
