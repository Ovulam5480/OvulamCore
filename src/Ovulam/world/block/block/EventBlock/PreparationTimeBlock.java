package Ovulam.world.block.block.EventBlock;

import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import mindustry.type.Sector;

import static mindustry.Vars.state;

public class PreparationTimeBlock extends EventBlock{
    public float time = 900f;
    public PreparationTimeBlock(String name) {
        super(name);
    }

    public class PreparationTimeBuild extends EventBlockBuild {
        public boolean pre = true;
        public float timer;

        @Override
        public void updateTile(){
            if(!pre)return;
            timer += delta();

            if(timer > time){
                pre = false;
                Events.fire(new PreparationFinish(state.getSector()));
            }
        }

        public void drawCamera(float cameraX, float cameraY, float tilesizeScl){
            if(!pre)return;

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
