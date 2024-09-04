package Ovulam.world.stage;

import Ovulam.world.event.FlowEvent;
import arc.Events;
import arc.func.Boolp;
import arc.util.Log;
import mindustry.game.EventType;


public class FlowEventTrigger {
    //事件
    public FlowEvent event;
    //触发次数上限
    public int triggerLimit;


    public <T> FlowEventTrigger(FlowEvent event, int triggerLimit, Boolp condition, Class<T> type){
        this.event = event;
        this.triggerLimit = triggerLimit;

        Events.on(type, t -> {
            if(condition.get() && !isRunning()) trigger();
        });

    }

    public <T> FlowEventTrigger(FlowEvent event, int triggerLimit, Class<T> type){
        this(event, triggerLimit, () -> true, type);
    }

    public FlowEventTrigger(FlowEvent event, int triggerLimit, Boolp condition){
        this.event = event;
        this.triggerLimit = triggerLimit;

        Events.run(EventType.Trigger.update, () -> {
            if(condition.get() && !isRunning()) trigger();
        });

        Events.run(EventType.Trigger.update, () -> Log.info(condition.get()));
    }

    public void trigger(){
        event.trigger();
    }

    public boolean isRunning(){
        return event.isRunning();
    }

    public void update(){
        event.update();
    }

}
