package Ovulam.world.event;

import arc.Events;
import mindustry.game.EventType;
import mindustry.type.Sector;

import static mindustry.Vars.state;

public class PreparationTime{
    public PreparationTime(float time, Sector sector){
        Events.on(EventType.LoseEvent.class, e -> {
            if(state.isCampaign() && state.getSector() == sector){
                Events.fire(new StartPreparation(time));
            }
        });
    }

    //Event
    public static class StartPreparation{
        public float preparationTime;
        public StartPreparation(float preparationTime){
            this.preparationTime = preparationTime;
        }
    }
}
