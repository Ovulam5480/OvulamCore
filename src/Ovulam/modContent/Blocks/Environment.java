package Ovulam.modContent.Blocks;

import Ovulam.modContent.OvulamItems;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.world.Block;
import mindustry.world.blocks.environment.OreBlock;


public class Environment {
    public static Block oreCopperOre, oreLeadOreOre, oreTinOre, oreSalt;

    public static void load(){
        oreCopperOre = new OreBlock("ore-copper-ore", OvulamItems.copperOre);
        oreLeadOreOre = new OreBlock("ore-lead-ore", OvulamItems.leadOre);
        oreTinOre = new OreBlock("ore-tin-ore", OvulamItems.tinOre);
        oreSalt = new OreBlock("ore-salt", OvulamItems.salt);

    }
}
