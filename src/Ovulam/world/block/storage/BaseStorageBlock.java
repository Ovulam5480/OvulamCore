package Ovulam.world.block.storage;

import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.units.BuildPlan;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;

import static mindustry.Vars.player;
import static mindustry.Vars.world;

//todo 仓库周围放置核心时, 仓库的物品合并到核心内
public class BaseStorageBlock extends StorageBlock {
    public TextureRegion region, topRegion, teamRegion1, teamRegion2, iconRegion;

    public BaseStorageBlock(String name) {
        super(name);
        update = true;
        size = 3;
        itemCapacity = 1000;
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        teamRegion1 = Core.atlas.find(name + "-team-1");
        teamRegion2 = Core.atlas.find(name + "-team-2");
        iconRegion = Core.atlas.find(name + "-icon");
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{iconRegion};
    }

    @Override
    public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid){
        Draw.reset();
        Draw.mixcol(!valid ? Pal.breakInvalid : Color.white, (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
        Draw.rect(region, plan.drawx(), plan.drawy());
        Draw.rect(topRegion, plan.drawx(), plan.drawy());
        Draw.reset();
        Draw.mixcol(faceCore(plan.drawx() / 8, plan.drawy() / 8, plan.rotation) != null ? player.team().color : Pal.remove,
                !valid ? Pal.breakInvalid : Color.white,
                (!valid ? 0.4f : 0.24f) + Mathf.absin(Time.globalTime, 6f, 0.28f));
        Draw.rect(Mathf.mod((plan.rotation), 4) < 2 ? teamRegion1 : teamRegion2, plan.drawx(), plan.drawy(), plan.rotation * 90);
        Draw.reset();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);
        Building core = faceCore(x + offset / 8, y + offset / 8, rotation);
        if(core != null){
            Drawf.circles(core.x, core.y, core.block.size * 5, Pal.accent);
        }
    }

    public Building faceCore(float x, float y, int rotation){
        float trns = size / 2f + 0.1f + (Mathf.mod((rotation), 4) < 2 ? 1f : 0);
        Building building = world.build((int) (x + Geometry.d4(rotation).x * trns), (int) (y + Geometry.d4(rotation).y * trns));
        return building instanceof CoreBlock.CoreBuild ? building : null;
    }

    public class BaseStorageBuild extends StorageBuild{
        public int side = -1;
        public boolean remoteLink = false;
        public boolean bossAugment;

        public void setBossAugment(boolean bossAugment){
            this.bossAugment = bossAugment;
        }

        public @Nullable Seq<Building> getProximityCore(){
            return proximity.select(building -> building.block instanceof CoreBlock);
        }

        //已经链接核心
        public boolean hasCoreMerge(){
            return !getProximityCore().isEmpty();
        }

        //完全链接核心
        public @Nullable Building completelyLinkedCore(){
            if(remoteLink) return Vars.state.teams.closestCore(x, y, team);
            return getProximityCore().find(building -> building.x == x || building.y == y);
        }

        //获得核心提升
        public boolean coreAugment(){
            return (hasCoreMerge() && completelyLinkedCore() != null) || bossAugment;
        }

        @Override
        public void updateTile(){
            if(completelyLinkedCore() != null && side >= 0){
                ((BaseCoreBlock.BaseCoreBuild)completelyLinkedCore()).coreAugment[Mathf.mod(side + 2, 4)] = this;
            }
        }
    }
}
