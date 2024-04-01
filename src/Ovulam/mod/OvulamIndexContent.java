package Ovulam.mod;

import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.type.UnitType;
import mindustry.world.Block;

public class OvulamIndexContent {
    public static Block CTBlock (String name){
        return Vars.content.block("creators-" + name);
    }
    public static Item CTItem (String name){
        return Vars.content.item("creators-" + name);
    }
    public static Liquid CTLiquid (String name){return Vars.content.liquid("creators-" + name);}

    public static Seq<UnitType> daggerUpgradesTree = Seq.with(UnitTypes.dagger, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign);
    public static Seq<UnitType> novaUpgradesTree = Seq.with(UnitTypes.nova, UnitTypes.pulsar, UnitTypes.quasar, UnitTypes.vela, UnitTypes.corvus);
    public static Seq<UnitType> crawlerUpgradesTree = Seq.with(UnitTypes.crawler, UnitTypes.mace, UnitTypes.fortress, UnitTypes.scepter, UnitTypes.reign);
    public static Seq<UnitType> flareUpgradesTree = Seq.with(UnitTypes.flare, UnitTypes.horizon, UnitTypes.zenith, UnitTypes.anthicus, UnitTypes.eclipse);
    public static Seq<UnitType> monoUpgradesTree = Seq.with(UnitTypes.mono, UnitTypes.poly, UnitTypes.mega, UnitTypes.quad, UnitTypes.oct);
    public static Seq<UnitType> rissoUpgradesTree = Seq.with(UnitTypes.risso, UnitTypes.minke, UnitTypes.bryde, UnitTypes.sei, UnitTypes.omura);
    public static Seq<UnitType> retusaUpgradesTree = Seq.with(UnitTypes.retusa, UnitTypes.oxynoe, UnitTypes.cyerce, UnitTypes.aegires, UnitTypes.navanax);

    public static Seq[] unitUpgradesTree = new Seq[]{daggerUpgradesTree,novaUpgradesTree,crawlerUpgradesTree,flareUpgradesTree,monoUpgradesTree,rissoUpgradesTree,retusaUpgradesTree};
}
