package Ovulam.mod;

import Ovulam.type.bullet.MortarBulletType;
import Ovulam.world.block.block.ManufacturerBlock;
import Ovulam.world.block.block.PayloadOre;
import Ovulam.world.block.defense.AblationTower;
import Ovulam.world.block.defense.ItemStackTurret;
import Ovulam.world.block.defense.Mortar;
import Ovulam.world.block.production.Drill9527;
import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.block.production.PayloadDrill;
import Ovulam.world.block.storage.PayloadDeconstructorStorage;
import Ovulam.world.draw.DrawBatchFactory;
import Ovulam.world.draw.DrawMixer;
import Ovulam.world.draw.DrawMultiConstruct;
import Ovulam.world.draw.DrawOrganize;
import Ovulam.world.move.MoveCustomP16;
import Ovulam.world.move.MoveCustomP9;
import Ovulam.world.move.Moved4;
import Ovulam.world.move.Moved8edge;
import Ovulam.world.other.Recipe;
import Ovulam.world.other.RecipeMover;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.content.Liquids;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.PayloadStack;
import mindustry.type.UnitType;
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
    PayloadDrill, PayloadDeconstructorStorage, Mortar, AblationTower, SSSSS, batchFactoryBase,
    //测试
    SSSS, SSS, PDS, SS, Drill9527, PayloadOre, S,
            q1;

    public static void load() {
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
                    new MultiPayloadPlan(180f, 1f, "123",
                            new Recipe(
                                    Seq.with(),
                                    Seq.with(),
                                    Seq.with(new PayloadStack(Blocks.copperWallLarge, 2),
                                            new PayloadStack(Blocks.thoriumWallLarge, 2)),
                                    0, true),
                            new Recipe(
                                    Seq.with(),
                                    Seq.with(),
                                    Seq.with(new PayloadStack(Blocks.surgeWallLarge, 1),
                                            new PayloadStack(Blocks.titaniumWallLarge, 4)),
                                    0, true),
                            new RecipeMover[]{
                                    new RecipeMover(Blocks.copperWallLarge,
                                            new MoveCustomP9(new int[]{1, 3, 5, 7}, 16)),
                                    new RecipeMover(Blocks.thoriumWallLarge,
                                            new MoveCustomP9(new int[]{0, 2, 4, 6, 8}, 16)),
                                    new RecipeMover(Blocks.titaniumWallLarge, new Moved8edge()),
                                    new RecipeMover(Blocks.surgeWallLarge, new Moved4())
                            },
                            new DrawMultiConstruct()
                    ),

                    new MultiPayloadPlan(180f, 1f, "12345",
                            new Recipe(
                                    Seq.with(),
                                    Seq.with(),
                                    Seq.with(new PayloadStack(Blocks.copperWallLarge, 4),
                                            new PayloadStack(Blocks.thoriumWallLarge, 5)),
                                    0, true),
                            new Recipe(
                                    Seq.with(),
                                    Seq.with(),
                                    Seq.with(new PayloadStack(Blocks.surgeWallLarge, 1),
                                            new PayloadStack(Blocks.titaniumWallLarge, 4)),
                                    0, true),
                            new RecipeMover[]{
                                    new RecipeMover(Blocks.titaniumWallLarge, new Moved8edge()),
                                    new RecipeMover(Blocks.surgeWallLarge, new Moved4())
                            },
                            new DrawMultiConstruct()
                    )
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
            ignorePayloadFullness = true;
            changeClear = true;

            plans = new Seq<>();
            for (UnitType unitType : Vars.content.units()) {
                if(Vars.content.units().indexOf(unitType) > 20)break;
                plans.add(
                        new MultiPayloadPlan(900f, 1f, "批量生产-" + unitType,
                                new Recipe(
                                        new Seq<>(unitType.getTotalRequirements()),
                                        Seq.with(),
                                        Seq.with(),
                                        0, true),

                                new Recipe(
                                        Seq.with(),
                                        Seq.with(),
                                        Seq.with(),
                                        0, true),

                                new RecipeMover[]{
                                        new RecipeMover(unitType, new MoveCustomP16(16))
                                },
                                new DrawMultiConstruct()
                        ));
            }
        }};

        batchFactoryBase = new ManufacturerBlock("batch-factory-base") {{
            buildCost = 300f;
            requirements(Category.defense, new ItemStack[]{});
            targetBlock = batchFactory;
            stage.add(
                    new ManufacturerStage(300f,
                            new Recipe(Seq.with(), Seq.with(), Seq.with(
                                    new PayloadStack(Blocks.titaniumWallLarge, 8),
                                    new PayloadStack(Blocks.copperWallLarge, 8)
                            ), 0, true),
                            new RecipeMover[]{
                                    new RecipeMover(Blocks.titaniumWallLarge,
                                            new MoveCustomP16(new int[]{0, 3, 5, 6, 9, 10, 12, 15}, 24)),
                                    new RecipeMover(Blocks.copperWallLarge,
                                            new MoveCustomP16(new int[]{1, 2, 4, 7, 8, 11, 13, 14}, 24))
                            }
                    ),
                    new ManufacturerStage(300f,
                            new Recipe(Seq.with(), Seq.with(), Seq.with(
                                    new PayloadStack(Blocks.surgeWallLarge, 12)
                            ), 0, true),
                            new RecipeMover[]{
                                    new RecipeMover(Blocks.surgeWallLarge,
                                            new MoveCustomP16(new int[]{0, 1, 2, 3, 4, 7, 8, 11, 12, 13, 14, 15}, 24))
                            }
                    ),
                    new ManufacturerStage(300f,
                            new Recipe(Seq.with(), Seq.with(), Seq.with(
                                    new PayloadStack(Blocks.plastaniumWallLarge, 16)
                            ), 0, true),
                            new RecipeMover[]{
                                    new RecipeMover(Blocks.plastaniumWallLarge,
                                            new MoveCustomP16(new int[]{0, 3, 5, 6, 9, 10, 12, 15}, 24))
                            }
                    )
            );
        }};
    }
}


