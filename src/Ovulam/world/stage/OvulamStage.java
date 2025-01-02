package Ovulam.world.stage;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.type.Sector;

public class OvulamStage {
    public ObjectMap<EventTrigger, Integer> events = new ObjectMap<>();

    public OvulamStage(Sector sector, Seq<EventTrigger> conditionEvents){
        conditionEvents.each(event -> {
            events.put(event, 0);
            event.sector = sector;
        });
    }

}
