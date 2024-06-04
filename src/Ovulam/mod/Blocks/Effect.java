package Ovulam.mod.Blocks;

import Ovulam.world.block.storage.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class Effect {
    public static Block
            baseCore,
            deconstructorStorage, playerSpawnStorage, unloaderStorage;

    public static void load(){
        baseCore = new BaseCoreBlock("core-base"){{
            size = 8;
            requirements(Category.effect, new ItemStack[]{});
        }};

        deconstructorStorage = new DeconstructorStorage("deconstructor-storage"){{
            size = 8;
            requirements(Category.effect, new ItemStack[]{});
        }};

        playerSpawnStorage = new PlayerSpawnStorage("player-spawn-storage"){{
            size = 8;
            requirements(Category.effect, new ItemStack[]{});
        }};

        unloaderStorage = new UnloaderStorage("unloader-storage"){{
            size = 8;
            requirements(Category.effect, new ItemStack[]{});
        }};

    }
}
