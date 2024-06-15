package Ovulam.mod;

import Ovulam.world.event.ClearResearchSchedule;
import Ovulam.world.event.LaunchSecondSector;
import Ovulam.world.event.PreparationTime;
import mindustry.content.Blocks;
import mindustry.content.SectorPresets;

public class OvulamEvents {
    //科技清除
    public static ClearResearchSchedule frozenForest;
    //准备时间
    public static PreparationTime lost;
    //发射区块
    public static LaunchSecondSector see;
    public static void init(){
        frozenForest = new ClearResearchSchedule(SectorPresets.frozenForest.sector, Blocks.battery);
        lost = new PreparationTime(100f, SectorPresets.frozenForest.sector);
        see = new LaunchSecondSector(SectorPresets.groundZero.sector, SectorPresets.frozenForest.sector);


    }
}
