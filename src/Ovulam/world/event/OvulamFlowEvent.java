package Ovulam.world.event;

import Ovulam.UI.EventAnimation;
import arc.util.Nullable;
import mindustry.Vars;

import static mindustry.Vars.state;

//流程事件, trigger()用于将getTrigger设为true
public class OvulamFlowEvent{
    public float EventTime = 200f, timer;
    public boolean updating;
    public boolean getTrigger;
    public double previousTicks;

    public @Nullable EventAnimation startAnimation, endAnimation;

    public OvulamFlowEvent(){
    }

    public void trigger(){
        getTrigger = true;
    }

    public void begin(){
        if(startAnimation != null)startAnimation.reset();
        previousTicks = state.tick;
        timer = 0;
    }

    public void end(){
        if(endAnimation != null)endAnimation.reset();

        updating = false;
    }

    public void update(){
        timer += delta();

        if(getTrigger){
            updating = true;
            getTrigger = false;
            begin();
        }

        if(!updating)return;


        if(timer > EventTime)end();
    }

    public float delta(){
        float delta = Vars.state.isPaused() ? 0 : (float) (state.tick - previousTicks);
        previousTicks = state.tick;
        return delta;
    }

    public boolean isUpdating(){
        return updating;
    }
}
