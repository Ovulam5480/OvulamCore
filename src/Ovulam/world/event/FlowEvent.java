package Ovulam.world.event;

import Ovulam.UI.EventAnimation;
import arc.util.Log;
import arc.util.Nullable;
import mindustry.Vars;

import static mindustry.Vars.state;

public abstract class FlowEvent {
    public float EventTime = 200f, timer;

    public boolean getTrigger;
    public Class<Object> eventType;

    public double previousTicks;

    public @Nullable EventAnimation startAnimation, endAnimation;

    public void trigger(){
        getTrigger = true;
        begin();

        Log.info("ti");
    }

    public void begin(){
        if(startAnimation != null)startAnimation.reset();
        previousTicks = state.tick;
        timer = 0;
    }

    public void end(){
        if(endAnimation != null)endAnimation.reset();

        getTrigger = false;
    }

    public void update(){
        timer += delta();

        if(getTrigger) {
            run();
            if(timer > EventTime)end();
        }
    }
    
    public void run(){};

    public float delta(){
        float delta = Vars.state.isPaused() ? 0 : (float) (state.tick - previousTicks);
        previousTicks = state.tick;
        return delta;
    }

    public boolean isRunning(){
        return getTrigger;
    }
}
