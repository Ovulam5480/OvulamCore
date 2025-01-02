package Ovulam.world.block.distribution;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.ui.Fonts;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class Neuron extends Block {
    public float range = 25;
    public int maxTransmitterAmount = 30;
    public int minTransmitterAmount = 10;
    public float time = 120f;

    public Neuron(String name) {
        super(name);
        size = 3;
        hasLiquids = true;
        hasItems = true;
        acceptsItems = true;
        solid = true;
        itemCapacity = 120;
        liquidCapacity = 100f;
        update = true;
        sync = true;
    }



    public Rect rangeRect(float x, float y, float range) {
        return new Rect(x - range, y - range, range * 2, range * 2);
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * tilesize - offset, y * tilesize - offset, range * tilesize));
    }

    public static class Transmitter {
        public Building building;
        public ItemSeq items;
        public float progress;

        public Transmitter(Building building, ItemSeq items, float progress) {
            this.building = building;
            this.items = items;
            this.progress = progress;
        }
    }

    public class NeuronBuild extends Building {
        //获得物品的树突,输入
        public Seq<Transmitter> dendrite = new Seq<>();
        //获得物品的轴突,输出
        public @Nullable Transmitter[] axon = new Transmitter[Vars.content.items().size];
        public Building[] itemCurrent = new Building[Vars.content.items().size];

        public ItemSeq demand = new ItemSeq();
        public Seq<Building> otherNeurons = new Seq<>();


        public boolean acceptItem(Building source, Item item) {
            if (source.block == block) return true;

            if (!dendrite.contains(transmitter -> transmitter.building == source)) {
                dendrite.add(new Transmitter(source, new ItemSeq(), 0f));
            }

            return dendrite.find(transmitter1 -> transmitter1.building == source).progress == 0;
        }

        public void handleItem(Building source, Item item) {
            dendrite.find(transmitter1 -> transmitter1.building == source).items.add(item);
        }

        /*
                public Seq<Building> potentialInput = new Seq<>();
        public Seq<Building> otherNeurons = new Seq<>();

        public void classify(){
            Seq<Building> eachBlocks = new Seq<>();
            Vars.indexer.eachBlock(team, rangeRect(x, y, range * tilesize),
                    building -> building.block.hasItems = true, eachBlocks::addUnique);

            eachBlocks.each(building -> {
                if(building.block == block) otherNeurons.addUnique(this);
                else if(building.canConsume() || building.block.acceptsItems) potentialInput.addUnique(building);
            });
        }

         */

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
        }

        @Override
        public void updateTile() {
            //给所有存在物品的建筑的"proximity" 加入自身
            //并且查询物品需求

            otherNeurons.clear();
            demand.clear();

            Seq<Building> nearBuildings = new Seq<>();

            Vars.indexer.eachBlock(team, rangeRect(x, y, range * tilesize),
                    building -> building.block.hasItems && building != this,
                    building -> {
                        if (building.block == block) otherNeurons.add(building);
                        else {
                            building.proximity.addUnique(this);
                            nearBuildings.add(building);
                        }

                        for (Item item : Vars.content.items()) {
                            demand.add(item, building.acceptStack(item, maxTransmitterAmount, this));
                        }
                    }
            );


            //轴突部分，用于输入物资
            Seq<Building> remove = new Seq<>();

            for (Transmitter transmitter : dendrite) {
                Building building = transmitter.building;

                if (building.buildOn() != building) {
                    remove.addUnique(building);
                    continue;
                }

                if (transmitter.items.total >= maxTransmitterAmount) transmitter.progress += delta();

                if (transmitter.progress > time) {
                    items.add(transmitter.items);
                    transmitter.items.clear();
                    transmitter.progress = 0;
                }
            }

            remove.each(building -> dendrite.remove(transmitter -> transmitter.building == building));

            //轴突部分，用于输出物资
            //需求物品？？？
            for (int i = 0; i < Vars.content.items().size; i++) {
                Item item = Vars.content.items().get(i);

                if (axon[i] == null) {
                    //如果不含该物品，则跳过
                    if (items.get(item) < minTransmitterAmount) continue;

                    int itemDemand = 0;
                    Building buildingDemand = null;

                    for (Building building : otherNeurons) {
                        int amount = ((NeuronBuild) building).demand.get(item);
                        if (amount > itemDemand) {
                            itemDemand = amount;
                            buildingDemand = building;
                        }
                    }

                    for (Building building : nearBuildings) {
                        //如果其他枢纽的需求超过该建筑的最大接收容量，则直接跳过该建筑
                        if (getMaximumAccepted(item) <= itemDemand) continue;

                        int amount = building.acceptStack(item, maxTransmitterAmount, this);
                        if (amount > itemDemand) {
                            itemDemand = amount;
                            buildingDemand = building;
                        }
                    }
                    if(buildingDemand == null) continue;

                    ItemSeq itemSeq = new ItemSeq();
                    int availableItem = Math.min(buildingDemand.getMaximumAccepted(item), items.get(item));

                    itemSeq.add(item, Math.min(itemDemand, availableItem));

                    items.remove(itemSeq);
                    axon[i] = new Transmitter(buildingDemand, itemSeq, 0f);
                } else {
                    Transmitter transmitter = axon[i];
                    Building building = transmitter.building;

                    if (building.buildOn() != building) axon[i] = null;
                    transmitter.progress += delta();

                    if (transmitter.progress < time ) continue;

                    if(building.block == block)
                        ((NeuronBuild) building).itemCurrent[Vars.content.items().indexOf(item)] = this;

                    building.handleStack(item, maxTransmitterAmount, this);
                    axon[i] = null;
                }
            }

        }

        @Override
        public void draw() {
            Font font = Fonts.outline;
            font.draw(String.valueOf(demand.toSeq()), x, y - 20, Align.center);

            Draw.z(Layer.flyingUnit);

            dendrite.each((transmitter -> {
                Building building = transmitter.building;

                Drawf.line(Color.acid, x, y, building.x, building.y);
                Drawf.circles(building.x, building.y, 12);

                if (transmitter.items.total == 0) return;

                float px = Mathf.lerp(building.x, x, transmitter.progress / time);
                float py = Mathf.lerp(building.y, y, transmitter.progress / time);

                Draw.rect(transmitter.items.toSeq().first().item.fullIcon, px, py);
            }));

            for (int i = 0; i < Vars.content.items().size; i++) {
                if (axon[i] == null) continue;
                Item item = Vars.content.items().get(i);
                Transmitter transmitter = axon[i];
                Building building = transmitter.building;
                if (building == null) continue;
                Drawf.line(Color.cyan,x, y, building.x, building.y);
                Draw.rect(item.fullIcon, building.x, building.y);

                float px = Mathf.lerp(x, building.x, transmitter.progress / time);
                float py = Mathf.lerp(y, building.y, transmitter.progress / time);

                Draw.rect(transmitter.items.toSeq().first().item.fullIcon, px, py);

                Drawf.circles(building.x, building.y, 12, item.color);
            }
        }
    }

}
