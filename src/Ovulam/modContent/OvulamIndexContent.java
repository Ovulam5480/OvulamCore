package Ovulam.modContent;

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
}
