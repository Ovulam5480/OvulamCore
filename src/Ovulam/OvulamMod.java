package Ovulam;

import Ovulam.mod.OvulamBlocks;
import Ovulam.mod.OvulamEvents;
import Ovulam.mod.OvulamItems;
import Ovulam.mod.OvulamUnits;
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
        /*
        OvulamCacheLayer.init();
        OvulamCacheLayer.load();

         */
    }

    @Override
    public void loadContent(){
        OvulamBlocks.load();
        OvulamItems.load();
        OvulamUnits.load();

        OvulamEvents.load();
        OvulamShaders.init();

        //OvulamUI.init();
    }
}

