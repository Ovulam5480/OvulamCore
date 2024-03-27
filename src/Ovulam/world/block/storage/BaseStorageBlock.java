package Ovulam.world.block.storage;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.blocks.storage.StorageBlock;

public class BaseStorageBlock extends StorageBlock {
    public BaseStorageBlock(String name) {
        super(name);
        update = true;
        size = 3;
        itemCapacity = 1000;
    }

    @Override
    public void load(){
        super.load();
    }

    public class BaseStorageBuild extends StorageBuild{
        public int side = -1;
        public boolean remoteLink = false;

        //已经链接核心
        public boolean hasCoreMerge(){
            return proximity.find(building -> building.block instanceof CoreBlock) != null;
        }

        //获得核心提升
        public boolean coreAugment(){
            if(!hasCoreMerge()) return false;
            return completelyLinkedCore() != null;
        }

        //完全链接核心
        public Building completelyLinkedCore(){
            side = -1;
            if(remoteLink) return Vars.state.teams.closestCore(x, y, team);

            int trns = this.block.size / 2 + 1;
            for (int i = 0; i < 4; i++){
                Building building = nearby(Geometry.d4(i).x * trns, Geometry.d4(i).y * trns);
                if(building != null && (building.x == x || building.y == y) && building.block instanceof BaseCoreBlock){
                    side = i;
                    return building;
                }
            }
            return null;
        }

        @Override
        public void updateTile(){
            if(completelyLinkedCore() != null && side >= 0){
                ((BaseCoreBlock.BaseCoreBuild)completelyLinkedCore()).coreAugment[Mathf.mod(side + 2, 4)] = this;
            }
        }
    }
}
