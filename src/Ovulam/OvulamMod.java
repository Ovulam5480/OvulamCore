package Ovulam;

import Ovulam.modContent.*;
import Ovulam.world.graphics.OvulamShaders;
import mindustry.mod.Mod;

public class OvulamMod extends Mod {

    public OvulamMod() {
    }

    public static String OvulamModName() {
        return "ovulam-";
    }

    @Override
    public void init() {
        OvulamEventAnimations.init();
        OvulamMechanicsEvents.init();
        OvulamStages.init();
    }

    @Override
    public void loadContent() {
        OvulamShaders.init();
        OvulamUnitTypes.load();
        OvulamItems.load();

        OvulamPlanets.load();

        OvulamBlocks.load();
    }
}

