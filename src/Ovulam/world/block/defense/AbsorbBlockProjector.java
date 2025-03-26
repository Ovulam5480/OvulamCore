package Ovulam.world.block.defense;

import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.TechTree;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.gen.Call;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.ConstructBlock;

import static mindustry.Vars.tilesize;
import static mindustry.Vars.world;

public class AbsorbBlockProjector extends Block {
    public float range = 64f;

    public AbsorbBlockProjector(String name) {
        super(name);
        update = true;
        size = 2;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("blockHealth", (AbsorbBlockProjectorBuilding e) -> new Bar(
                () -> e.absorbBuild.health + "",
                () -> Pal.accent,
                () -> e.absorbBuild.health / e.absorbBuild.maxHealth
        ));
    }

    public Rect rangeRect(float x, float y, float range) {
        return Tmp.r1.set(x - range, y - range, range * 2, range * 2);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * 8 + offset, y * 8 + offset, range));
    }

    public class AbsorbBlockProjectorBuilding extends Building{
        Building absorbBuild = AbsorbBlock.absorbBlock.newBuilding();

        @Override
        public void updateTile() {
            //for (int i = 0; i < )
            //Vars.content.blocks().find(b -> b.name == "游戏内方块的名称, 包括前面的模组名前缀");
            for (float tx = -range; tx < range; tx += 8) {
                for (float ty = -range; ty < range; ty += 8) {
                    Tile tile = world.tileWorld(x + tx, y + ty);

                    if(tile != null){
                        tile.setBlock(AbsorbBlock.absorbBlock);
                        tile.build = absorbBuild;
                    }
                }
            }

        }

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
        }
    }
}
