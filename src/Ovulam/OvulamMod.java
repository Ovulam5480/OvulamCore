package Ovulam;

import Ovulam.UI.StoryTable;
import Ovulam.mod.OvulamBlocks;
import Ovulam.mod.OvulamItems;
import Ovulam.mod.OvulamUnits;
import Ovulam.type.unit.InvitationUnitEntity;
import Ovulam.world.graphics.OvulamShaders;
import arc.Events;
import mindustry.content.UnitTypes;
import mindustry.mod.Mod;

public class OvulamMod extends Mod{
    //RecipesManager recipesManager;
    public static String ovulamName(){
        return "ovulam";
    }

    public OvulamMod(){
    }

    @Override
    public void init() {
        super.init();
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

