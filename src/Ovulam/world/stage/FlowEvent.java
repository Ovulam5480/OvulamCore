package Ovulam.world.stage;

import Ovulam.world.event.OvulamFlowEvent;
import arc.Events;


public class FlowEvent {
    //事件
    public OvulamFlowEvent event;
    //触发次数上限
    public int triggerLimit;

    public Class<Object> eventType;

    public boolean getTrigger;

    public FlowEvent(OvulamFlowEvent event, int triggerLimit){
        this.event = event;
        this.triggerLimit = triggerLimit;
    }

    public FlowEvent(OvulamFlowEvent event, int triggerLimit, boolean condition){
        this(event, triggerLimit);
        this.getTrigger = condition;
    }

    public <T> FlowEvent(OvulamFlowEvent event, int triggerLimit, Class<T> type){
        this(event, triggerLimit);

        this.eventType = (Class<Object>) type;

        Events.on(type, t -> {
            if(!isUpdating()) getTrigger = true;
        });
    }

    public boolean isUpdating(){
        return event.isUpdating();
    }

    public void trigger(){
        getTrigger = false;
        event.trigger();
    }

    public void update(){
        event.update();
    }

}
