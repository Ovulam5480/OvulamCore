package Ovulam.modContent.Blocks;

import Ovulam.entities.OvulamFx;
import Ovulam.world.block.block.ManufacturerBlock;
import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.drawBlock.DrawBatchFactory;
import Ovulam.world.drawBlock.DrawKnitter;
import Ovulam.world.drawBlock.DrawMixer;
import Ovulam.world.drawRecipePayload.DrawPayloadDefault;
import Ovulam.world.drawRecipePayload.DrawPayloadExpansion;
import Ovulam.world.move.MoveCustomP16;
import Ovulam.world.move.MoveCustomP9;
import Ovulam.world.move.MoveOut;
import Ovulam.world.move.MoveSize;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.content.Liquids;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

//各种工厂及其组装方块
//todo 不要使用MoveDefault!!!
public class crafting {
    public static Block
            knitter, mixer, batchFactory, crystallizer,
            knitterBase, mixerBase, batchFactoryBase;
    public static void load(){
        batchFactory = new MultiPayloadCrafter("batch-factory") {{
            requirements(Category.crafting, new ItemStack[]{});
            size = 15;
            drawer = new DrawBatchFactory();
            plans = Seq.with(
                    new MultiPayloadPlan(300f, 1f, "123",
                            new Object[]{},
                            new Object[]{Liquids.water, 1f},
                            new Object[]{}, 0f, false,
                            new Object[]{},
                            new Object[]{Liquids.oil, 1f},
                            new Object[]{}, 120, true
                    ),
                    new MultiPayloadPlan(300f, 1f, "123",
                            new Object[]{},
                            new Object[]{Liquids.cryofluid, 1f},
                            new Object[]{}, 0f, false,
                            new Object[]{},
                            new Object[]{Liquids.slag, 1f},
                            new Object[]{}, 120, false
                    )
            );
        }};

        mixer = new MultiPayloadCrafter("mixer") {{
            requirements(Category.crafting, new ItemStack[]{});
            size = 15;
            drawer = new DrawMixer();
            moveOutMover = new MoveOut(24);
            moveCapital = new MoveSize(24);
            plans = Seq.with(
                    new MultiPayloadPlan(300f, 1f, "123",
                            new Object[]{},
                            new Object[]{},
                            new Object[]{
                                    Blocks.battery, 5, new MoveCustomP9(new int[]{0,2,4,6,8},24f), new DrawPayloadDefault(),
                                    Blocks.massDriver, 4, new MoveCustomP9(new int[]{1,3,5,7},24f), new DrawPayloadDefault()
                            }, 0f, false,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{
                                    Blocks.cyclone, 3, new MoveCustomP9(new int[]{0, 4, 8}, 16f), new DrawPayloadDefault()
                            }, 120, true
                    )
            );
        }};

        knitter = new MultiPayloadCrafter("knitter") {{
            requirements(Category.crafting, new ItemStack[]{});
            size = 15;
            craftEffect = OvulamFx.phaseFragment;
            drawer = new DrawKnitter(32f, 16, 2f, 0f,16f);
            plans = Seq.with(
                    new MultiPayloadPlan(300f, 1f, "123",
                            new Object[]{},
                            new Object[]{},
                            new Object[]{
                                    Blocks.copperWallLarge, 5, new MoveCustomP9(new int[]{0,2,4,6,8},16f), new DrawPayloadDefault(),
                                    Blocks.thoriumWallLarge, 4, new MoveCustomP9(new int[]{1,3,5,7},16f), new DrawPayloadDefault()
                            }, 0f, false,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{
                                    Blocks.titaniumWallLarge, 3, new MoveCustomP9(new int[]{0,4,8}, 16f), new DrawPayloadDefault()
                            }, 120, true
                    )
            );
        }};

        knitterBase = new ManufacturerBlock("knitter-base"){{
            requirements(Category.crafting, new ItemStack[]{});
            targetBlock = knitter;
            stages = Seq.with(
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                            120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{1,2,4,7,8,11,13,14}, 16f), new DrawPayloadExpansion()},
                            120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                            120f, false)
            );
        }};

        mixerBase = new ManufacturerBlock("mixer-base"){{
            requirements(Category.crafting, new ItemStack[]{});
            targetBlock = mixer;
            stages = Seq.with(
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                            120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{1,2,4,7,8,11,13,14}, 16f), new DrawPayloadExpansion()},
                            120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                            120f, false)
            );
        }};

        batchFactoryBase = new ManufacturerBlock("batch-factory-base"){{
            requirements(Category.crafting, new ItemStack[]{});
            targetBlock = batchFactory;
            stages = Seq.with(
                    new ManufacturerStage(300f,
                    new Object[]{},
                    new Object[]{},
                    new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                    120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{1,2,4,7,8,11,13,14}, 16f), new DrawPayloadExpansion()},
                            120f, false),
                    new ManufacturerStage(300f,
                            new Object[]{},
                            new Object[]{},
                            new Object[]{Blocks.copperWallLarge, 8, new MoveCustomP16(new int[]{0,3,5,6,9,10,12,15}, 16f), new DrawPayloadExpansion()},
                            120f, false)
            );
        }};
    }
}
