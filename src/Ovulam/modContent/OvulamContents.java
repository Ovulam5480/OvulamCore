package Ovulam.modContent;

import Ovulam.gen.EntityRegistry;
import Ovulam.gen.Stackerc;
import Ovulam.world.graphics.OvulamShaders;
import ent.anno.Annotations;

public class OvulamContents {
    static @Annotations.EntityDef({Stackerc.class}) Stackerc stacker;

    public static void load(){
        EntityRegistry.register();

        OvulamShaders.init();
        OvulamUnitTypes.load();
        OvulamItems.load();

        OvulamPlanets.load();

        OvulamBlocks.load();

    }
}
