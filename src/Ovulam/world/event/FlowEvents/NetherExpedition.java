package Ovulam.world.event.FlowEvents;

import Ovulam.modContent.OvulamEventAnimations;
import Ovulam.world.event.FlowEvent;
import arc.util.Log;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.type.ItemSeq;
import mindustry.type.Sector;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.*;

//地府远征
//达到某个条件时, 跳转到某个地图
public class NetherExpedition extends FlowEvent {
    public CoreBlock coreBlock = (CoreBlock) Blocks.coreShard;
    public Sector from;
    public Sector to;

    public ItemSeq itemSeq = new ItemSeq();

    public NetherExpedition(Sector from, Sector to, float preparationTime) {
        this.from = from;
        this.to = to;
        this.EventTime = preparationTime;

        startAnimation = OvulamEventAnimations.netherExpedition;
    }

    @Override
    public void trigger() {
        super.trigger();
        Log.info("AAAAAAAAAAAAAA");
    }

    public void end() {
        if (state.isCampaign() && state.getSector() == from) {
            //todo
            Vars.renderer.showLaunch(coreBlock);
            Time.runTask(coreLandDuration - 8f, () -> Vars.control.playSector(from, to));
        }
        universe.updateLaunchResources(itemSeq);

        super.end();
    }

    public void update() {
        super.update();
    }
}
