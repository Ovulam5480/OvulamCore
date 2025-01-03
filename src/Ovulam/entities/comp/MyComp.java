package Ovulam.entities.comp;

import arc.util.Log;
import ent.anno.Annotations.EntityComponent;
import mindustry.gen.Unitc;

@EntityComponent
abstract class MyComp implements Unitc {
    @Override
    public void update() {
        Log.info("1");
    }
}
