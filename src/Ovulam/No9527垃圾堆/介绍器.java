package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.geom.Intersector;
import arc.math.geom.Point2;
import arc.math.geom.Rect;
import arc.struct.IntSeq;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Time;
import arc.util.Tmp;
import arc.util.io.Reads;
import arc.util.io.Writes;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.world.Block;
import mindustry.world.blocks.distribution.ItemBridge;
import mindustry.world.blocks.distribution.MassDriver;
import mindustry.world.blocks.power.PowerGraph;
import mindustry.world.blocks.power.PowerNode;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;
import mindustry.world.modules.PowerModule;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class 介绍器 extends Block {
    public float range = 20f;
    public 介绍器(String name) {
        super(name);
        hasItems = true;
        itemCapacity = 30;
        destructible = true;
        update = true;

        configurable = true;

        config(Integer.class, (介绍器Building entity, Integer value) -> {
            Building other = world.build(value);
            boolean contains = entity.buildings.contains(other), valid = other != null && other.block.hasItems;

            if(contains){
                entity.buildings.remove(other);
            }else if(linkValid(entity, other) && valid){
                entity.buildings.add(other);
            }
        });

        config(Point2[].class, (介绍器Building tile, Point2[] value) -> {
            IntSeq old = new IntSeq();
            tile.buildings.each(b -> old.add(b.pos()));

            //clear old
            for(int i = 0; i < old.size; i++){
                configurations.get(Integer.class).get(tile, old.get(i));
            }

            //set new
            for(Point2 p : value){
                configurations.get(Integer.class).get(tile, Point2.pack(p.x + tile.tileX(), p.y + tile.tileY()));
            }
        });
    }

    public boolean linkValid(Building tile, Building link){
        if(tile == link
                || link == null
                || !((link.block instanceof GenericCrafter) || (link.block instanceof Separator))
                || tile.team != link.team) return false;

        link.hitbox(Tmp.r2);

        return Intersector.intersectRectangles(Tmp.r1.setCentered(tile.x, tile.y, range * tilesize * 2), Tmp.r2, Tmp.r3);
    }

    public Rect rangeRect(float x, float y, float range) {
        return Tmp.r1.set(x - range, y - range, range * 2, range * 2);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * 8 + offset, y * 8 + offset, range * tilesize));
    }

    public class 介绍器Building extends Building {
        public Seq<Building> buildings = new Seq<>(), removes = new Seq<>();


        @Override
        public boolean acceptItem(Building source, Item item) {
            return buildings.contains(source) && items.total() < itemCapacity && (!hasConsumers || efficiency == 1);
        }

        @Override
        public void handleItem(Building source, Item item) {
            if(!put(item))super.handleItem(source, item);
        }

        @Override
        public boolean onConfigureBuildTapped(Building other){
            if(linkValid(this, other)){
                configure(other.pos());
                return false;
            }

            if(this == other){ //double tapped
                if(buildings.size == 0){ //find links
                    Seq<Point2> points = new Seq<>();
                    Vars.indexer.eachBlock(team, rangeRect(x, y, range * tilesize),
                            b -> linkValid(this, b),
                            link -> {
                                points.add(new Point2(link.tileX() - tile.x, link.tileY() - tile.y));
                            });
                    configure(points.toArray(Point2.class));
                }else{ //clear links
                    configure(new Point2[0]);
                }
                deselect();
                return false;
            }

            return true;
        }

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
            buildings.each(b -> {
                Drawf.square(b.x, b.y, b.block.size * 8 * 0.5f);
            });
        }

        @Override
        public void drawConfigure() {
            drawSelect();
            Drawf.circles(x, y, block.size * 8);
        }

        @Override
        public void updateTile() {
            buildings.each(b -> {
                if(b.tile.build != b)removes.add(b);

                if(!b.shouldConsume()){
                    b.proximity.addUnique(this);
                }
            });

            if(removes.size > 0){
                removes.each(b -> {
                    buildings.remove(b);
                    b.proximity.remove(this);
                });
                removes.clear();
            }

            dump();
        }

        @Override
        public Point2[] config(){
            Point2[] out = new Point2[buildings.size];
            for(int i = 0; i < out.length; i++){
                out[i] = Point2.unpack(buildings.get(i).pos()).sub(tile.x, tile.y);
            }
            return out;
        }

        @Override
        public void write(Writes write) {
            super.write(write);

            write.i(buildings.size);
            buildings.each(b -> write.i(b.pos()));
            Log.info(buildings.size);
        }

        @Override
        public void read(Reads read, byte revision) {
            super.read(read, revision);
            int amount = read.i();

            int[] poss = new int[amount];
            for (int i = 0; i < amount; i++){
                poss[i] = read.i();
            }

            Time.run(10f, () -> {
                for (int i : poss) {
                    Building building = world.build(i);
                    if(building != null && linkValid(this, building)){

                        buildings.add(building);
                    }
                }
            });

        }
    }
}