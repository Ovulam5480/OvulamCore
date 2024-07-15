package Ovulam;

import Ovulam.mod.*;
import Ovulam.world.graphics.OvulamShaders;
import mindustry.mod.Mod;

public class OvulamMod extends Mod{
    public static String OvulamModName(){
        return "ovulam-";
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
        OvulamUnitTypes.load();

        OvulamBlocks.load();
        OvulamItems.load();

        OvulamPlanets.load();
        //OvulamUI.init();
    }
}

