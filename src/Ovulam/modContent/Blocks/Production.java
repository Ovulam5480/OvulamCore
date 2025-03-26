package Ovulam.modContent.Blocks;

import Ovulam.No9527垃圾堆.Drill9527;
import Ovulam.world.block.block.PayloadOre;
import Ovulam.world.block.production.PayloadDrill;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class Production {
    public static Block PayloadDrill, LargePayloadDrill, Drill9527;
    public static Block LargePayloadOre, PayloadOre;

    public static void load() {
        PayloadOre = new PayloadOre("payloadOre") {{
            size = 1;
            health = 200;
            armor = 5;
            itemCapacity = 300;
            oreAmount = 3;
            requirements(Category.production, new ItemStack[]{new ItemStack(Items.sand, 300)});
        }};
        LargePayloadOre = new PayloadOre("large-payloadOre") {{
            size = 2;
            health = 1200;
            armor = 15;
            itemCapacity = 1000;
            oreAmount = 12;
            requirements(Category.production, new ItemStack[]{new ItemStack(Items.sand, 1000)});
        }};

        PayloadDrill = new PayloadDrill("payloadDrill") {{
            requirements(Category.production, new ItemStack[]{});
            oreBlock = (PayloadOre) PayloadOre;
            tier = 5;
            size = 3;

            hasTop = false;
            laserDrillRadius = 8.5f;
            laserWidth = 0.2f;
        }};

        LargePayloadDrill = new PayloadDrill("large-payloadDrill") {{
            requirements(Category.production, new ItemStack[]{});
            oreBlock = (PayloadOre) LargePayloadOre;
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
