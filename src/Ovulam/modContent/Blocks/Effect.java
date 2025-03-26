package Ovulam.modContent.Blocks;

import Ovulam.world.block.defense.AblationTower;
import Ovulam.world.block.storage.*;
import arc.graphics.g2d.TextureRegion;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.Planet;
import mindustry.type.Weapon;
import mindustry.world.Block;

public class Effect {
    public static Block
            //todo 地膏 菌轮 芝畦 Sporebed Fairyring Mushroomfield
            //todo 霖幄 油幕 天罗 Oilcanopy Reliefshelter Imperialcanopy
            coreOilcanopy,
            deconstructorStorage, playerSpawnStorage, unloaderStorage,
            AblationTower;

    public static void load(){
        coreOilcanopy = new FactoryCoreBlock("core-oilcanopy"){{
            size = 4;
            unitType = UnitTypes.mono;
            requirements(Category.effect, new ItemStack[]{});

            recipes = Seq.with(
                    new CoreRecipe(
                            ItemStack.with(Items.sand, 3, Items.coal, 2),
                            ItemStack.list(Items.silicon, 3, Items.scrap, 1),
                            300f, Items.silicon),
                    new CoreRecipe(
                            ItemStack.with(Items.sand, 3),
                            ItemStack.list(Items.graphite, 1),
                            300f, Items.graphite));
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
