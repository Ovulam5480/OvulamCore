package Ovulam.world.mechanics;

import Ovulam.UI.EventAnimation;
import arc.util.Nullable;

public abstract class Mechanics {
    public @Nullable EventAnimation startAnimation;

    public Mechanics(){
        trigger();
    }


    public abstract void trigger();

}
