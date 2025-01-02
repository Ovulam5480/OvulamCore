package Ovulam.modContent;

import Ovulam.entities.Item.OvulamItem;
import mindustry.type.Item;

public class OvulamItems {
    public static Item copperer;

    public static void load() {
        copperer = new OvulamItem("copperer");

        /*
        int size = Vars.content.items().size;

        for (int i = 0; i < size; i++){
            Item item = Vars.content.item(i);
            OvulamItem ovulamItem = new OvulamItem(item.name, item.color){

            };
            if(!(item instanceof OvulamItem)) Vars.content.items().set(i, new OvulamItem(item.name, item.color));
        }

         */


    }
}
