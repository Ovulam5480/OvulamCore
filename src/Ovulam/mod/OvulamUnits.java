package Ovulam.mod;

import Ovulam.type.unit.InvitationUnitEntity;
import Ovulam.type.unit.InvitationUnitType;
import mindustry.entities.abilities.ForceFieldAbility;
import mindustry.type.UnitType;

public class OvulamUnits {
    public static UnitType invitation;

    public static void load() {
        invitation = new InvitationUnitType("invitation") {{
            constructor = InvitationUnitEntity::new;
            health = 9000;
            hitSize = 15 * 8;
            abilities.add(new ForceFieldAbility(800, 0, 10000, 0));
            speed = 0.05f;
            canAttack = false;
            flying = true;
            rotateMoveFirst = true;
            rotateSpeed = 0.05f;

        }};
    }
}
