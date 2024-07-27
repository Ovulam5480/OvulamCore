package Ovulam.world.block.eventBlock;

public class BossComingEventBlock extends EventBlock{
    public BossComingEventBlock(String name) {
        super(name);
    }

    public class BossComingEventBuild extends EventBlockBuild{
        @Override
        public void updateTile(){
        }

        @Override
        public void drawCamera(float cameraX, float cameraY, float tilesizeScl){
        }
    }
}
