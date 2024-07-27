package Ovulam.world.block.eventBlock;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.type.Sector;

import static mindustry.Vars.state;

public class PreparationTimeEventBlock extends EventBlock{
    public float time = 900f;
    public PreparationTimeEventBlock(String name) {
        super(name);
    }

    public class PreparationTimeEventBuild extends EventBlockBuild {
        public boolean finish = false;
        public float timer;

        @Override
        public void updateTile(){
            if(finish)return;
            timer += delta();

            if(timer > time){
                finish = true;
                Events.fire(new PreparationFinish(state.getSector()));
            }
        }

        @Override
        public void drawCamera(float cameraX, float cameraY, float tilesizeScl){
            if(finish)return;

            float progress = timer / time;
            Lines.stroke(8f * tilesizeScl);
            Draw.color(Color.pink);
            Draw.alpha(0.5f);
            Lines.arc(cameraX, cameraY, 32f * tilesizeScl, progress, 90f);
            Lines.stroke(1f);
        }
    }

    public static class PreparationFinish{
        Sector sector;
        public PreparationFinish(Sector sector){
            this.sector = sector;
        }
    }
}
