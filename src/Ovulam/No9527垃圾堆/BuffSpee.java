package Ovulam.No9527垃圾堆;

import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.logic.LAccess;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

public class BuffSpee extends Block {//速度更改
    public StatusEffect Buff = new StatusEffect("unitBuff");

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
            if (type == LAccess.shootp) {
                //假设p1(就是unit)为0的时候清空资源, p2(就是shootp)输入队伍编号, 0是废墟, 1是黄队, 2是红队, 依此类推
                if(p1 == 0 && p2 >= 0){
                    CoreBlock.CoreBuild coreBuild = Vars.state.teams.get(Team.get((int) p2)).core();
                    if(coreBuild != null){
                        coreBuild.items.clear();
                    }
                }
            }

            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public void updateTile() {
            Vars.state.teams.present.select(teamData -> teamData.team != Vars.state.rules.defaultTeam).each(teamData -> teamData.units.each(unit -> unit.apply(Buff, 60)));
        }
    }
}
