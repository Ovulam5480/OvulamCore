package Ovulam.mod;

import Ovulam.UI.StoryTable;
import arc.Core;
import arc.Events;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.type.UnitType;

public class OvulamEvents {
    public static StoryTable storyTable;

    public static void unitD(UnitType unitType){
        Events.on(EventType.UnitDestroyEvent.class, e -> {
            if(e.unit.type == unitType){
                storyTable = new StoryTable(
                        UnitTypes.mono.fullIcon,"这是老虎",
                        UnitTypes.aegires.fullIcon,"这是狐狸",
                        UnitTypes.alpha.fullIcon,"这是狮子",
                        UnitTypes.anthicus.fullIcon,"这是绵羊"
                );
                Core.scene.add(storyTable.storyTable);
            }
        });
    }

    public static void load(){
        unitD(UnitTypes.mono);
    }
}
