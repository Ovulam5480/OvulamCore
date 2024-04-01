package Ovulam.world.block.storage;

import arc.math.Mathf;

public class DeconstructorStorage extends BaseStorageBlock{
    public DeconstructorStorage(String name) {
        super(name);
    }

    public class DeconstructorStorageBuild extends BaseStorageBuild{

        @Override
        public void updateTile(){
            if(completelyLinkedCore() != null && side >= 0){
                ((BaseCoreBlock.BaseCoreBuild)completelyLinkedCore()).coreAugment[Mathf.mod(side + 2, 4)] = this;
            }
        }
    }
}
