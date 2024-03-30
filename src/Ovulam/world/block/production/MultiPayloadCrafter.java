package Ovulam.world.block.production;

import Ovulam.world.block.payload.MultiPayloadBlock;
import Ovulam.world.consumers.ConsumePositionPayloadsDynamic;
import Ovulam.world.consumers.ConsumePowerDynamicCanBeNegative;
import Ovulam.world.move.MoveCustomP9;
import Ovulam.world.move.MoveOut;
import Ovulam.world.move.MovePayload;
import Ovulam.world.move.MoveSize;
import Ovulam.world.other.PositionPayload;
import Ovulam.world.other.Recipe;
import Ovulam.world.other.RecipeMover;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.*;
import mindustry.ui.Bar;
import mindustry.ui.ItemImage;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;
import mindustry.world.blocks.payloads.BuildPayload;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.UnitPayload;
import mindustry.world.consumers.ConsumeItemDynamic;
import mindustry.world.consumers.ConsumeLiquidsDynamic;
import mindustry.world.draw.DrawBlock;
import mindustry.world.draw.DrawDefault;
import mindustry.world.meta.Stat;

import java.util.HashMap;

import static mindustry.Vars.content;
import static mindustry.Vars.control;

public class MultiPayloadCrafter extends MultiPayloadBlock {
    private final Seq<PayloadStack> emptyPayloadStacks = new Seq<>();
    public DrawBlock drawer = new DrawDefault();
    public Seq<MultiPayloadPlan> plans = new Seq<>(5);
    public boolean ignorePayloadFullness = false;
    public boolean changeClear;
    //行
    public int tableRows = 8;
    //列
    public int tableColumns = 8;

    public MovePayload moveInMover = new MoveCustomP9(16);
    public MovePayload moveCapital = new MoveSize();
    public MovePayload moveOutMover = new MoveOut();
    public float outMoverCapitalMulti = 2f;

    //todo 方块保存配置 与 地图保存配置
    public MultiPayloadCrafter(String name) {
        super(name);
        size = 3;
        update = true;
        hasPower = true;
        hasLiquids = true;
        hasItems = true;
        acceptsItems = true;
        acceptsPayload = true;
        solid = true;
        itemCapacity = 10;
        liquidCapacity = 100f;
        configurable = true;
        clearOnDoubleTap = true;
        outputsPayload = true;
        rotate = true;
        rotateDraw = false;
        //todo ???
        dumpFacing = true;

        consume(new ConsumeItemDynamic((MultiPayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).inputRecipe.itemStacks.toArray() : ItemStack.empty));

        //todo 完全消耗
        consume(new ConsumeLiquidsDynamic((MultiPayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).inputRecipe.liquidStacks.toArray() : LiquidStack.empty));

        consume(new ConsumePositionPayloadsDynamic((MultiPayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).inputRecipe.payloadStacks : emptyPayloadStacks));

        consume(new ConsumePowerDynamicCanBeNegative((MultiPayloadCrafterBuild e) -> e.currentPlan != -1 ?
                plans.get(Math.min(e.currentPlan, plans.size - 1)).inputRecipe.Power : 0));
    }

    @Override
    public void init() {
        for (MultiPayloadPlan plan : plans) {
            for (ItemStack stack : plan.inputRecipe.itemStacks) {
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
            for (LiquidStack stack : plan.inputRecipe.liquidStacks) {
                liquidCapacity = Math.max(liquidCapacity, stack.amount);
            }
        }
        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (MultiPayloadCrafterBuild e) ->
                new Bar("bar.progress", Pal.ammo, e::progress));
    }


    @Override
    public void load() {
        super.load();
        drawer.load(this);
        plans.forEach(multiPayloadPlan -> multiPayloadPlan.drawBlock.load(this));
        plans.forEach((plan) -> plan.icon = Core.atlas.find(name + "-" + plan.name));
    }

    @Override
    public void drawPlan(BuildPlan plan, Eachable<BuildPlan> list, boolean valid) {
        super.drawPlan(plan, list, valid);
        drawer.drawPlan(this, plan, list);
    }

    @Override
    public TextureRegion[] icons() {
        return drawer.icons(this);
    }

    ////////////////////////
    public static class MultiPayloadPlan {
        public float craftTime;
        public float warmup;
        public String name;
        public TextureRegion icon = new TextureRegion();
        public Recipe inputRecipe;
        public Recipe outputRecipe;
        public RecipeMover[] recipeMover;
        public DrawBlock drawBlock;

        public MultiPayloadPlan(float craftTime, float warmup, String name, Recipe inputRecipe, Recipe outputRecipe,
                                RecipeMover[] recipeMover, DrawBlock drawBlock) {
            this.craftTime = craftTime;
            this.warmup = warmup;
            this.name = name;
            this.inputRecipe = inputRecipe;
            this.outputRecipe = outputRecipe;
            this.recipeMover = recipeMover;
            this.drawBlock = drawBlock;
        }
    }

    /////////////////////////////////
    public class MultiPayloadCrafterBuild extends MultiPayloadBlockBuild {
        public int previousPlan = -1;
        public int currentPlan = -1;
        public int hoveredPlan = -1;
        public float progress;
        public Seq<PositionPayload> inputPositionPayloads = new Seq<>();
        public Seq<PositionPayload> craftPositionPayloads = new Seq<>();
        public Seq<PositionPayload> remover = new Seq<>();

        public HashMap<PositionPayload, Integer> outputPositionPayloads = new HashMap<>();

        public void buildConfiguration(Table table) {
            table.table(configTable -> {

                Table recipeShow = new Table(Styles.black3);
                recipeShow.defaults().size(48, 48).left();

                Table topCont = new Table();
                int col = Mathf.ceil((float) plans.size / tableColumns);

                Table cont = new Table(Styles.black9);
                cont.defaults().size(40);

                hoveredPlan = currentPlan;
                updateRecipeTable(recipeShow);

                Runnable rebuild = () -> {
                    for (int i = 0; i < plans.size; i++) {
                        int index = i;
                        MultiPayloadPlan plan = plans.get(i);

                        ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, 32, () ->
                                control.input.config.hideConfig()).tooltip(plan.name).get();

                        button.setChecked(currentPlan == index);
                        button.getStyle().imageUp = new TextureRegionDrawable(content.units().get(1).fullIcon);
                        button.changed(() -> currentPlan = (button.isChecked() ? index : -1));

                        button.hovered(() -> {
                            hoveredPlan = index;
                            updateRecipeTable(recipeShow);
                        });
                        if (i % tableRows == tableRows - 1) cont.row();
                    }
                    table.fill(table1 -> {
                        table1.top();
                        table1.setPosition(0, -40 * col);
                        table1.add(recipeShow);
                    });
                };
                rebuild.run();

                Table scrollPane = new Table();
                ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
                pane.setScrollingDisabled(false, false);
                pane.setScrollYForce(block.selectScroll);
                pane.update(() -> block.selectScroll = pane.getScrollY());
                pane.setOverscroll(false, true);
                scrollPane.add(pane).maxHeight(tableColumns * 40);
                topCont.add(scrollPane);

                table.add(topCont);
            });
        }

        private void updateRecipeTable(Table table) {
            table.clear();
            if (hoveredPlan == -1) return;
            table.margin(10);

            MultiPayloadPlan plan = plans.get(hoveredPlan);
            Recipe inputRecipe = plan.inputRecipe;

            table.add(Stat.input.localized()).height(48).row();

            for (int i = 0; i < inputRecipe.itemStacks.size; i++) {
                ItemStack itemStack = inputRecipe.itemStacks.get(i);
                ItemImage itemImage = new ItemImage(itemStack.item.uiIcon, itemStack.amount);

                table.add(itemImage).center();
                if (i % 4 == 3) table.row();
            }
            table.row();
            for (int i = 0; i < inputRecipe.liquidStacks.size; i++) {
                LiquidStack liquidStacks = inputRecipe.liquidStacks.get(i);
                table.add(new ReqImage(liquidStacks.liquid.uiIcon, () -> true)).center();
                if (i % 4 == 3) table.row();
            }

            table.row();
            for (int i = 0; i < inputRecipe.payloadStacks.size; i++) {
                PayloadStack payloadStack = inputRecipe.payloadStacks.get(i);
                table.add(new ReqImage(payloadStack.item.uiIcon, () -> true)).center();
                if (i % 4 == 3) table.row();
            }
            table.row();

            if (inputRecipe.Power > 0) {
                table.image(Icon.power.tint(Pal.accent)).size(32);
                table.add(String.valueOf(inputRecipe.Power)).size(32);
                table.row();
            }

            Recipe outputRecipe = plan.outputRecipe;
            table.add(Stat.output.localized()).height(48).row();

            for (int i = 0; i < outputRecipe.itemStacks.size; i++) {
                ItemStack itemStack = outputRecipe.itemStacks.get(i);
                ItemImage itemImage = new ItemImage(itemStack.item.uiIcon, itemStack.amount);

                table.add(itemImage).center();
                if (i % 4 == 3) table.row();
            }
            table.row();
            for (int i = 0; i < outputRecipe.liquidStacks.size; i++) {
                LiquidStack liquidStacks = outputRecipe.liquidStacks.get(i);
                table.add(new ReqImage(liquidStacks.liquid.uiIcon, () -> true)).center();
                if (i % 4 == 3) table.row();
            }

            table.row();
            for (int i = 0; i < outputRecipe.payloadStacks.size; i++) {
                PayloadStack payloadStack = outputRecipe.payloadStacks.get(i);
                table.add(new ReqImage(payloadStack.item.uiIcon, () -> true)).center();
                if (i % 4 == 3) table.row();
            }
            table.row();

            if (outputRecipe.Power > 0) {
                table.image(Icon.power.tint(Pal.accent)).size(32);
                table.add(String.valueOf(outputRecipe.Power)).size(32);
                table.row();
            }

            hoveredPlan = -1;
        }

        @Override
        public int getMaximumAccepted(Item item) {
            ItemStack itemStack1 = getInputItems().find(itemStack -> itemStack.item == item);
            return itemStack1 == null ? 0 : itemStack1.amount * 2;
        }


        @Override
        public float progress() {
            return currentPlan == -1 ? 0 : progress / getCurrentPlan().craftTime;
        }

        @Override
        public void draw() {
            drawer.draw(this);
            if (currentPlan != -1) getCurrentPlan().drawBlock.draw(this);

            Draw.z(Layer.blockOver);
            drawPayload();
        }

        //必须空间 和 混合空间
        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            //当前配方不需要输入载荷
            if (currentPlan == -1 || getInputPayloads().size == 0 || !getInputPayloads().contains(payloadStack ->
                    payloadStack.item == payload.content())) return false;

            //必须的空间
            //第一倍的载荷输入到moveIn,所以总是能够输入
            //以外的载荷输入到moveCapital
            int place = getPayloadsAmount(getInputPayloads(), payload) + moveCapital.maxCapital(block);

            for (PayloadStack payloadStack : getInputPayloads()) {
                place -= Math.max(getPayloadAmount(payloadStack.item) -
                        (payloadStack.item == payload.content() ? 0 : payloadStack.amount), 0);
            }
            return place > 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (currentPlan == -1) {
                return false;
            }
            for (LiquidStack stack : getInputLiquids()) {
                if (stack.liquid == liquid) return true;
            }
            return false;
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return currentPlan != -1 && items.get(item) < getMaximumAccepted(item);
        }


        public MultiPayloadPlan getCurrentPlan() {
            return currentPlan == -1 ? null : plans.get(currentPlan);
        }

        public Seq<ItemStack> getInputItems() {
            return getCurrentPlan().inputRecipe.itemStacks;
        }

        public Seq<LiquidStack> getInputLiquids() {
            return getCurrentPlan().inputRecipe.liquidStacks;
        }

        public Seq<PayloadStack> getInputPayloads() {
            return getCurrentPlan().inputRecipe.payloadStacks;
        }

        public Seq<ItemStack> getOutputItems() {
            return getCurrentPlan().outputRecipe.itemStacks;
        }

        public Seq<LiquidStack> getOutputLiquids() {
            return getCurrentPlan().outputRecipe.liquidStacks;
        }

        public Seq<PayloadStack> getOutputPayloads() {
            return getCurrentPlan().outputRecipe.payloadStacks;
        }

        public boolean change() {
            return currentPlan != previousPlan;
        }

        //只有液体的
        public void continueAddBar() {
            if (currentPlan == -1) {
                for (Liquid liquid : content.liquids()) removeBar("liquid-" + liquid.name);
                return;
            }
            Seq<LiquidStack> addLiquids = new Seq<>();
            Seq<LiquidStack> removeLiquids = new Seq<>();

            if (change() && previousPlan != -1) {
                removeLiquids.add(getLiquids(previousPlan));
            }
            addLiquids.add(getLiquids(currentPlan));

            removeLiquids.forEach(liquidStack -> removeBar("liquid-" + liquidStack.liquid.name));
            addLiquids.forEach(liquidStack -> addLiquidBar(liquidStack.liquid));
        }

        public Seq<LiquidStack> getLiquids(int targetPlan) {
            Seq<LiquidStack> liquids = new Seq<>();
            MultiPayloadPlan multiPayloadPlan = plans.get(targetPlan);
            if (multiPayloadPlan.inputRecipe.liquidStacks != null)
                liquids.add(multiPayloadPlan.inputRecipe.liquidStacks);
            if (multiPayloadPlan.outputRecipe.liquidStacks != null)
                liquids.add(multiPayloadPlan.outputRecipe.liquidStacks);
            return liquids;
        }

        @Override
        public void updateTile() {
            if (!configurable || currentPlan < 0 || currentPlan >= plans.size) currentPlan = -1;
            if (change()) {
                previousPlan = currentPlan;
                progress = 0;
                if (changeClear) {
                    positionPayloads.forEach(positionPayload -> {
                        Payload payload = positionPayload.payload;
                        Fx.breakBlock.at(payload.x(), payload.y(), payload.size() / 8f);
                    });
                    positionPayloads.clear();
                    outputPositionPayloads.clear();
                }
            }
            continueAddBar();

            if (currentPlan != -1) {
                MultiPayloadPlan plan = getCurrentPlan();
                if (efficiency > 0) {
                    progress += delta();
                    if (!getCurrentPlan().outputRecipe.liquidCompletely) {
                        getOutputLiquids().forEach(liquidStack ->
                                handleLiquid(this, liquidStack.liquid, delta() * liquidStack.amount));
                    }
                }

                if (progress >= plan.craftTime) {
                    craft();
                    Fx.placeBlock.at(x, y, size);
                }
            }

            setPositionPayloads();
            moveOutPayloads();
            moveInPayloads();
            dumpOutputs();
        }

        //输出载荷 的最小索引
        public int getMinimumIndex() {
            float amount = outputPositionPayloads.size();
            for (int i = 0; i < amount; i++) {
                if (!outputPositionPayloads.containsValue(i)) {
                    return i;
                }
            }
            return (int) amount;
        }

        //区分建筑内的载荷是输入(输入 和 缓存)还是输出
        public void setPositionPayloads() {
            inputPositionPayloads.clear();
            craftPositionPayloads.clear();
            //配方中不存在输入载荷时，所有载荷都应该输出

            if (currentPlan == -1 || getInputPayloads().size == 0) {
                positionPayloads.forEach(positionPayload ->
                        outputPositionPayloads.putIfAbsent(positionPayload, getMinimumIndex()));
            } else {
                /////////////////////
                //否则将所以非输入的载荷设为输出
                for (PositionPayload positionPayload : positionPayloads) {
                    //如果该 载荷 包含在 工作载荷(使用moveIn) 中
                    if (getInputPayloads().contains(payloadStack ->
                            positionPayload.content() == payloadStack.item)) {

                        //判断数量，如果工作载荷能容纳该载荷，则加入该载荷
                        int amount = craftPositionPayloads.count(positionPayload1 ->
                                positionPayload1.content() == positionPayload.content()
                        );
                        if (amount < getPayloadsAmount(getInputPayloads(), positionPayload.payload)) {
                            craftPositionPayloads.add(positionPayload);
                        }

                        inputPositionPayloads.add(positionPayload);
                        outputPositionPayloads.remove(positionPayload);
                    }
                }
                //非 输入载荷 都应该输出
                Seq<PositionPayload> output = positionPayloads.copy();
                output.removeAll(inputPositionPayloads);

                output.forEach(positionPayload -> outputPositionPayloads.putIfAbsent(positionPayload, getMinimumIndex()));

            }
        }

        public int sideAmount() {
            return (size - 1) / 2;
        }

        //输入的载荷优先填充moveIn，然后填充moveCapital
        //不以moveInMover的容量去判断moveIn是否填满，工作区容纳一次工作所需要的所以载荷
        public void moveInPayloads() {
            //输入判断，所有非输入的载荷都应该输出
            for (PositionPayload positionPayload : craftPositionPayloads) {

                int index = 0;
                MovePayload movePayload = findMovePayload(positionPayload.content());

                if (movePayload == null) {
                    movePayload = moveInMover;
                    index = craftPositionPayloads.indexOf(positionPayload);
                } else {
                    for (PositionPayload positionPayload1 : craftPositionPayloads) {
                        if (positionPayload1.content() == positionPayload.content()) {
                            if (positionPayload1 == positionPayload) break;
                            index++;

                        }
                    }
                }
                positionPayload.targetPosition = setTargetPosition(index, movePayload);
            }

            int place = 0;

            for (PositionPayload positionPayload : inputPositionPayloads) {
                int index = inputPositionPayloads.indexOf(positionPayload);
                //如果载荷属于工作区载荷
                if (!craftPositionPayloads.contains(positionPayload)) {
                    positionPayload.targetPosition = setTargetPosition(index - place, moveCapital);
                } else {
                    place++;
                }
                updatePayload(positionPayload);
            }
        }

        //输出载荷非线性,载荷和索引一对
        public void moveOutPayloads() {
            if (outputPositionPayloads.isEmpty()) return;
            outputPositionPayloads.forEach((positionPayload, integer) -> {
                //如果载荷的索引 大于 sideAmount()，并且输出载荷不包含 （索引 减去 sideAmount()）的值
                //该载荷“前方”有空位，改变索引移动到空位
                if (integer >= sideAmount() && !outputPositionPayloads.containsValue(integer - sideAmount())) {
                    integer -= sideAmount();
                }
                outputPositionPayloads.replace(positionPayload, integer);
                positionPayload.targetPosition = setTargetPosition(integer, moveOutMover);

                updatePayload(positionPayload);
            });

        }


        //todo 解除帧率限制？
        public void dumpOutputs() {
            if (currentPlan == -1) {
                dump(items.first());
            } else if (timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : getOutputItems()) {
                    dump(output.item);
                }
            }

            if (currentPlan == -1) {
                liquids.each((liquid, amount) -> dumpLiquid(liquid, 2f));
            } else {
                for (LiquidStack liquidStack : getOutputLiquids()) {
                    dumpLiquid(liquidStack.liquid, 2f);
                }
            }

            outputPositionPayloads.forEach((positionPayload, integer) -> {
                //如果载荷位于边缘 并且载荷已经到达目标位置，并且已经被输出
                if (integer < sideAmount() && hasArrived(positionPayload)) {
                    //移除该载荷
                    if (dumpPositionPayload(positionPayload)) {
                        remover.add(positionPayload);
                    } else if (nearBuildings(positionPayload, 1f).size == 0 && positionPayload.payload.dump()) {
                        //todo 单位输出时 并且 面前有建筑时，应当给堵上
                        remover.add(positionPayload);
                        positionPayloads.remove(positionPayload);
                    }
                }
            });
            remover.each(positionPayload -> outputPositionPayloads.remove(positionPayload));
            remover.clear();
        }

        @Override
        public boolean shouldConsume() {
            if (currentPlan == -1) return false;
            //如果生产后物品总量大于物品容量，返回否
            for (var output : getOutputItems()) {
                if (items.get(output.item) + output.amount > itemCapacity) {
                    return false;
                }
            }


            for (var output : getOutputLiquids()) {
                if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) {
                    return false;
                }
            }


            for (PositionPayload positionPayload : craftPositionPayloads) {
                if (!hasArrived(positionPayload)) {
                    return false;
                }
            }

            int amount = getOutputPayloads().sum(payloadStack -> payloadStack.amount);

            return outputPositionPayloads.size() + (ignorePayloadFullness ? 0 : amount) <=
                    moveOutMover.maxCapital(block) * outMoverCapitalMulti && enabled;

        }

        //todo 电力生产，消耗
        @Override
        public float getPowerProduction() {
            return getCurrentPlan().outputRecipe.Power * efficiency;
        }

        public void craft() {
            consume();

            for (var output : getOutputItems()) {
                this.handleStack(output.item, output.amount, this);
            }


            if (!getCurrentPlan().outputRecipe.liquidCompletely) {
                for (var output : getOutputLiquids()) {
                    this.handleLiquid(this, output.liquid, output.amount * getCurrentPlan().craftTime);
                }
            }

            for (var output : getOutputPayloads()) {
                //量子纠缠遗址
                for (int i = 0; i < output.amount; i++) {
                    Payload payload;
                    if (output.item instanceof Block b) {
                        payload = new BuildPayload(b, team);
                    } else if (output.item instanceof UnitType u) {
                        Unit unit = u.create(team);
                        unit.rotation = rotdeg();
                        payload = new UnitPayload(u.create(team));
                        Events.fire(new EventType.UnitCreateEvent(unit, this));
                    } else {
                        return;
                    }

                    //如果存在配方mover
                    MovePayload movePayload = findMovePayload(payload.content());
                    if (movePayload == null) {
                        handlePayload(this, payload);
                    } else
                        handlePositionPayload(new PositionPayload(payload, setTargetPosition(i, movePayload), Vec2.ZERO));

                }

            }

            progress %= 1f;
        }

        public MovePayload findMovePayload(UnlockableContent payload) {
            MovePayload recipeMover = null;
            for (RecipeMover moveInMover1 : getCurrentPlan().recipeMover) {
                if (moveInMover1.unlockableContent == payload) {
                    recipeMover = moveInMover1.movePayload;
                    break;
                }
            }
            return recipeMover;
        }

        public MovePayload getMoveInMover() {
            return moveInMover;
        }

        public MovePayload getMoveOutMover() {
            return moveOutMover;
        }

    }
}
