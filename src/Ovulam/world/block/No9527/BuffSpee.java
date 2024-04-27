package Ovulam.world.block.No9527;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.logic.LAccess;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;

public class BuffSpee extends Block {//速度更改
    public static StatusEffect Buff = new StatusEffect("unitBuff") {{
        //show = false;c
    }};

    public BuffSpee(String name) {
        super(name);
        update = true;
        sync = true;
        canOverdrive = false;
        targetable = false;
        forceDark = true;
        privileged = true;
        size = 1;
        requirements(Category.defense, new ItemStack[]{});
    }

    @Override
    public boolean canBreak(Tile tile) {
        return !privileged || Vars.state.rules.editor || Vars.state.playtestingMap != null;
    }

    public class BuffSpeeBuild extends Building {
        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.config) Buff.speedMultiplier = (float) p1;
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void updateTile() {
            Vars.state.teams.present.select(teamData -> teamData.team != Vars.state.rules.defaultTeam).each(teamData -> teamData.units.each(unit -> unit.apply(Buff, 60)));
        }
    }
}
