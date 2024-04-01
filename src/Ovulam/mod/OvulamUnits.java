package Ovulam.mod;

import Ovulam.type.unit.InvitationUnitEntity;
import Ovulam.type.unit.InvitationUnitType;
import mindustry.Vars;
import mindustry.type.UnitType;

public class OvulamUnits {
    public static UnitType invitation;

    public static void load() {
        invitation = new InvitationUnitType("invitation") {{
            constructor = InvitationUnitEntity::new;
            immunities.addAll(Vars.content.statusEffects());
            health = 9000;

        }};
    }
}
