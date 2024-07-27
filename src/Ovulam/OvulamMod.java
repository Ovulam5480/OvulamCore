package Ovulam;

import Ovulam.modContent.*;
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
        OvulamEventAnimations.init();
        OvulamEvents.init();
    }

    @Override
    public void loadContent(){
        OvulamShaders.init();
        OvulamUnitTypes.load();

        OvulamItems.load();

        OvulamPlanets.load();

        OvulamBlocks.load();
    }
}

