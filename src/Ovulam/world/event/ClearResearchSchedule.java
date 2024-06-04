package Ovulam.world.event;

import arc.Events;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.type.Sector;

import static mindustry.Vars.state;

//当区块丢失时, 清除某个研究的研究进度
public class ClearResearchSchedule{
    public ClearResearchSchedule(Sector sector, UnlockableContent content){
        //todo gameOverEvent, 但是似乎不是必要的? 起码现在不是必要
        Events.on(EventType.LoseEvent.class, e -> {
            if(state.isCampaign() && state.getSector() == sector){
                clearSchedule(content);
            }
        });
    }

    public void clearSchedule(UnlockableContent content){
        if(content.alwaysUnlocked) return;
        TechTree.TechNode techNode = content.techNode;

        if(techNode != null)techNode.reset();
        content.clearUnlock();
    }
}
