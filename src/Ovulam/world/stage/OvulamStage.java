package Ovulam.world.stage;

import arc.Events;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.type.Sector;

public class OvulamStage {
    public Sector sector;
    public ObjectMap<FlowEventTrigger, Integer> events = new ObjectMap<>();

    public OvulamStage(Sector sector, Seq<FlowEventTrigger> conditionEvents){
        this.sector = sector;

        conditionEvents.each(event -> {
            events.put(event, 0);
        });

        update();
    }

    public void update(){
        Events.run(EventType.Trigger.update, () -> {
            //if(!correctSector())return;

            events.each((e, i) -> {
                e.update();
            });
        });

    }

    public boolean correctSector(){
        return Vars.state.isCampaign() && Vars.state.getSector() == sector;
    }
}
