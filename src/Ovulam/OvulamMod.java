package Ovulam;

import Ovulam.mod.OvulamBlocks;
import Ovulam.mod.OvulamEvents;
import Ovulam.mod.OvulamItems;
import Ovulam.mod.OvulamUnits;
import Ovulam.world.graphics.OvulamCacheLayer;
import Ovulam.world.graphics.OvulamShaders;
import arc.util.Log;
import mindustry.mod.Mod;

public class OvulamMod extends Mod{
    public static String ovulamName(){
        return "ovulam";
    }

    public OvulamMod(){
        Log.info("5480 is eating.");
    }

    @Override
    public void init() {
    }

    @Override
    public void loadContent(){
        OvulamShaders.init();

        OvulamCacheLayer.init();
        OvulamCacheLayer.load();

        OvulamBlocks.load();
        OvulamItems.load();
        OvulamUnits.load();

        OvulamEvents.load();
        //OvulamUI.init();
    }
}

