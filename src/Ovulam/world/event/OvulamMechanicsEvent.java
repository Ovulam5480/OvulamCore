package Ovulam.world.event;

import Ovulam.UI.EventAnimation;
import arc.util.Nullable;

//机制事件, trigger()直接调用begin(), trigger()由事件触发
//不需要更新
public abstract class OvulamMechanicsEvent {
    public @Nullable EventAnimation startAnimation;

    public OvulamMechanicsEvent(){
        trigger();
    }


    public abstract void trigger();


    public void begin(){
        if(startAnimation != null)startAnimation.reset();
    }

}
