package Ovulam.world.consumers;

import arc.func.Func;
import arc.scene.ui.layout.Table;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.LiquidStack;
import mindustry.ui.ReqImage;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;

public class ConsumeLiquidsDynamicCompletely extends Consume {
    public final Func<Building, LiquidStack[]> liquids;

    @SuppressWarnings("unchecked")
    public <T extends Building> ConsumeLiquidsDynamicCompletely(Func<T, LiquidStack[]> liquids){
        this.liquids = (Func<Building, LiquidStack[]>) liquids;
    }

    @Override
    public void apply(Block block){
        block.hasLiquids = true;
    }


    @Override
    public void build(Building build, Table table){
        LiquidStack[][] current = {liquids.get(build)};

        table.table(cont -> {
            table.update(() -> {
                if(current[0] != liquids.get(build)){
                    rebuild(build, cont);
                    current[0] = liquids.get(build);
                }
            });

            rebuild(build, cont);
        });
    }

    private void rebuild(Building build, Table table){
        table.clear();
        int i = 0;

        for(LiquidStack stack : liquids.get(build)){
            table.add(new ReqImage(stack.liquid.uiIcon,
                    () -> build.liquids != null && build.liquids.get(stack.liquid) > 0)).size(Vars.iconMed).padRight(8);
            if(++i % 4 == 0) table.row();
        }
    }

    @Override
    public void trigger(Building build){
        for(LiquidStack stack : liquids.get(build)){
            build.liquids.remove(stack.liquid, stack.amount);
        }
    }

    @Override
    public float efficiency(Building build){
        for(LiquidStack stack : liquids.get(build)){
            if(stack.amount < build.liquids.get(stack.liquid)){
                return 0;
            }
        }
        return 1;
    }
}
