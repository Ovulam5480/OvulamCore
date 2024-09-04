package Ovulam.modContent;

import Ovulam.world.event.MechanicsEvents.ClearResearchSchedule;
import Ovulam.world.event.FlowEvents.NetherExpedition;
import mindustry.content.Blocks;
import mindustry.content.Planets;
import mindustry.content.SectorPresets;

public class OvulamMechanicsEvents {

    //科技清除
    public static ClearResearchSchedule frozenForest;
    //发射区块
    public static NetherExpedition see;
    public static void init(){
        frozenForest = new ClearResearchSchedule(SectorPresets.frozenForest.sector, Blocks.battery);

        see = new NetherExpedition(Planets.serpulo.sectors.get(175), SectorPresets.frozenForest.sector, 240f);


    }
}
