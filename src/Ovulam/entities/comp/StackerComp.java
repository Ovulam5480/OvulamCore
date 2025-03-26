package Ovulam.entities.comp;

import Ovulam.gen.Stackerc;
import ent.anno.Annotations;
import mindustry.gen.Itemsc;

@Annotations.EntityComponent(base = true)
@Annotations.EntityDef(Stackerc.class)
abstract class StackerComp implements Itemsc {
    int itemCapacity;

    @Override
    public int itemCapacity() {
        return itemCapacity;
    }
}
