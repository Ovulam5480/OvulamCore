package Ovulam;

import Ovulam.mod.OvulamBlocks;
import Ovulam.mod.OvulamEvents;
import Ovulam.mod.OvulamItems;
import Ovulam.mod.OvulamUnits;
import Ovulam.world.graphics.OvulamShaders;
import mindustry.mod.Mod;

public class OvulamMod extends Mod{
    public static String OvulamModName(){
        return "ovulam";
    }

    public OvulamMod(){
    }
    @Override
    public void init() {
        OvulamEvents.init();
    }

    @Override
    public void loadContent(){

        OvulamShaders.init();

        OvulamBlocks.load();
        OvulamItems.load();
        OvulamUnits.load();

        //OvulamUI.init();
    }
}

