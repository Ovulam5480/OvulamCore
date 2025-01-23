package Ovulam;

import Ovulam.gen.EntityRegistry;
import Ovulam.modContent.*;
import Ovulam.world.graphics.OvulamCacheLayers;
import Ovulam.world.graphics.OvulamShaders;
import mindustry.Vars;
import mindustry.content.StatusEffects;
import mindustry.game.Team;
import mindustry.mod.Mod;


public class OvulamMod extends Mod {
    public static OvulamRenderer renderer = new OvulamRenderer();

    public OvulamMod() {
    }

    public static String modName() {
        return "ovulam-";
    }

    @Override
    public void init() {
        OvulamEventAnimations.init();
        //OvulamMechanicsEvents.init();
        OvulamStages.init();

    }

    @Override
    public void loadContent() {
        EntityRegistry.register();

        OvulamShaders.init();
        OvulamCacheLayers.init();

        OvulamUnitTypes.load();
        OvulamItems.load();

        OvulamPlanets.load();

        OvulamBlocks.load();
    }
}

