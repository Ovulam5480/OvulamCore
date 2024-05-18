package Ovulam.mod;

import Ovulam.entities.bullet.MortarBulletType;
import Ovulam.mod.Blocks.Crafting;
import Ovulam.world.block.No9527.Drill9527;
import Ovulam.world.block.No9527.LaserMassDriver;
import Ovulam.world.block.block.ItemBlock;
import Ovulam.world.block.block.PayloadOre;
import Ovulam.world.block.defense.AblationTower;
import Ovulam.world.block.defense.ItemStackTurret;
import Ovulam.world.block.defense.Mortar;
import Ovulam.world.block.production.PayloadDrill;
import Ovulam.world.block.storage.PayloadDeconstructorStorage;
import Ovulam.world.drawBlock.DrawOrganize;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block
            //方块
            PayloadOreLarge,
    //工厂
    organize,order,
    //
    PayloadDrill, PayloadDeconstructorStorage, Mortar, AblationTower, SSSSS,
    //测试
    SSSS, SSS, PDS, SS, Drill9527, PayloadOre, S,
            q1;

    public static void load() {
        Crafting.load();

        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++){
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);

        }

        Blocks.massDriver = new LaserMassDriver("ssss"){{
            requirements(Category.defense, new ItemStack[]{});
        }};

        AblationTower = new AblationTower("AblationTower") {{
            side = 8;
            size = 4;
            requirements(Category.defense, new ItemStack[]{});
        }};

        order = new ItemStackTurret("order"){{
            shootSound = Sounds.shootBig;
            bullet = new BasicBulletType(7f, 75f);

            size = 4;
            reload = 300f;
            itemCapacity = 120;
            requirements(Category.defense, new ItemStack[]{});

            flammabilityMultiplier = 1f;
            explosivenessMultiplier = 1f;
            radioactivityMultiplier = 1f;
            chargeMultiplier = 1f;
        }};

        organize = new ItemStackTurret("organize"){{
            bullet = new MortarBulletType(this){{
                lifetime = 240f;
                height = 2f;
                offsideMultiplier = 2f;
            }};

            drawer = new DrawOrganize();

            size = 4;
            reload = 1200f;
            itemCapacity = 300;
            requirements(Category.defense, new ItemStack[]{});

            flammabilityMultiplier = 1f;
            explosivenessMultiplier = 1f;
            radioactivityMultiplier = 1f;
            chargeMultiplier = 1f;
        }};



        PayloadOreLarge = new PayloadOre("PayloadOre-large") {{
            size = 2;
            health = 4000;
            armor = 40;
            itemCapacity = 1000;
            oreAmount = 12;
            requirements(Category.defense, new ItemStack[]{new ItemStack(Items.sand, 1000)});
        }};



        Mortar = new Mortar("mortar") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
        }};

        PayloadDrill = new PayloadDrill("PayloadDrill") {{
            requirements(Category.defense, new ItemStack[]{});
            oreBlock = (PayloadOre) PayloadOreLarge;
            tier = 5;
            size = 5;
        }};

        Drill9527 = new Drill9527("9527") {{
            consumePower(1145f / 60f);
            requirements(Category.defense, new ItemStack[]{});
            consumeLiquid(Liquids.slag, 0.1f).boost();
            tier = 9527;
            liquidBoostIntensity = 2f;
        }};

        PayloadDeconstructorStorage = new PayloadDeconstructorStorage("payload-deconstructor-storage") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 6;
        }};
    }
}


