package Ovulam.modContent;

import Ovulam.world.event.FlowEvents.NetherExpedition;
import Ovulam.world.stage.FlowEventTrigger;
import Ovulam.world.stage.OvulamStage;
import arc.math.Mathf;
import arc.struct.Seq;
import mindustry.content.SectorPresets;

public class OvulamStages {
    public static OvulamStage test;
    public static void init(){
        test = new OvulamStage(SectorPresets.groundZero.sector, Seq.with(new FlowEventTrigger(
                new NetherExpedition(SectorPresets.groundZero.sector,
                        SectorPresets.frozenForest.sector, 200f), 222, () -> Mathf.chance(0.4f)
        )));
    }
}
