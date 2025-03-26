package Ovulam.modContent;

import Ovulam.world.type.ChemicalItem;
import arc.graphics.Color;
import mindustry.content.Items;
import mindustry.type.Item;

public class OvulamItems {
    public static Item stone, copperOre, leadOre, tinOre, bauxiteOre, quartz, salt,
    tin;

    public static void load() {
        stone = new Item("stone", Color.valueOf("54545b")){{
            cost = 2.5f;
        }};

        copperOre = new Item("copper-ore", Color.valueOf("b8705c")){{
            buildable = false;
        }};

        leadOre = new Item("lead-ore", Color.valueOf("706b7a")){{
            buildable = false;
        }};

        tinOre = new Item("tin-ore", Color.valueOf("4e4e75")){{
            buildable = false;
        }};

        tin = new Item("tin", Color.valueOf("6a6aa1"));

        quartz = new Item("quartz", Color.valueOf("999999"));

        salt = new Item("salt", Color.valueOf("dcc3c3"));

//        bauxiteOre = new Item("bauxite-ore"){{
//            buildable = false;
//        }};
    }
}
