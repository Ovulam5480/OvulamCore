package Ovulam.world.block.defense.LaserDefense;

import arc.graphics.Color;
import arc.math.geom.Rect;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

// 绝缘方块,用于激光盾的外壳
public class AbsorbLasersBlock extends Block {
    public AbsorbLasersBlock(String name) {
        super(name);
        canOverdrive = false;
        absorbLasers = true;
        destructible = true;
        health = 100000;
    }


    public static class AbsorbLasersBuild extends Building{
        public SpaceTower.SpaceTowerBuilding target;

        public boolean collision(Bullet other) {
            return true;
        }

        public void hitbox(Rect out) {
            out.setCentered(this.x, this.y, (float)(this.block.size * 8), (float)(this.block.size * 8));
        }

        public void setXY(float x, float y){
            this.x = x;
            this.y = y;
        }

        @Override
        public void draw() {
            Drawf.circles(x, y, 4);
            Drawf.line(Color.cyan,x,y,target.x, target.y);
        }

        @Override
        public void updateTile() {
            if(target.buildOn() != target) remove();
        }
    }
}
