package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.Item;
import mindustry.world.blocks.storage.StorageBlock;

//todo 加入液体？
public class UnloaderStorage extends BaseStorageBlock {
    public float unloadTime = 5;

    public UnloaderStorage(String name) {
        super(name);
    }


    public class UnloaderStorageBuild extends BaseStorageBuild {
        public ObjectMap<Building, Float> buildingsTimer = new ObjectMap<>();
        public Seq<Building> buildings = new Seq<>();
        public Seq<Building> remove = new Seq<>();

        @Override
        public void draw() {
            super.draw();
            Draw.z(Layer.playerName);
            buildings.each(building -> Drawf.circles(building.x, building.y, 3));
        }


        @Override
        public void updateTile() {
            super.updateTile();

            buildings.clear();

            buildings = proximity.select(building -> !(building.block instanceof StorageBlock) && building.block.acceptsItems).copy();

            if (!hasCoreMerge()) {
                buildingsTimer.each(((building, aFloat) -> {if (building.buildOn() != building) remove.add(building);}));
                remove.each(building -> buildingsTimer.remove(building));
                remove.clear();

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
                        //某些物流原件的acceptStack为0, 这些原件不需要也不能接收物品堆
                        int amount = building.acceptStack(item, items.get(item), this);
                        if(items.get(item) == 0 || amount == 0) continue;
                        building.handleStack(item, amount, this);
                        items.remove(item, amount);
                    }
                });
            }
        }
    }
}
