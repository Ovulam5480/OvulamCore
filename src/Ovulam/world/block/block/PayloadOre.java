package Ovulam.world.block.block;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemSeq;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class PayloadOre extends Block {
    public int oreAmount = 999;
    public TextureRegion region;
    public TextureRegion stoneRegion;
    public TextureRegion[] oreRegion = new TextureRegion[oreAmount];


    public PayloadOre(String name){
        super(name);
        hasItems = true;
        solid = true;
        destructible = true;
        allowResupply = true;
        separateItemCapacity = false;
        absorbLasers = true;
        itemCapacity = -1;
    }

    @Override
    public void init() {
        super.init();
        if(itemCapacity == -1){
            itemCapacity = 32 * Mathf.pow(size, 2);
        }
        //todo 物品石头
        //requirements = new ItemStack[]{new ItemStack(item, 72 * Mathf.pow(size, 3))};
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        stoneRegion = Core.atlas.find(name + "-ore");
        for (int i = 0; i < oreAmount; i++){
            oreRegion[i] = Core.atlas.find(name + "-ore-" + i);
        }
    }

    @Override
    public boolean outputsItems() {
        return false;
    }

    public class PayloadOreBuild extends Building {
        public boolean isFulled = false;
        public Item[] drawOre;
        public ItemSeq itemsWhenFull = new ItemSeq();
        public ItemSeq itemAmountWhenFull = new ItemSeq();

        @Override
        public boolean acceptItem(Building source, Item item){
            return false;
        }

        public void isFulled(){
            if(isFulled){
                return;
            }
            if(items.total() >= itemCapacity - Vars.content.items().size){
                isFulled = true;
            }
        }

        @Override
        public void draw(){
            isFulled();
            if(!isFulled){
                Draw.rect(region, x, y);
            } else if(drawOre == null){
                drawOre = new Item[oreAmount];
                itemsWhenFull.add(items);
                float hadOre = 0;
                for (Item item : Vars.content.items()){
                    float floatOreAmount = items.get(item) * oreAmount / (float) itemCapacity;
                    for (float j = hadOre; j < hadOre + floatOreAmount; j++){
                        drawOre[(int) j] = item;
                        itemAmountWhenFull.add(item, 1);
                    }
                    hadOre += floatOreAmount;
                }
            } else {
                Draw.rect(stoneRegion, x, y);
                ItemStack currentItemStack = new ItemStack();
                for (int i = 0; i < drawOre.length; i++){
                    Item item = drawOre[i];

                    if(currentItemStack.item != item){
                        currentItemStack = new ItemStack(item, 1);
                    } else {
                        currentItemStack.amount++;
                    }

                    float alpha = ((items.get(item) * itemAmountWhenFull.get(item) / (float) itemsWhenFull.get(item)) - itemAmountWhenFull.get(item) + currentItemStack.amount);
                    Draw.color(item.color);
                    Draw.alpha(alpha);
                    Draw.rect(oreRegion[i], x, y);
                    Draw.reset();
                }

            }


        }
    }
}
