package Ovulam.world.event;

import Ovulam.modContent.OvulamEventAnimations;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.type.ItemSeq;
import mindustry.type.Sector;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

//达到某个条件时, 在一段时间后发射到某个地图
public class LaunchEvent extends OvulamEvent {
    public CoreBlock coreBlock = (CoreBlock) Blocks.coreShard;
    public Sector from;
    public Sector to;

    public ItemSeq itemSeq = new ItemSeq();

    public LaunchEvent(Sector from, Sector to, float completeTime) {
        super(null, OvulamEventAnimations.netherExpedition, completeTime);

        this.from = from;
        this.to = to;
    }

    @Override
    public void finish() {
        if (Vars.state.isCampaign() && Vars.state.getSector() == from) {
            //todo
            Vars.renderer.showLaunch(coreBlock);
            Time.runTask(coreLandDuration - 8f, () -> Vars.control.playSector(from, to));
        }
        universe.updateLaunchResources(itemSeq);

        super.finish();
    }

    public void drawLaunchPads(){
        float fout = renderer.getLandTime() / coreLandDuration;

        if(renderer.isLaunching()) fout = 1f - fout;

        float fin = 1f - fout;

    }
}
