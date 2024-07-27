package Ovulam.world.event;

import Ovulam.UI.EventAnimation;
import arc.Events;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.EventType;

import static mindustry.Vars.state;

public abstract class OvulamEvent{
    public float EventTime = 200f, timer;
    public boolean updating;
    public boolean getTrigger;
    public double previousTicks;

    public @Nullable EventAnimation startAnimation, endAnimation;

    public OvulamEvent(){
        trigger();
        Events.run(EventType.Trigger.update, () ->{
            if(getTrigger){
                updating = true;
                begin();
            }

            if(updating) update();
        });
    }

    public abstract void trigger();


    public void begin(){
        if(startAnimation != null)startAnimation.reset();
        previousTicks = state.tick;
        timer = 0;

        getTrigger = false;
    }

    public void end(){
        if(endAnimation != null)endAnimation.reset();

        updating = false;
    }

    public void update(){
        timer += Vars.state.isPaused() ? 0 : (float) (state.tick - previousTicks);
        previousTicks = state.tick;

        if(timer > EventTime)end();
    }

}
