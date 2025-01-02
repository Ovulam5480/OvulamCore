package Ovulam.modContent.Blocks;

import Ovulam.world.block.defense.AblationTower;
import Ovulam.world.block.storage.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class effect {
    public static Block
            //todo 幻辉科技核心
            coreMartialDragon,
            deconstructorStorage, playerSpawnStorage, unloaderStorage,
            AblationTower;

    public static void load(){
        coreMartialDragon = new BaseCoreBlock("treeRoot-martial-dragon"){{
            size = 15;
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

        //todo de
        AblationTower = new AblationTower("AblationTower") {{
            side = 8;
            size = 4;
            requirements(Category.effect, new ItemStack[]{});
        }};

    }
}
