package Ovulam.world.block.defense.LaserDefense;

import arc.math.geom.Rect;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.world.Block;
import mindustry.world.Tile;

public class SpaceTower extends Block {
    public AbsorbLasersBlock absorbLasersBlock;
    public AbsorbShield laserShield;
    public float range = 40;

    public SpaceTower(String name) {
        super(name);
        update = true;
        sync = true;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.placing, (x - range) + offset, (y - range) + offset, range * 2, range * 2);
    }

    public class SpaceTowerBuilding extends Building {
        public Seq<Building> buildings = new Seq<>();

        public arc.math.geom.Rect rect(float x, float y, float size) {
            return Rect.tmp.set(x - size / 2, y - size / 2, size, size);
        }

        @Override
        public void draw() {
            Rect rectRange = rect(this.x, this.y, range * 2);
            Drawf.dashRect(Pal.placing, rectRange);
            buildings.each(tile1 -> {
                Drawf.circles(tile1.x * 8, tile1.y * 8, 3);
            });
        }

        @Override
        public void updateTile() {
            Seq<Tile> tileSeq = new Seq<>();
            for (float i = -range; i < range; i = i + 8) {
                for (float j = -range; j < range; j = j + 8) {
                    Tile tile1 = Vars.world.tile((int) (x + i) / 8, (int) (y + j) / 8);
                    if(tile1 == null || !(tile1.block() == Blocks.air
                            || (tile1.build instanceof AbsorbLasersBlock.AbsorbLasersBuild))
                    )continue;

                    tileSeq.add(tile1);
                }
            }

            if(Vars.state.teams.get(team).getBuildings(laserShield).isEmpty()) return;
            Building building = Vars.state.teams.get(team).getBuildings(laserShield).first();

            tileSeq.each(tile1 -> {
                if(tile1.block() == Blocks.air && tile1.build == null)tile1.setBlock(absorbLasersBlock, team, 0);
                if(tile1.build instanceof AbsorbLasersBlock.AbsorbLasersBuild b){
                    b.target = this;
                    ((AbsorbShield.AbsorbShieldBuild)building).buildings.add(b);
                }
            });
        }

    }
}
