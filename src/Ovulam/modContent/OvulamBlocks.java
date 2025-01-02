package Ovulam.modContent;

import Ovulam.No9527垃圾堆.LaserLaserBulletType;
import Ovulam.modContent.Blocks.crafting;
import Ovulam.modContent.Blocks.effect;
import Ovulam.modContent.Blocks.turret;
import Ovulam.world.block.block.EffectTest;
import Ovulam.world.block.block.ItemBlock;
import Ovulam.world.block.defense.AerialExclusionWall;
import Ovulam.world.graphics.OvulamCacheLayers;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.blocks.environment.Floor;

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

        SSSSS = new AerialExclusionWall("SSSSS") {{
            size = 2;
            requirements(Category.defense, new ItemStack[]{});
        }
        };

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


