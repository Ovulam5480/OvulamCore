package Ovulam.world.block.storage;

import arc.Core;
import arc.func.Floatc;
import arc.func.Floatp;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.Drawable;
import arc.scene.ui.layout.Table;
import arc.struct.FloatSeq;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.UnitType;
import mindustry.ui.Bar;
import mindustry.ui.fragments.PlacementFragment;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.units.UnitFactory;

public class FactoryCoreBlock extends BaseCoreBlock{
    public Seq<CoreRecipe> recipes = new Seq<>();
    public FactoryCoreBlock(String name) {
        super(name);
    }

    public class FactoryCoreBuild extends BaseCoreBuild{
        public float[] timers = new float[recipes.size];

        @Override
        public void display(Table table) {
            super.display(table);
            table.row();

            table.table(rst -> {
                recipes.each(r -> {
                    rst.table(rt-> {
                        Floatp progress = () -> timers[recipes.indexOf(r)] / r.time;

                        rt.image(r.icon.fullIcon).size(32).row();
                        rt.image(Core.atlas.white()).color(Pal.accent).size(32 * progress.get(), 8).update(image -> {
                            image.setWidth(32 * progress.get());
                        }).left();
                    }).margin(4);
                });
            }).left();

        }

        @Override
        public void updateTile() {
            super.updateTile();

            int i = 0;
            for (CoreRecipe r : recipes) {
                boolean accept = true;
                for (ItemStack stack : r.product) {
                    if(items.get(stack.item) + stack.amount > getMaximumAccepted(stack.item)){
                        accept = false;
                        break;
                    }
                }

                if(items.has(r.input) && accept){
                    float t = timers[i];
                    t += edelta();
                    if(t >= r.time){
                        t = 0;

                        items.remove(r.input);
                        items.add(r.product);
                    }
                    timers[i] = t;
                }
                i++;
            }
        }
    }

    public static class CoreRecipe{
        public ItemStack[] input;
        public Seq<ItemStack> product;
        public float time;
        public Item icon;

        public CoreRecipe(ItemStack[] input, Seq<ItemStack> product, float time, Item icon) {
            this.input = input;
            this.product = product;
            this.time = time;
            this.icon = icon;
        }
    }
}
