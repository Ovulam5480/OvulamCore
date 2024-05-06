package Ovulam.mod;

import Ovulam.entities.bullet.MortarBulletType;
import Ovulam.world.block.No9527.BuffSpee;
import Ovulam.world.block.No9527.Drill9527;
import Ovulam.world.block.block.ItemBlock;
import Ovulam.world.block.block.ManufacturerBlock;
import Ovulam.world.block.block.PayloadOre;
import Ovulam.world.block.defense.AblationTower;
import Ovulam.world.block.defense.ItemStackTurret;
import Ovulam.world.block.defense.Mortar;
import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.block.production.PayloadDrill;
import Ovulam.world.block.storage.PayloadDeconstructorStorage;
import Ovulam.world.drawBlock.DrawBatchFactory;
import Ovulam.world.drawBlock.DrawKnitter;
import Ovulam.world.drawBlock.DrawMixer;
import Ovulam.world.drawBlock.DrawOrganize;
import Ovulam.world.drawRecipePayload.DrawPayloadConstruct;
import Ovulam.world.drawRecipePayload.DrawPayloadExpansion;
import Ovulam.world.move.MoveCircle;
import Ovulam.world.move.MoveCustomP4;
import Ovulam.world.type.Recipe;
import Ovulam.world.type.RecipePayloadManager;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.world.Block;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block
            //方块
            GrapheneBlockLarge,
            metaglassBlockLarge, graphiteBlockLarge, PayloadOreLarge, batchFactory,
    //工厂
    organize,order,mixer,
    //
    PayloadDrill, PayloadDeconstructorStorage, Mortar, AblationTower, SSSSS, batchFactoryBase,mixerBase,
    //测试
    SSSS, SSS, PDS, SS, Drill9527, PayloadOre, S,knitter,
            q1;

    public static void load() {
        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++){
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);

        }

        SSSS = new BuffSpee("ssss"){{
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

        mixer = new MultiPayloadCrafter("mixer") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
            drawer = new DrawMixer();
            plans = Seq.with(
            );
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
        batchFactory = new MultiPayloadCrafter("batch-factory") {{
            drawer = new DrawBatchFactory();
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
            changeClear = true;

            plans = new Seq<>();
        }};


        mixerBase = new ManufacturerBlock("mixer-base") {{
            buildCost = 300f;
            requirements(Category.defense, new ItemStack[]{});
            targetBlock = mixer;
        }};

        knitter = new MultiPayloadCrafter("knitter") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
            drawer = new DrawKnitter(32f, 16, 2f, 0f,16f);
            plans = Seq.with(
                    new MultiPayloadPlan(300f, 1f, "123",
                            new Recipe(
                                    ItemStack.list(Items.coal, 300, Items.sand, 300),
                                    LiquidStack.list(),
                                    RecipePayloadManager.list(
                                            Blocks.copperWallLarge, 16, new MoveCircle(32), new DrawPayloadConstruct()
                                    ),
                                    0f, true
                            ),
                            new Recipe(
                                    ItemStack.list(),
                                    LiquidStack.list(),
                                    RecipePayloadManager.list(
                                            Blocks.thoriumWallLarge, 4, new MoveCustomP4(16), new DrawPayloadExpansion()
                                    ),
                                    0f, true
                            )
                    )
            );
        }};

        batchFactoryBase = new ManufacturerBlock("batch-factory-base") {{
            buildCost = 300f;
            requirements(Category.defense, new ItemStack[]{});
            targetBlock = batchFactory;
        }};
    }
}


