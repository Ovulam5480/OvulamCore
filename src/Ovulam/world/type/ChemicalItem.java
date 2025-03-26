package Ovulam.world.type;

import Ovulam.OvulamMod;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import mindustry.logic.LStatements;
import mindustry.type.Item;
import mindustry.world.meta.Stat;

public class ChemicalItem extends Item {
    public ItemState state = ItemState.solid;

    public ChemicalItem(String name, Color color) {
        super(name, color);
        hidden = true;
        alwaysUnlocked = true;
    }

    @Override
    public void loadIcon() {
        //TextureRegion region = Core.atlas.find(OvulamMod.modName() + state);
    }

    public enum ItemState{
        //固体, 粉末, 结晶
        solid, dust, crystal
    }
}
