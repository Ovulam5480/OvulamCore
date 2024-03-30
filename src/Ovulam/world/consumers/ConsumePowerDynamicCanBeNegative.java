package Ovulam.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;

public class ConsumePowerDynamicCanBeNegative extends ConsumePower {
    private final Floatf<Building> usage;

    //哎嘿！
    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumePowerDynamicCanBeNegative(Floatf<T> usage){
        super(0, 0, false);
        this.usage = (Floatf<Building>) usage;
    }

    @Override
    public float efficiency(Building build){
        if(usage.get(build) <= 0)return 1;
        return build.power.status;
    }

    @Override
    public float requestedPower(Building entity){
        return usage.get(entity);
    }
}
