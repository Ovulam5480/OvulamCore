package Ovulam.world.consumers;

import arc.func.Floatf;
import mindustry.gen.Building;
import mindustry.world.consumers.ConsumePower;

public class ConsumePowerDDynamic extends ConsumePower {
    private final Floatf<Building> usage;

    //哎嘿！
    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumePowerDDynamic(Floatf<T> usage){
        super(0, 0, false);
        this.usage = (Floatf<Building>) usage;
    }

    @Override
    public float requestedPower(Building entity){
        return usage.get(entity);
    }
}
