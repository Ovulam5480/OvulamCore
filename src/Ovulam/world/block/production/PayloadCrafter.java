package Ovulam.world.block.production;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ButtonGroup;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Structs;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.ItemStack;
import mindustry.type.Liquid;
import mindustry.type.LiquidStack;
import mindustry.ui.Bar;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;

import static mindustry.Vars.*;

//这玩意P用没有
public class PayloadCrafter extends PayloadBlock {

    public int[] capacities = {};
    public Seq<PayloadPlan> plans = new Seq<>(5);
    public DrawBlock drawer = new DrawDefault();


    public TextureRegion region, topRegion, iconRegion;

    //todo 详细页面
    public PayloadCrafter(String name){
        super(name);
        size = 3;
        update = true;
        hasPower = true;
        hasLiquids = true;
        hasItems = true;
        solid = true;
        itemCapacity = 100;
        liquidCapacity = 100f;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;

        config(Integer.class, (PayloadCrafterBuild tile, Integer i) -> {
            if(!configurable) return;

            if(tile.currentPlan == i) return;
            tile.currentPlan = i < 0 || i >= plans.size ? -1 : i;
            tile.progress = 0;
        });
        config(Block.class, (PayloadCrafterBuild tile, Block val) -> {
            if(!configurable) return;

            int next = plans.indexOf(p -> p.to == val);
            if(tile.currentPlan == next) return;
            tile.currentPlan = next;
            tile.progress = 0;
        });
        consume(new ConsumeItemDynamic((PayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).itemRequirements : ItemStack.empty));
        consume(new ConsumeLiquidsDynamic((PayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).liquidRequirements : LiquidStack.empty));
    }

    @Override
    public void load(){
        super.load();
        region = Core.atlas.find(name);
        topRegion = Core.atlas.find(name + "-top");
        iconRegion = Core.atlas.find(name + "-icon");

        drawer.load(this);
        plans.forEach((plan) -> {
            plan.icon = Core.atlas.find(name + "-" + plan.name);
        });
    }

    @Override
    public TextureRegion[] icons(){
        return new TextureRegion[]{iconRegion};
    }

    @Override
    public void init(){
        capacities = new int[content.items().size];
        for (PayloadPlan plan : plans){
            for (ItemStack stack : plan.itemRequirements){
                capacities[stack.item.id] = Math.max(capacities[stack.item.id], stack.amount * 2);
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
        }
        super.init();
    }

    @Override
    //正常
    public void setBars(){
        super.setBars();
        addBar("progress", (PayloadCrafterBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
        removeBar("liquid");
    }


    @Override
    public boolean outputsItems(){
        return false;
    }


    public static class PayloadPlan {
        public Block from;
        public Block to;
        public ItemStack[] itemRequirements;
        public LiquidStack[] liquidRequirements;
        public float time;
        public String name;
        public TextureRegion icon = new TextureRegion();

        public PayloadPlan(float time, String name, Block from, Block to, ItemStack[] itemRequirements, LiquidStack[] liquidRequirements){
            this.from = from;
            this.to = to;
            this.time = time;
            this.name = name;
            this.itemRequirements = itemRequirements;
            this.liquidRequirements = liquidRequirements;
        }

        PayloadPlan(){
        }
    }

    public class PayloadCrafterBuild extends PayloadBlockBuild<BuildPayload> {
        public int previousPlan = -1;
        public int currentPlan = -1;
        public float progress;

        @Override
        public boolean acceptItem(Building source, Item item){
            return currentPlan != -1 &&
                    items.get(item) < getMaximumAccepted(item) &&
                    Structs.contains(plans.get(currentPlan).itemRequirements, stack -> stack.item == item);
        }

        @Override
        public int getMaximumAccepted(Item item){
            return capacities[item.id];
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid){
            if(currentPlan == -1){
                return false;
            }
            for (LiquidStack stack : plans.get(currentPlan).liquidRequirements){
                if(stack.liquid == liquid){
                    return true;
                }
            }
            return false;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            //当前存在配方
            return currentPlan != -1
                    //自身不拥有载荷
                    && this.payload == null
                    //自身启用，或来源于自身？
                    && (this.enabled || source == this)
                    && relativeTo(source) != rotation
                    && payload instanceof BuildPayload
                    && ((BuildPayload) payload).block() == getCurrentPlan().from;
        }

        @Override
        public void display(Table table){
            super.display(table);
            if(team != player.team() || currentPlan == -1) return;

            table.row();
            table.table(t -> {
                t.left().defaults().left();
                if(getPlanFrom() != null){
                    t.add(new ReqImage(getPlanFrom().fullIcon, () -> payload != null)).size(iconMed).padRight(4);
                }

                t.label(() -> "[accent] -> []");
                t.image(getPlanTo().fullIcon).size(iconMed).padRight(4).row();
                t.label(() -> "配方 : " + getCurrentPlan().name).size(iconMed).padRight(4);

            }).pad(4).padLeft(0f).fillX().left();
        }

        //有问题
        /*
        @Override
        public void buildConfiguration(Table table){
            Seq<Block> to = Seq.with(plans).map(u -> u.to).filter(u -> !Vars.state.rules.isBanned(u));

            if(to.any()){
                ItemSelection.buildTable(PayloadCrafter.this, table,
                        to,
                        () -> currentPlan == -1 ? null : plans.get(currentPlan).to,
                        block -> configure(plans.indexOf(u -> u.to == block)),
                        selectionRows, selectionColumns);
            }else{
                table.table(Styles.black3, t -> t.add("@none").color(Color.lightGray));
            }
        }

         */

        @Override
        public void buildConfiguration(Table table){
            ButtonGroup<ImageButton> group = new ButtonGroup<>();
            group.setMinCheckCount(0);
            Table cont = new Table().top();
            cont.defaults().size(40);
            int rows = 5;

            Runnable rebuild = () -> {
                group.clear();
                cont.clearChildren();
                plans.forEach(plan -> {
                    ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, 32, () -> {
                        control.input.config.hideConfig();
                    }).tooltip(plan.name).group(group).get();
                    button.changed(() -> currentPlan = (button.isChecked() ? plans.indexOf(plan) : -1));
                    button.getStyle().imageUp = new TextureRegionDrawable(plan.icon);
                    button.update(() -> button.setChecked(currentPlan == plans.indexOf(plan)));
                });
            };

            rebuild.run();

            Table main = new Table().background(Styles.black6);

            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
            pane.setScrollingDisabled(true, false);

            pane.setScrollYForce(block.selectScroll);
            pane.update(() -> {
                block.selectScroll = pane.getScrollY();
            });

            pane.setOverscroll(false, false);
            main.add(pane).maxHeight(40 * rows);
            table.top().add(main);
        }

        public PayloadPlan getCurrentPlan(){
            return currentPlan == -1 ? null : plans.get(currentPlan);
        }

        @Override
        public Object config(){
            return currentPlan;
        }

        public boolean change(){
            return previousPlan != currentPlan;
        }

        public Block getPlanFrom(){
            return currentPlan == -1 ? null : getCurrentPlan().from;
        }

        public Block getPlanTo(){
            return currentPlan == -1 ? null : getCurrentPlan().to;
        }

        public float progress(){
            return currentPlan == -1 ? 0 : progress / plans.get(currentPlan).time;
        }

        @Override
        public boolean shouldConsume(){
            return currentPlan != -1 && enabled &&
                    (getCurrentPlan().from == null || (payload != null && payload.block() == getCurrentPlan().from));
        }

        public void continueAddBar(){
            if(currentPlan == -1){
                for (Liquid liquid : content.liquids()){
                    removeBar("liquid-" + liquid.name);
                }
                return;
            }
            if(change() && previousPlan != -1){
                for (LiquidStack liquid : plans.get(previousPlan).liquidRequirements){
                    removeBar("liquid-" + liquid.liquid.name);
                }
            }
            for (LiquidStack liquid : plans.get(currentPlan).liquidRequirements){
                addLiquidBar(liquid.liquid);
            }
        }

        @Override
        public void draw(){
            Draw.rect(region, x, y);
            drawPayload();
            drawer.draw(this);
            Draw.rect(topRegion, x, y);
        }


        @Override
        public void updateTile(){
            if(!configurable){
                currentPlan = -1;
            }

            if(currentPlan < 0 || currentPlan >= plans.size){
                payload = null;
                currentPlan = -1;
            }


            if(change()){
                //items.clear();应该没有这个必要
                payload = null;
                progress = 0;
            }

            continueAddBar();

            PayloadPlan plan = getCurrentPlan();

            if(plan == null){
                return;
            }

            //如果自身存在载荷，并且载荷与配方产出的载荷相同
            if(payload != null && payload.block() == plan.to){
                //移出载荷
                moveOutPayload();
            }
            //否则，如果自身不存在载荷，或者自身的配方不需要载荷
            else if((payload != null && moveInPayload()) || plan.from == null){
                progress += edelta();

                if(progress >= plan.time){
                    payload = new BuildPayload(plan.to, team);

                    payload.block().placeEffect.at(x, y, payload.size() / tilesize);
                    payVector.setZero();

                    progress %= 1f;
                    consume();

                }
            }
            previousPlan = currentPlan;
        }
    }
}
