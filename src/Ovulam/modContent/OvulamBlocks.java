package Ovulam.modContent;

import Ovulam.No9527垃圾堆.LaserLaserBulletType;
import Ovulam.modContent.Blocks.*;
import Ovulam.world.block.distribution.CompositeConveyor;
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
        Environment.load();
        Turret.load();
        Production.load();
        Distribution.load();
        Crafting.load();
        Effect.load();

        SSSSSS = new CompositeConveyor("QQQQQ") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 16;
            itemsSpeed = 4f;
        }};

        ((LaserTurret) Blocks.meltdown).shootType = new LaserLaserBulletType() {{
            length = 180f;
            drawSize = 420f;
        }};

/*        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++) {
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);
        }*/
    }
}


