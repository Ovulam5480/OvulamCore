package Ovulam.No9527垃圾堆;

import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.consumers.Consume;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;

//todo 完善合并工厂
public class MergeCrafter extends Block {
    public ItemStack outputItem;
    public float craftTime = 80;
    public String inputDescription = "这是默认的输入描述,请修改";

    public MergeCrafter(String name) {
        super(name);
        update = true;
        solid = true;
        hasItems = true;
        sync = true;
        rotate = false;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (MergeCrafterBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    @Override
    public void setStats() {
        stats.timePeriod = craftTime;
        super.setStats();
        stats.add(Stat.productionTime, craftTime / 60f, StatUnit.seconds);
        stats.add(Stat.output, StatValues.items(craftTime, outputItem));
        stats.remove(Stat.input);
        stats.add(Stat.input, inputDescription);
    }

    public class MergeCrafterBuild extends Building {
        public float progress;
        public Consume consume;

        @Override
        public void displayConsumption(Table table) {
            table.left();
            if (consume != null) consume.build(this, table);
        }

        @Override
        public boolean shouldConsume() {
            if (items.get(outputItem.item) + outputItem.amount > itemCapacity) return false;
            return enabled;
        }

        @Override
        public float progress() {
            return progress / craftTime;
        }

        @Override
        public void updateTile() {
            Consume currentConsume = null;

            for (Consume consume : consumers) {
                if (consume.efficiency(this) > 0) {
                    efficiency(consume.efficiency(this));
                    currentConsume = consume;
                    break;
                }
            }

            consume = currentConsume;

            if (currentConsume == null || !shouldConsume()) return;

            progress += delta();

            if (progress > craftTime) {
                currentConsume.trigger(this);
                for (int i = 0; i < outputItem.amount; i++) offload(outputItem.item);
                progress -= craftTime;
            }
        }
    }
}
