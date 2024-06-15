package Ovulam.world.event;

import Ovulam.world.block.block.EventBlock.PreparationTimeBlock;
import arc.Events;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.EventType;
import mindustry.type.ItemSeq;
import mindustry.type.Sector;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

//达到某个条件时, 跳转到某个地图
public class LaunchSecondSector{
    public CoreBlock coreBlock = (CoreBlock) Blocks.coreShard;

    public LaunchSecondSector(Sector from, Sector to){
        Events.on(PreparationTimeBlock.PreparationFinish.class, e -> {
            if(state.isCampaign() && state.getSector() == from){
                Vars.renderer.showLaunch(coreBlock);
                Time.runTask(coreLandDuration - 8f, () -> Vars.control.playSector(from, to));
            }
        });
        Events.on(EventType.SectorLaunchEvent.class, e -> {
            if(e.sector == to){
                universe.updateLaunchResources(new ItemSeq());
            }
        });

    }
}
