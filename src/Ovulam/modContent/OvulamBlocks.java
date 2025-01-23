package Ovulam.modContent;

import Ovulam.No9527垃圾堆.Drill9527;
import Ovulam.No9527垃圾堆.LaserLaserBulletType;
import Ovulam.No9527垃圾堆.liq发散器;
import Ovulam.No9527垃圾堆.介绍器;
import Ovulam.modContent.Blocks.crafting;
import Ovulam.modContent.Blocks.effect;
import Ovulam.modContent.Blocks.turret;
import Ovulam.world.block.block.EffectTest;
import Ovulam.world.block.production.Dirll;
import Ovulam.world.graphics.OvulamCacheLayers;
import mindustry.content.Blocks;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LaserTurret;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block SSSSS, SSSSSS;

    public static void load() {
        turret.load();
        crafting.load();
        effect.load();

        SSSSSS = new EffectTest("QQQQQ") {{
            requirements(Category.defense, new ItemStack[]{});
        }};

        SSSSS = new Dirll("9527") {{
            size = 2;
            requirements(Category.defense, new ItemStack[]{});
        }};

        ((LaserTurret) Blocks.meltdown).shootType = new LaserLaserBulletType() {{
            length = 180f;
            drawSize = 420f;
        }};

        Blocks.arkyciteFloor.cacheLayer = OvulamCacheLayers.subspaceCacheLayer;

/*        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++) {
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);
        }*/
    }
}


