package Ovulam.world.block.storage;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.world.blocks.storage.StorageBlock;

import java.util.HashMap;

//todo 加入液体？
public class UnloaderStorage extends BaseStorageBlock {
    public float unloadTime = 5;

    public UnloaderStorage(String name) {
        super(name);
    }


    public class UnloaderStorageBuild extends BaseStorageBuild {
        public HashMap<Building, Float> buildingsTimer = new HashMap<>();


        @Override
        public void updateTile() {
            super.updateTile();

            Seq<Building> buildings = new Seq<>();
            buildings.addAll(proximity.select(building -> !(building.block instanceof StorageBlock) && building.block.hasItems));

            if (!hasCoreMerge()) {
                Seq<Building> remove = new Seq<>();
                buildingsTimer.forEach(((building, aFloat) -> {if (building.buildOn() != building) remove.add(building);}));
                remove.each(building -> buildingsTimer.remove(building));

                buildings.each(building -> {
                    if (!buildingsTimer.containsKey(building)) buildingsTimer.put(building, 0f);
                    float timer = buildingsTimer.get(building);

                    for (timer += delta(); timer > unloadTime; timer -= unloadTime)
                        for (int i = 0; i < Vars.content.items().size; i++) {
                            Item item = Vars.content.item(i);
                            if (items.get(item) > 0 && building.acceptItem(this, item)) {
                                building.handleItem(this, item);
                                items.remove(item, 1);
                                break;
                            }
                        }

                    buildingsTimer.put(building, timer);
                });
            } else {
                buildings.each(building -> {
                    for (Item item : Vars.content.items()){
                        if(items.get(item) == 0) continue;
                        int amount = building.acceptStack(item, items.get(item), this);
                        building.handleStack(item, amount, this);
                        items.remove(item, amount);
                    }
                });
            }
        }
    }
}
