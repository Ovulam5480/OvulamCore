package Ovulam.world.block.defense.LaserDefense;

import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

//激光盾,绝缘方块组成外壳,空间塔提供外壳存放空间
public class AbsorbShield extends Block {

    public AbsorbShield(String name) {
        super(name);
        update = true;
        sync = true;
    }


    public class AbsorbShieldBuild extends Building {
        public Seq<Building> buildings = new Seq<>();

        public float radius() {
            return buildings.size * 8 / (2 * Mathf.pi);
        }

        @Override
        public void hitbox(Rect out) {
            out.setCentered(this.x, this.y, (float) (this.block.size * 8), (float) (this.block.size * 8));
        }

        @Override
        public void updateTile() {
            buildings.each(building -> {
                int index = buildings.indexOf(building);
                float per = (float) index / buildings.size;
                building.set((float) (x + Math.cos(per * 2 * Mathf.pi) * radius()),
                        (float) (y + Math.sin(per * 2 * Mathf.pi) * radius()));
            });
            //buildings.clear();
        }

        @Override
        public void draw() {
            Drawf.circles(x, y, radius());
        }
    }
}