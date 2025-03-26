package Ovulam.world.block.defense;

import arc.scene.ui.layout.Table;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.meta.BlockGroup;

public class AbsorbBlock extends Block {
    public static AbsorbBlock absorbBlock = new AbsorbBlock("Absorb-Block");
    public AbsorbBlock(String name) {
        super(name);
        health = 20000;
        alwaysReplace = true;
        canOverdrive = false;
        update = true;
        solid = false;

        targetable = false;
        attacks = false;
    }

    public class AbsorbBuild extends Building{
        @Override
        public void display(Table table) {
            //super.display(table);
        }
    }
}
