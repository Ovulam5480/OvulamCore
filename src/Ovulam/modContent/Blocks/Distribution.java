package Ovulam.modContent.Blocks;

import Ovulam.world.block.distribution.CompositeConveyor;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.PayloadDeconstructor;
import mindustry.world.blocks.payloads.PayloadUnloader;

public class Distribution {
    public static Block smallPayloadConveyor, smallPayloadUnloader, smallPayloadDeconstructor;

    public static void load(){
        smallPayloadConveyor = new CompositeConveyor("small-payload-conveyor"){{
            size = 2;
            requirements(Category.distribution, new ItemStack[]{});
        }};

        smallPayloadUnloader = new PayloadUnloader("small-payload-unloader"){{
            size = 2;
            consumesPower = false;
            requirements(Category.distribution, new ItemStack[]{});
        }};

        smallPayloadDeconstructor = new PayloadDeconstructor("small-payload-deconstructor"){{
            size = 2;
            consumesPower = false;
            requirements(Category.distribution, new ItemStack[]{});
        }};
    }
}
