package Ovulam.world.event.FlowEvents;

import Ovulam.UI.EventAnimation;
import Ovulam.modContent.OvulamEventAnimations;
import Ovulam.world.event.FlowEvent;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Items;
import mindustry.game.Teams;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.world.blocks.storage.CoreBlock;

//自组织爆发事件线
public class SelfOrganizingOutbreak extends FlowEvent {
    public float overdriveTime = 600f, overdriveTimer;
    public EventAnimation decompose = OvulamEventAnimations.selfOrganizationDecompose;
    public EventAnimation confusion = OvulamEventAnimations.selfOrganizationConfusion;

    public SelfOrganizingOutbreak() {
        startAnimation = OvulamEventAnimations.selfOrganization;
        endAnimation = OvulamEventAnimations.selfOrganizationCollapse;
        //持续三十分钟
        EventTime = 600f;
    }

    public void begin() {
        overdriveTimer = 0;
        super.begin();
    }

    //结局:坍缩
    public void end() {
        super.end();
    }

    public void update() {
        super.update();

        overdriveTime += delta();

        if (overdriveTimer > overdriveTime) {
            Teams.TeamData teamData = Vars.state.teams.get(Vars.state.rules.defaultTeam);
            Seq<Building> buildings = teamData.buildings.select(building -> building.block().category == Category.crafting);

            CoreBlock.CoreBuild core = teamData.core();
            int size = buildings.size;

            if (core != null && core.items.has(Items.phaseFabric, size)) {
                core.items.remove(Items.phaseFabric, size);
                buildings.each(building -> building.applyBoost(3, overdriveTime));
            }else {
                //结局:崩溃
                decompose.reset();
                getTrigger = false;
                return;
            }

            overdriveTimer -= overdriveTime;
        }
    }
}
