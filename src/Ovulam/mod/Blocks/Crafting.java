package Ovulam.mod.Blocks;

import Ovulam.entities.OvulamFx;
import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.drawBlock.DrawBatchFactory;
import Ovulam.world.drawBlock.DrawKnitter;
import Ovulam.world.drawBlock.DrawMixer;
import Ovulam.world.drawRecipePayload.DrawPayloadDefault;
import Ovulam.world.move.MoveCustomP9;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

//各种工厂及其组装方块
//todo 不要使用MoveDefault!!!
public class Crafting {
    public static Block
            knitter, mixer, batchFactory,
            batchFactoryBase ,mixerBase;
    public static void load(){
        batchFactory = new MultiPayloadCrafter("batch-factory") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
            drawer = new DrawBatchFactory();
            plans = new Seq<>();
        }};

        mixer = new MultiPayloadCrafter("mixer") {{
            requirements(Category.defense, new ItemStack[]{});
            size = 15;
            drawer = new DrawMixer();
            plans = new Seq<>();
        }};

        knitter = new MultiPayloadCrafter("knitter") {{
            requirements(Category.defense, new ItemStack[]{});
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
                                    Blocks.titaniumWallLarge, 3, new MoveCustomP9(new int[]{0, 4, 8}, 16f), new DrawPayloadDefault()
                            }, 120, true
                    )
            );
        }};
    }
}
