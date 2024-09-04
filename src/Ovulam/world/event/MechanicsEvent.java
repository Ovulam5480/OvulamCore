package Ovulam.world.event;

import Ovulam.UI.EventAnimation;
import arc.util.Nullable;

public abstract class MechanicsEvent {
    public @Nullable EventAnimation startAnimation;

    public MechanicsEvent(){
        trigger();
    }


    public abstract void trigger();


    public void run(){
        if(startAnimation != null)startAnimation.reset();
    }

}
