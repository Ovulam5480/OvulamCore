package Ovulam.No9527垃圾堆;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.logic.LAccess;
import mindustry.world.Block;

public class 加damage extends Block {

    public 加damage(String name) {
        super(name);
        update = true;
        sync = true;
        canOverdrive = false;
        targetable = false;
        forceDark = true;
        privileged = true;
        size = 1;
    }

    public class 加damageBuild extends Building {
        public float Multiplier = 1;


        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.config) Multiplier = (float) p1;
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void updateTile() {
            Vars.state.rules.blockDamageMultiplier = Multiplier;
        }
    }
}
