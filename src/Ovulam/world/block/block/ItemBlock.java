package Ovulam.world.block.block;

import Ovulam.OvulamMod;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import mindustry.gen.Building;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class ItemBlock extends Block {
    public Item item;
    public TextureRegion itemRegion;

    public ItemBlock(Item item, int blockSize){
        super("ItemBlock-" + item.name + "-" + blockSize);
        this.item = item;
        this.size = blockSize;
        health = 2000;
        solid = true;
        sync = true;
        destructible = true;
    }

    //以64个物品为一格的“块”, 总物品为“块”的立方, 1:64, 2:512, 3:1728
    //以72个物品为一格的“块”, 总物品为“块”的立方, 1:72, 2:576, 3:1944, 选用这个
    //1:72  8:576  27:1944  64:4608  125:9000  216:15552

    @Override
    public void init(){
        //pixmap真是太他🐎的离谱了哎!!!
        this.requirements = new ItemStack[]{new ItemStack(item, 72 * Mathf.pow(size, 3))};
        super.init();
    }

    @Override
    public void load() {
        super.load();
        itemRegion = item.fullIcon;
        region = Core.atlas.find(OvulamMod.OvulamModName() + "ItemBlock-" + size);
    }

    public Color itemColor(){
        Color color = item.color.cpy();
        return color.fromHsv(color.hue(),color.saturation() * 1.5f,color.value() * 1.3f);
    }

    //@Override
    //public TextureRegion[] icons() {
    //    return new TextureRegion[]{itemRegion, region};
    //}


    public class ItemBlockBuild extends Building {

        @Override
        public void draw() {
            Draw.color(itemColor());
            Draw.rect(region, x, y);
            Draw.reset();
        }


        @Override
        public void damage(float damage){
            damage = Math.max((1 + Mathf.pow(maxHealth, -1 / (1 + damage))) * maxHealth / 10f, damage);
            super.damage(damage);
        }
    }
}
