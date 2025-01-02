package Ovulam.world.stage;

import Ovulam.world.event.OvulamEvent;
import arc.Events;
import arc.func.Boolp;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Sector;

public class EventTrigger {
    //事件
    public OvulamEvent event;
    //触发次数上限
    public int triggerLimit;
    //触发区块
    public @Nullable Sector sector;

    public <T> EventTrigger(OvulamEvent ovulamEvent, int triggerLimit, Boolp condition, Class<T> type){
        this.event = ovulamEvent;
        this.triggerLimit = triggerLimit;

        Events.on(type, t -> {
            if(condition.get() && !inDuration() && correctSector()) trigger();
        });
    }

    public EventTrigger(OvulamEvent ovulamEvent, int triggerLimit, Boolp condition){
        this.event = ovulamEvent;
        this.triggerLimit = triggerLimit;

        Events.run(EventType.Trigger.update, () -> {
            if(condition.get() && !inDuration() && correctSector()) trigger();
        });
    }

    public <T> EventTrigger(OvulamEvent ovulamEvent, int triggerLimit, Class<T> type){
        this(ovulamEvent, triggerLimit, () -> true, type);
    }


    public void trigger(){
        //event.trigger();
    }

    public boolean correctSector(){
        if(sector != null)return Vars.state.isCampaign() && Vars.state.getSector() == sector;
        return true;
    }

    public boolean inDuration(){
        return event.inDuration;
    }
}