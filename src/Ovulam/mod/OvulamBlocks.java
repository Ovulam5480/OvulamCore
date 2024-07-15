package Ovulam.mod;

import Ovulam.mod.Blocks.Crafting;
import Ovulam.mod.Blocks.Effect;
import Ovulam.mod.Blocks.Turret;
import Ovulam.No9527垃圾堆.LaserLaserBulletType;
import Ovulam.world.block.block.ItemBlock;
import Ovulam.world.block.defense.AerialExclusionWall;
import Ovulam.world.block.liquid.PoolFloor;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.content.StatusEffects;
import mindustry.graphics.CacheLayer;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.LaserTurret;
import mindustry.world.meta.Attribute;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block SSSSS, SSSSSS;

    public static void load() {
        Turret.load();
        Crafting.load();
        Effect.load();

        SSSSSS = new PoolFloor("QQQQQ") {{
            size = 1;
            speedMultiplier = 0.18f;
            variants = 0;
            status = StatusEffects.wet;
            statusDuration = 140f;
            drownTime = 200f;
            liquidDrop = Liquids.water;
            isLiquid = true;
            cacheLayer = CacheLayer.water;
            albedo = 0.9f;
            attributes.set(Attribute.spores, 0.15f);
            supportsOverlay = true;
            requirements(Category.defense, new ItemStack[]{});
        }};

        SSSSS = new AerialExclusionWall("SSSSS") {{
            size = 2;
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


