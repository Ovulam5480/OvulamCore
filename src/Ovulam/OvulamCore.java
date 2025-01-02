package Ovulam;

import Ovulam.modContent.*;
import Ovulam.world.graphics.OvulamCacheLayers;
import Ovulam.world.graphics.OvulamShaders;
import arc.Events;
import mindustry.game.EventType;
import mindustry.mod.Mod;


public class OvulamCore extends Mod {
    public static OvulamRenderer renderer = new OvulamRenderer();

    public OvulamCore() {
    }

    public static String OvulamCoreName() {
        return "ovulam-";
    }

    @Override
    public void init() {
        OvulamEventAnimations.init();
        //OvulamMechanicsEvents.init();
        OvulamStages.init();

        Events.run(EventType.Trigger.update, renderer::apply);
    }

    @Override
    public void loadContent() {
        OvulamShaders.init();
        OvulamCacheLayers.init();

        OvulamUnitTypes.load();
        OvulamItems.load();

        OvulamPlanets.load();

        OvulamBlocks.load();

    }
}

