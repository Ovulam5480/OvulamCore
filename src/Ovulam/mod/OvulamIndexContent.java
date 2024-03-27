package Ovulam.mod;

import mindustry.Vars;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;

public class OvulamIndexContent {
    public static Block CTBlock (String name){
        return Vars.content.block("creators-" + name);
    }
    public static Item CTItem (String name){
        return Vars.content.item("creators-" + name);
    }
    public static Liquid CTLiquid (String name){return Vars.content.liquid("creators-" + name);}

    //public UnitType[][] upgradesTree = new UnitType[7][]
    /*
    public HashMap<UnitType, UnitType> upgradesTreee = new HashMap<>(Map.of(
            UnitTypes.dagger, UnitTypes.mace,
            UnitTypes.mace, UnitTypes.fortress,
            UnitTypes.fortress, UnitTypes.scepter,
            UnitTypes.scepter, UnitTypes.reign,
            UnitTypes.nova, UnitTypes.reign));

     */

}
