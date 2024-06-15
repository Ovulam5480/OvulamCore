package Ovulam.mod;

import Ovulam.mod.Blocks.Crafting;
import Ovulam.mod.Blocks.Effect;
import Ovulam.mod.Blocks.Turret;
import Ovulam.world.No9527.JumpQi;
import Ovulam.world.No9527.LaserLaserBulletType;
import Ovulam.world.block.block.ItemBlock;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LaserTurret;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block SSSSS;

    public static void load() {
        Turret.load();
        Crafting.load();
        Effect.load();

        SSSSS = new JumpQi("SSSSS") {{
            requirements(Category.defense, new ItemStack[]{});
        }};

        ((LaserTurret) Blocks.meltdown).shootType = new LaserLaserBulletType() {{
            length = 180f;
            drawSize = 420f;
        }};

        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++) {
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);
        }
    }
}


