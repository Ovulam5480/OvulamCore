package Ovulam.world.event;

//自组织爆发事件线
/*
public class SelfOrganizingOutbreak extends OvulamEvent {
    public float overdriveTime = 600f, overdriveTimer;
    public EventAnimation decompose = OvulamEventAnimations.selfOrganizationDecompose;
    public EventAnimation confusion = OvulamEventAnimations.selfOrganizationConfusion;

    public SelfOrganizingOutbreak() {
        startAnimation = OvulamEventAnimations.selfOrganization;
        endAnimation = OvulamEventAnimations.selfOrganizationCollapse;
        //持续三十分钟
        completeTime = 600f;
    }

    public void begin() {
        overdriveTimer = 0;
        super.begin();
    }

    //结局:坍缩
    @Override
    public void finish() {
        super.finish();
    }

    @Override
    public void duration() {
        if (overdriveTimer > overdriveTime) {
            Teams.TeamData teamData = Vars.state.teams.get(Vars.state.rules.defaultTeam);
            Seq<Building> buildings = teamData.buildings.select(building -> building.block().category == Category.crafting);

            CoreBlock.CoreBuild treeRoot = teamData.treeRoot();
            int size = buildings.size;

            */
/*
            if (treeRoot != null && treeRoot.items.has(Items.phaseFabric, size)) {
                treeRoot.items.remove(Items.phaseFabric, size);
                buildings.each(building -> building.applyBoost(3, overdriveTime));
            } else {
                //结局:崩溃
                decompose.reset();
                 = false;
                return;
            }

             *//*


            overdriveTimer -= overdriveTime;
        }
    }
}
*/
