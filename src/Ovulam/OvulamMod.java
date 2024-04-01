package Ovulam;

import Ovulam.UI.StoryTable;
import Ovulam.mod.OvulamBlocks;
import Ovulam.mod.OvulamItems;
import Ovulam.mod.OvulamUnits;
import Ovulam.type.unit.InvitationUnitEntity;
import Ovulam.world.block.No9527.CT3PlanetDialog;
import Ovulam.world.graphics.OvulamShaders;
import arc.Events;
import arc.util.Log;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.mod.Mod;

public class OvulamMod extends Mod{
    public static String ovulamName(){
        return "ovulam";
    }

    public OvulamMod(){
        Log.info("5480 is eating.");
    }

    @Override
    public void init() {
        Vars.ui.planet = new CT3PlanetDialog();
    }

    @Override
    public void loadContent(){
        OvulamBlocks.load();
        OvulamItems.load();
        OvulamUnits.load();

        OvulamShaders.init();

        Events.on(InvitationUnitEntity.class, invitationUnitEntity -> new StoryTable(UnitTypes.mono.fullIcon,"AAAAAA"));

        //OvulamUI.init();
    }
}

