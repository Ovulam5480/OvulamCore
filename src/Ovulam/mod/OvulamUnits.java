package Ovulam.mod;

import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.type.UnitType;

public class OvulamUnits {
    public static UnitType QQQ;

    public static void load() {
        QQQ = new UnitType("QQQ") {{
            constructor = UnitTypes.flare.constructor;
            immunities.addAll(Vars.content.statusEffects());

        }};
    }
}
