package Ovulam.No9527垃圾堆;

import Ovulam.gen.EntityRegistry;
import mindustry.gen.UnitEntity;

public class OnedamageUnit extends UnitEntity {
    public int classId() {
        return EntityRegistry.getID(this.getClass());
    }

    @Override
    public void rawDamage(float amount) {
        super.rawDamage(1);
    }
}
