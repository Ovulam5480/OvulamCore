package Ovulam.world.mechanics;

import Ovulam.modContent.OvulamEventAnimations;
import arc.Events;
import mindustry.content.TechTree;
import mindustry.ctype.UnlockableContent;
import mindustry.game.EventType;
import mindustry.type.Sector;

import static mindustry.Vars.state;

//失去研究进度
//当区块丢失时, 清除某个研究的研究进度
public class ClearResearchSchedule extends Mechanics {
    public Sector sector;
    public UnlockableContent tech;

    public boolean lostSector;

    public ClearResearchSchedule(Sector sector, UnlockableContent content){
        this.sector = sector;
        this.tech = content;
        startAnimation = OvulamEventAnimations.researchLost;
    }

    @Override
    public void trigger() {
        Events.on(EventType.LoseEvent.class, e -> {
            if(state.isCampaign() && state.getSector() == sector){
                lostSector = true;
            }
        });
        Events.on(EventType.WorldLoadEvent.class, e -> {
            if(state.isCampaign() && state.getSector().planet == sector.planet && lostSector){
                run();
                lostSector = false;
            }
        });
    }

    public void run(){
        clearSchedule(tech);
    }

    public void clearSchedule(UnlockableContent content){
        if(content.alwaysUnlocked) return;
        TechTree.TechNode techNode = content.techNode;

        if(techNode != null)techNode.reset();
        content.clearUnlock();
    }

}
