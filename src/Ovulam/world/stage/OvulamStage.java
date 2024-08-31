package Ovulam.world.stage;

import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Sector;

public class OvulamStage {
    public Sector sector;
    public ObjectMap<FlowEvent, Integer> events = new ObjectMap<>();

    public OvulamStage(Sector sector, Seq<FlowEvent> conditionEvents){
        this.sector = sector;

        conditionEvents.each(event -> {
            events.put(event, 0);

            if(event.eventType != null){
                Events.on(event.eventType, t -> {
                    if(!event.isUpdating()) event.getTrigger = true;
                });
            }
        });

        update();
    }

    public void update(){
        Events.run(EventType.Trigger.update, () -> {
            if(!correctSector())return;

            events.each((e, i) -> {
                int current = events.get(e);

                if(e.getTrigger && current < e.triggerLimit && !e.isUpdating()){
                    e.trigger();
                    events.put(e, current + 1);
                }

                e.update();
            });
        });

    }

    public boolean correctSector(){
        return Vars.state.isCampaign() && Vars.state.getSector() == sector;
    }
}
