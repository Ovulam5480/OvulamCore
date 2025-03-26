package Ovulam.world.block.production;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Nullable;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Tile;
import mindustry.world.blocks.environment.Floor;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatUnit;
import mindustry.world.meta.StatValues;
import Ovulam.world.block.block.PayloadOre;

import static arc.util.Time.time;
import static mindustry.Vars.*;

public class  PayloadDrill extends PayloadBlock {
    public PayloadOre oreBlock;
    protected final ObjectIntMap<Item> oreCount = new ObjectIntMap<>();
    protected final Seq<Item> itemArray = new Seq<>();
    //需不需要这个还得再想
    public float hardnessDrillMultiplier = 5f;
    public int tier;
    public @Nullable Item blockedItem;
    public float drillTime = 5;
    public float liquidBoostIntensity = 1.6f;
    public DrawBlock drawer = new DrawDefault();
    public float glowScl = 3f;
    public float pulseIntensity = 0.07f;
    public float laserWidth = 0.45f;

    public TextureRegion region, iconRegion;
    public TextureRegion topRegion;
    public TextureRegion shadowRegion;
    public TextureRegion laserDrillRegion;
    public float laserDrillRadius = 11f;
    public TextureRegion laser;
    public TextureRegion laserEnd;
    public TextureRegion laserCenter;

    public boolean hasTop = true;

    //todo 详细页面
    //todo 挖石头?rrrrrrrrrcs
    public PayloadDrill(String name){
        super(name);
        hasConsumers = true;
        hasItems = false;
        hasLiquids = true;
        update = true;
        hasShadow = false;
        rotate = true;
        rotateDraw = false;
        outputsPayload = true;
        //
        //outlineIcon = true;
        //outlinedIcon = 2;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        iconRegion = Core.atlas.find(name + "-icon");
        if(hasTop)topRegion = Core.atlas.find(name + "-top");
        shadowRegion = Core.atlas.find(name + "-shadow");
        laserDrillRegion = Core.atlas.find(name + "-laserDrill");
        laser = Core.atlas.find("laser");
        laserEnd = Core.atlas.find("laser-end");
        laserCenter = Core.atlas.find("laser-center");
        drawer.load(this);
    }

    @Override
    public void setStats(){
        super.setStats();

        stats.add(Stat.drillTier, StatValues.blocks(b -> b instanceof Floor f && !f.wallOre && f.itemDrop != null &&
                f.itemDrop.hardness <= tier && f.itemDrop != blockedItem && (indexer.isBlockPresent(f) || state.isMenu())));

        stats.add(Stat.drillSpeed, 60f / drillTime * size * size, StatUnit.itemsSecond);
        if(liquidBoostIntensity != 1){
            stats.add(Stat.boostEffect, liquidBoostIntensity * liquidBoostIntensity, StatUnit.timesSpeed);
        }

    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("progress", (PayloadDrillBuild entity) -> new Bar("bar.progress", Pal.ammo, entity::progress));
    }


    public boolean canMine(Tile tile){
        if(tile == null || tile.block().isStatic()) return false;
        Item drops = tile.drop();
        return drops != null && drops.hardness <= tier && drops != blockedItem;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(isMultiblock()){
            for (Tile other : tile.getLinkedTilesAs(this, tempTiles)){
                if(canMine(other)){
                    return true;
                }
            }
            return false;
        } else {
            return canMine(tile);
        }
    }

    public Item getDrop(Tile tile){
        return tile.drop();
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{iconRegion};
    }

    protected void countOre(Tile tile, ObjectIntMap<Item> oreCount, Seq<Item> itemArray){
        oreCount.clear();
        itemArray.clear();
        for (Tile other : tile.getLinkedTilesAs(this, tempTiles)){
            if(canMine(other)){
                oreCount.increment(getDrop(other), 0, 1);
            }
        }
        for (Item item : oreCount.keys()){
            itemArray.add(item);
        }
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Tile tile = world.tile(x, y);
        if(tile == null) return;
        countOre(tile, oreCount, itemArray);

        if(itemArray.size > 0){
            for (int i = 0; i < itemArray.size; i++){
                Item item = itemArray.get(i);
                float width = drawPlaceText(Core.bundle.formatFloat("bar.drillspeed", 60f / getDrillTime(item) * oreCount.get(item), 2), x, y + i, valid);
                float dx = x * tilesize + offset - width / 2f - 4f,
                        dy = y * tilesize + offset + size * tilesize / 2f + 5 + i * tilesize,
                        s = iconSmall / 4f;
                Draw.mixcol(Color.darkGray, 1f);
                Draw.rect(item.fullIcon, dx, dy - 1, s, s);
                Draw.reset();
                Draw.rect(item.fullIcon, dx, dy, s, s);
            }
        }
    }

    //删掉了特定物品的倍率
    //单位：帧数/物品
    public float getDrillMultiTime(Item item){
        return (drillTime + hardnessDrillMultiplier * item.hardness) / drillTime;
    }

    public float getDrillTime(Item item){
        return getDrillMultiTime(item) * drillTime;
    }

    public class PayloadDrillBuild extends PayloadBlockBuild<BuildPayload> {
        //搞这么多干嘛(
        public float progress;
        public float warmup;
        //覆盖的矿石全挖一遍所用的时间
        public float fullTime;
        float[] wangle = new float[4];


        public Seq<Item> dominantItems = new Seq<>();
        public ObjectIntMap<Item> dominantItemss = new ObjectIntMap<>();

        @Override
        public boolean shouldConsume(){
            return true;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return false;
        }


        @Override
        public float progress(){
            if(payload != null) return 0;
            return dominantItems.size == 0 ? 0f : Mathf.clamp(progress / fullTime);
        }


        /*      到普通端进行测试
                @Override
                public void drawSelect(){
                    //如果挖取矿物不为无
                    if(dominantItem != null){
                        float dx = x - size * tilesize/2f, dy = y + size * tilesize/2f, s = iconSmall / 4f;
                        Draw.mixcol(Color.darkGray, 1f);
                        Draw.rect(dominantItem.fullIcon, dx, dy - 1, s, s);
                        Draw.reset();
                        Draw.rect(dominantItem.fullIcon, dx, dy, s, s);
                    }
                }

         */
        @Override
        public void onProximityUpdate(){
            super.onProximityUpdate();
            countOre(tile, dominantItemss, dominantItems);
        }

        public float getOreFraction(Item item){
            float allOreSpeed = getOreTime();
            return dominantItemss.get(item) / getDrillTime(item) / allOreSpeed;
        }

        public float getOreTime(){
            float oreSpeed = 0;
            for (Item ore : dominantItems){
                oreSpeed += dominantItemss.get(ore) / getDrillTime(ore);
            }
            fullTime = oreBlock.itemCapacity / oreSpeed;
            return oreSpeed;
        }


        @Override
        public void draw(){
            Drawf.shadow(shadowRegion, x, y);
            Draw.rect(region, x, y);
            //Draw.rect(outRegion, x, y, rotdeg());

            if(payload == null){
                Drawf.shadow(x, y, oreBlock.size * tilesize * 2f, progress());
                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent);

                    for (TextureRegion region : oreBlock.getGeneratedIcons()){
                        Shaders.blockbuild.region = region;
                        Shaders.blockbuild.time = time;
                        Shaders.blockbuild.progress = progress();

                        Draw.rect(region, x, y, oreBlock.rotate ? rotdeg() : 0);
                        Draw.flush();
                    }
                    Draw.color();
                });
                Draw.z(Layer.blockBuilding + 1);
                Draw.color(Pal.accent);

                Lines.lineAngleCenter(x + Mathf.sin(time, 10f, Vars.tilesize / 2f * oreBlock.size + 1f), y, 90, oreBlock.size * Vars.tilesize + 1f);

                Draw.reset();
            }
            drawPayload();

            Draw.z(Layer.blockBuilding + 1.1f);
            if(hasTop)Draw.rect(topRegion, x, y);
            int trns = oreBlock.size * tilesize / 2;
            //激光发射的目标位置
            float lx = progress() * trns * Mathf.sin(progress() * 6 * 3.14f) + x;
            float ly = progress() * trns * Mathf.cos(progress() * 6 * 3.14f) + y;
            float width = (laserWidth + Mathf.absin(time + 5 + (id % 9) * 9, glowScl, pulseIntensity)) * warmup;
            for (int i = 0; i < 4; i++){
                //各激光炮台的位置
                float px = x + Geometry.d8edge(i).x * laserDrillRadius;
                float py = y + Geometry.d8edge(i).y * laserDrillRadius;
                float angle = (float) Math.toDegrees(Math.atan((ly - py) / (lx - px))) + 90 * Geometry.d8edge(i).x;

                wangle[i] = Mathf.lerpDelta(wangle[i], angle, 0.05f);
                Draw.rect(laserDrillRegion, px, py, wangle[i]);
                //激光炮台的炮管位置, 8 = 4 * 2
                float p2x = px + Mathf.cos((float) Math.toRadians(wangle[i] + 90)) * laserDrillRegion.height / 8;
                float p2y = py + Mathf.sin((float) Math.toRadians(wangle[i] + 90)) * laserDrillRegion.height / 8;
                //Draw.rect(laserCenter,p2x,p2y);
                Drawf.laser(laser, laserEnd, p2x, p2y, lx, ly, width);
            }
        }


        @Override
        public void updateTile(){
            if(dominantItems.size == 0){
                return;
            }
            getOreTime();

            if(efficiency > 0 && payload == null){
                float speed = Mathf.lerp(1f, liquidBoostIntensity, optionalEfficiency) * efficiency;
                warmup = Mathf.approachDelta(warmup, speed, 1f);
                progress += delta() * speed * warmup;
            } else {
                warmup = Mathf.approachDelta(warmup, 0f, 1f);
            }
            if(progress >= fullTime){
                payload = new BuildPayload(oreBlock, team);
                for (Item item : dominantItems){
                    payload.build.items.add(item, (int) (getOreFraction(item) * payload.block().itemCapacity));
                }
                payload.block().placeEffect.at(x, y, payload.size() / tilesize);
                payVector.setZero();
                progress %= fullTime;
            }
            moveOutPayload();
        }

    }
}
