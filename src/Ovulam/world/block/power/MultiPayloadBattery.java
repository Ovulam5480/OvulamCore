package Ovulam.world.block.power;

import Ovulam.world.block.payload.MultiPayloadBlock;
import arc.struct.EnumSet;
import mindustry.world.meta.BlockFlag;
import mindustry.world.meta.BlockGroup;

//多载荷电池
public class MultiPayloadBattery extends MultiPayloadBlock {
    public MultiPayloadBattery(String name) {
        super(name);
        rotate = false;
        solid = true;
        hasPower = true;
        outputsPower = true;
        group = BlockGroup.power;
        flags = EnumSet.of(BlockFlag.battery);
    }
}
