package Ovulam.world.block.production;

import Ovulam.UI.RecipeTable;
import Ovulam.entities.OvulamFx;
import Ovulam.world.block.payload.MultiPayloadBlock;
import Ovulam.world.consumers.ConsumeLiquidsDynamicCompletely;
import Ovulam.world.consumers.ConsumePositionPayloadsDynamic;
import Ovulam.world.consumers.ConsumePowerDynamicCanBeNegative;
import Ovulam.world.move.MoveOut;
import Ovulam.world.move.MovePayload;
import Ovulam.world.move.MoveSize;
import Ovulam.world.type.PositionPayload;
import Ovulam.world.type.Recipe;
import Ovulam.world.type.RecipePayloadManager;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.entities.Effect;
import mindustry.entities.units.BuildPlan;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.logic.LAccess;
import mindustry.type.*;
import mindustry.ui.Bar;
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

import static mindustry.Vars.content;
import static mindustry.Vars.control;

//能够消耗, 产出载荷和其他资源的多合成工厂
public class MultiPayloadCrafter extends MultiPayloadBlock {
    public Seq<MultiPayloadPlan> plans = new Seq<>(5);
    public DrawBlock drawer = new DrawDefault();
    //改变配方清除自身携带的载荷
    public boolean changeClear;
    //行
    public int tableRows = 8;
    //列
    public int tableColumns = 8;

    public Effect crafterDestroyEffect = OvulamFx.destroyTitanBlock;

    public Effect craftEffect = Fx.placeBlock;

    //待加工区
    public MovePayload moveCapital = new MoveSize();
    //输出区
    public MovePayload moveOutMover = new MoveOut();

    public float outMoverCapacityMulti = 2f;

    private final PayloadStack[] emptyPayloadStacks = {};

    //todo 方块保存配置 与 地图保存配置
    //todo 只要一个配方的方块不需要设置配方
    public MultiPayloadCrafter(String name) {
        super(name);
        destroyEffect = Fx.none;

        configurable = true;
        clearOnDoubleTap = true;

        //todo ???
        dumpFacing = true;
        consumeBuilder.clear();

        consume(new ConsumeItemDynamic((MultiPayloadCrafterBuild e) ->
                e.currentPlan != -1 ? e.getInputItems().toArray(ItemStack.class) : ItemStack.empty));

        consume(new ConsumeLiquidsDynamic((MultiPayloadCrafterBuild e) ->
                e.currentPlan != -1 && !e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));

        consume(new ConsumeLiquidsDynamicCompletely((MultiPayloadCrafterBuild e) ->
                e.currentPlan != -1 && e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));

        consume(new ConsumePositionPayloadsDynamic((MultiPayloadCrafterBuild e) ->
                e.currentPlan != -1 ? e.getInputPayloads().toArray(PayloadStack.class) : emptyPayloadStacks));

        consume(new ConsumePowerDynamicCanBeNegative((MultiPayloadCrafterBuild e) ->
                e.currentPlan != -1 ? e.getInputPower() : 0));
    }

    @Override
    public void init() {
        for (MultiPayloadPlan plan : plans) {
            for (ItemStack stack : plan.inputRecipe.itemStacks) {
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
            for (LiquidStack stack : plan.inputRecipe.liquidStacks) {
                liquidCapacity = Math.max(liquidCapacity, stack.amount * 2f * (plan.inputRecipe.liquidCompletely ? 1 : plan.craftTime));
            }
            plan.inputRecipe.payloadManagers.each(pm -> pm.init(this));
            plan.outputRecipe.payloadManagers.each(pm -> pm.init(this));

            if(!plan.outputRecipe.liquidStacks.isEmpty())outputsLiquid = true;
            if(!plan.outputRecipe.payloadStacks().isEmpty())outputsPayload = true;
            if(plan.outputRecipe.power > 0)outputsPower = true;
            if(plan.inputRecipe.power > 0)consumesPower = true;
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
        plans.each((plan) -> {
            plan.inputRecipe.payloadManagers.each(rpm -> rpm.drawRecipePayload.load(this));
            plan.outputRecipe.payloadManagers.each(rpm -> rpm.drawRecipePayload.load(this));
            plan.icon = Core.atlas.find(name + "-" + plan.name);
        });
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.output, table -> {
            table.row();
            for(int i = 0; i < plans.size; i++){
                MultiPayloadPlan plan = plans.get(i);

                table.table(plansTable -> {
                    plansTable.setBackground(Tex.buttonOver);

                    plansTable.add(plan.name).center().row();

                    plansTable.table(input -> {
                        input.setBackground(Tex.buttonOver);
                        input.add(Stat.input.localized()).fontScale(1.2f).row();
                        input.table(stageTableTable -> RecipeTable.addRecipeTable(stageTableTable, plan.inputRecipe));
                    }).pad(10).center().row();

                    plansTable.image(Icon.tree).center().row();

                    plansTable.table(output -> {
                        output.setBackground(Tex.buttonOver);
                        output.add(Stat.output.localized()).fontScale(1.2f).row();
                        output.table(stageTableTable -> RecipeTable.addRecipeTable(stageTableTable, plan.outputRecipe));
                    }).pad(10).center();

                }).growX().pad(10).top();

                //table.row();
            }
        });
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
        public float warmupSpeed;
        public String name;
        public TextureRegion icon = new TextureRegion();
        public Recipe inputRecipe;
        public Recipe outputRecipe;

        public MultiPayloadPlan(float craftTime, float warmupSpeed, String name,
                                Recipe inputRecipe, Recipe outputRecipe) {
            this.craftTime = craftTime;
            this.warmupSpeed = warmupSpeed;
            this.name = name;

            this.inputRecipe = inputRecipe;
            this.outputRecipe = outputRecipe;
        }

        public MultiPayloadPlan(float craftTime, float warmupSpeed, String name,
                                Object[] inputItems, Object[] inputLiquids, Object[] inputPayloads,
                                float inputPower, boolean inputLiquidCompletely,
                                Object[] outputItems, Object[] outputLiquids, Object[] outputPayloads,
                                float outputPower, boolean outputLiquidCompletely) {
            this.craftTime = craftTime;
            this.warmupSpeed = warmupSpeed;
            this.name = name;

            this.inputRecipe = new Recipe(ItemStack.list(inputItems), LiquidStack.list(inputLiquids),
                    RecipePayloadManager.list(inputPayloads), inputPower, inputLiquidCompletely);
            this.outputRecipe = new Recipe(ItemStack.list(outputItems), LiquidStack.list(outputLiquids),
                    RecipePayloadManager.list(outputPayloads), outputPower, outputLiquidCompletely);
        }

    }

    /////////////////////////////////
    public class MultiPayloadCrafterBuild extends MultiPayloadBlockBuild {
        public int previousPlan = -1;
        public int currentPlan = -1;
        public int hoveredPlan = -1;
        //输入到载荷内作为材料的载荷
        public Seq<PositionPayload> inputPayloads = new Seq<>();
        //作为材料的载荷当中, 用于当前工作的载荷
        public Seq<PositionPayload> craftPayloads = new Seq<>();
        //输出的产物载荷
        public ObjectMap<PositionPayload, Integer> outputPayloads = new ObjectMap<>();
        public float inputPayloadsAlpha = 1f;
        public float craftPayloadsAlpha = 1f;
        public float outputPayloadsAlpha = 1f;

        public Seq<PositionPayload> remover = new Seq<>();

        public float progress;
        public float totalProgress;
        public float warmup;

        @Override
        public float progress() {
            return currentPlan == -1 ? 0 : progress / getCurrentPlan().craftTime;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        //todo 载荷透明度
        public void buildConfiguration(Table table) {
            table.table(configTable -> {
                Table recipeShow = new Table(Styles.black3);

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
                        //todo icons
                        button.getStyle().imageUp = new TextureRegionDrawable(content.units().get(1).fullIcon);
                        button.changed(() -> currentPlan = (button.isChecked() ? index : -1));

                        button.hovered(() -> {
                            hoveredPlan = index;
                            updateRecipeTable(recipeShow);
                        });
                        if (i % tableRows == tableRows - 1) cont.row();
                    }
                };
                rebuild.run();

                table.fill(recipeTable -> {
                    recipeTable.top();
                    recipeTable.setPosition(0, -40 * col);
                    recipeTable.add(recipeShow);
                });

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

        //配方表
        private void updateRecipeTable(Table table) {
            table.clear();
            if (hoveredPlan == -1) return;

            MultiPayloadPlan plan = plans.get(hoveredPlan);

            Recipe inputRecipe = plan.inputRecipe;
            table.label(Stat.input::localized).height(36).row();
            RecipeTable.addRecipeTable(table, inputRecipe);

            table.row();

            Recipe outputRecipe = plan.outputRecipe;
            table.label(Stat.output::localized).height(36).row();
            RecipeTable.addRecipeTable(table, outputRecipe);

            hoveredPlan = -1;
        }

        @Override
        public void onDestroyed() {
            crafterDestroyEffect.at(x, y, rotation, block);
            super.onDestroyed();
        }

        @Override
        public void control(LAccess type, double p1, double p2, double p3, double p4) {
            if (type == LAccess.config) currentPlan = (int) p1;
            super.control(type, p1, p2, p3, p4);
        }

        @Override
        public int getMaximumAccepted(Item item) {
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack == null ? 0 : stack.amount * 2;
        }


        public void drawTargetPosition(){
            positionPayloads.each(positionPayload -> Drawf.dashCircle(x + positionPayload.targetPosition.x,
                    y + positionPayload.targetPosition.y, 3, Pal.accent));
        }
        public void drawInputTargetPosition(){
            inputPayloads.each(positionPayload -> Drawf.dashCircle(x + positionPayload.targetPosition.x,
                    y + positionPayload.targetPosition.y, 3, Color.cyan));
        }
        public void drawOutputTargetPosition(){
            for (PositionPayload positionPayload : outputPayloads.keys()){
                Drawf.dashCircle(x + positionPayload.targetPosition.x,
                        y + positionPayload.targetPosition.y, 3, Color.orange);
            }
        }
        public void drawOutputLine(){
            for (PositionPayload positionPayload : outputPayloads.keys()){
                Drawf.line(Color.pink,
                        positionPayload.x(this), positionPayload.y(this),
                        positionPayload.x(this), positionPayload.y(this));
            }
        }

        //region  block
        //sideRegion blockOver - 1
        //payloads blockOver
        //workRegion blockOver + 1
        //topRegion blockBuilding - 1
        //todo supportRegion blockBuilding - 1, topRegion flyingUnit + 1

        //todo 载荷透明度, 可能会用到着色器?
        @Override
        public void draw() {
            drawer.draw(this);

            //总是渲染输出载荷
            for (PositionPayload positionPayload : outputPayloads.keys()){
                positionPayload.payload.draw();
            }

            if(efficiency > 0){
                //工厂加工时, 只渲染非加工区的载荷, 否则渲染全部的输入载荷
                inputPayloads.each(positionPayload -> {
                    if(!craftPayloads.contains(positionPayload)) positionPayload.payload.draw();
                });
                //渲染加工效果
                getCurrentPlan().inputRecipe.payloadManagers.each(recipePayloadManager -> recipePayloadManager.drawInput(this));
                getCurrentPlan().outputRecipe.payloadManagers.each(recipePayloadManager -> recipePayloadManager.drawOutput(this));
            }else inputPayloads.each(positionPayload -> positionPayload.payload.draw());

            //drawInputTargetPosition();
            //drawOutputTargetPosition();
            //drawOutputLine();
        }

        //必须空间 和 混合空间
        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            //当前配方不需要载荷, 或者不需要该载荷
            if (currentPlan == -1 || getInputPayloads().size == 0 || !getInputPayloads().contains(payloadStack ->
                    payloadStack.item == payload.content())) return false;

            //必须的空间
            //第一倍的载荷输入到moveIn,所以总是能够输入
            //以外的载荷输入到moveCapital
            int place = getMoverLimit(getInputPayloads(), payload) + moveCapital.maxCapacity(block);

            for (PayloadStack payloadStack : getInputPayloads()) {
                place -= Math.max(getPayloadAmount(payloadStack.item) -
                        (payloadStack.item == payload.content() ? 0 : payloadStack.amount), 0);
            }
            return place > 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if(currentPlan == -1) return false;
            return getInputLiquids().contains(liquidStack -> liquidStack.liquid == liquid);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if(currentPlan == -1) return false;
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack != null && items.get(stack.item) < getMaximumAccepted(item);
        }

        public Seq<MultiPayloadPlan> getPlans(){
            return plans;
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

        public boolean getInputLiquidsCompletely() {
            return getCurrentPlan().inputRecipe.liquidCompletely;
        }

        public Seq<PayloadStack> getInputPayloads() {
            return getCurrentPlan().inputRecipe.payloadStacks();
        }

        public float getInputPower() {
            return getCurrentPlan().inputRecipe.power;
        }

        public ObjectMap<UnlockableContent, MovePayload> getInputMover() {
            ObjectMap<UnlockableContent, MovePayload> map = new ObjectMap<>();
            getCurrentPlan().inputRecipe.payloadManagers.each(manager -> map.put(manager.content(), manager.movePayload));
            return map;
        }

        public float getCraftTime() {
            return getCurrentPlan().craftTime;
        }

        public Seq<ItemStack> getOutputItems() {
            return getCurrentPlan().outputRecipe.itemStacks;
        }

        public Seq<LiquidStack> getOutputLiquids() {
            return getCurrentPlan().outputRecipe.liquidStacks;
        }

        public Seq<PayloadStack> getOutputPayloads() {
            return getCurrentPlan().outputRecipe.payloadStacks();
        }

        public float getOutputPower() {
            return getCurrentPlan().outputRecipe.power;
        }

        public ObjectMap<UnlockableContent, MovePayload> getOutputMover() {
            ObjectMap<UnlockableContent, MovePayload> map = new ObjectMap<>();
            getCurrentPlan().outputRecipe.payloadManagers.each(manager -> map.put(manager.content(), manager.movePayload));
            return map;
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

            removeLiquids.each(liquidStack -> removeBar("liquid-" + liquidStack.liquid.name));
            addLiquids.each(liquidStack -> addLiquidBar(liquidStack.liquid));
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
                    positionPayloads.each(positionPayload -> {
                        Payload payload = positionPayload.payload;
                        Fx.breakBlock.at(payload.x(), payload.y(), payload.size() / 8f);
                    });
                    positionPayloads.clear();
                    outputPayloads.clear();
                }
            }
            continueAddBar();

            if (currentPlan != -1) {
                warmup = Mathf.approachDelta(warmup, efficiency, getCurrentPlan().warmupSpeed * delta());
                progress += warmup * edelta();
                totalProgress += warmup * edelta();

                if (!getCurrentPlan().outputRecipe.liquidCompletely) {
                    getOutputLiquids().each(liquidStack ->
                            handleLiquid(this, liquidStack.liquid, edelta() * liquidStack.amount));
                }

                if (progress >= getCurrentPlan().craftTime) {
                    craft();
                    craftEffect.at(x, y, rotation, this);
                }
            }

            checkPositionPayloads();
            moveOutPayloads();
            moveInPayloads();
            dumpOutputs();
        }

        //输出载荷 的最小索引
        public int getMinimumIndex() {
            int amount = outputPayloads.size;
            for (int i = 0; i < amount; i++) {
                if (!outputPayloads.containsValue(i, true)) {
                    return i;
                }
            }
            return amount;
        }

        public int getOutMoverCapacity(){
            return (int) (moveOutMover.maxCapacity(block) * outMoverCapacityMulti);
        }

        //区分建筑内的载荷是输入(输入 和 缓存)还是输出
        public void checkPositionPayloads() {
            inputPayloads.clear();
            craftPayloads.clear();
            //配方中不存在输入载荷时，所有载荷都应该输出

            if (currentPlan == -1 || getInputPayloads().size == 0) {
                positionPayloads.each(positionPayload ->
                        outputPayloads.put(positionPayload, getMinimumIndex()));
            } else {
                /////////////////////
                //否则将所以非输入的载荷设为输出
                for (PositionPayload positionPayload : positionPayloads) {
                    //如果该 载荷 包含在 加工载荷(使用moveIn) 中
                    if (getInputPayloads().contains(payloadStack ->
                            positionPayload.content() == payloadStack.item)) {

                        //判断数量，如果加工载荷能容纳该载荷，则加入该载荷
                        int amount = craftPayloads.count(positionPayload1 ->
                                positionPayload1.content() == positionPayload.content()
                        );
                        if (amount < getMoverLimit(getInputPayloads(), positionPayload.payload)) {
                            craftPayloads.add(positionPayload);
                        }

                        inputPayloads.add(positionPayload);
                        outputPayloads.remove(positionPayload);
                    }
                }
                //非 输入载荷 都应该输出
                Seq<PositionPayload> output = positionPayloads.copy();
                output.removeAll(inputPayloads);

                output.each(positionPayload -> outputPayloads.put(positionPayload, getMinimumIndex()));

            }
        }

        public int sideAmount() {
            return (size - 1) / 2;
        }

        //输入的载荷优先填充moveIn，然后填充moveCapital
        //不以moveInMover的容量去判断moveIn是否填满，加工区容纳一次加工所需要的所以载荷
        public void moveInPayloads() {
            //输入判断，所有非输入的载荷都应该输出
            for (PositionPayload positionPayload : craftPayloads) {

                int index = 0;
                MovePayload movePayload = getInputMover().get(positionPayload.content());

                if (movePayload == null) {
                    movePayload = moveInMover;
                    index = craftPayloads.indexOf(positionPayload);
                } else {
                    for (PositionPayload positionPayload1 : craftPayloads) {
                        if (positionPayload1.content() == positionPayload.content()) {
                            if (positionPayload1 == positionPayload) break;
                            index++;

                        }
                    }
                }
                positionPayload.targetPosition = setTargetPosition(index, movePayload);
            }

            int place = 0;

            for (PositionPayload positionPayload : inputPayloads) {
                int index = inputPayloads.indexOf(positionPayload);
                //如果载荷属于加工区载荷
                if (!craftPayloads.contains(positionPayload)) {
                    positionPayload.targetPosition = setTargetPosition(index - place, moveCapital);
                } else {
                    place++;
                }
                updatePayload(positionPayload);
            }
        }

        //输出载荷
        public void moveOutPayloads() {
            outputPayloads.each((positionPayload, integer) -> {
                //如果载荷的索引 大于 sideAmount()，并且输出载荷不包含 （索引 减去 sideAmount()）的值
                //该载荷“前方”有空位，改变索引移动到空位
                if (integer >= sideAmount() && !outputPayloads.containsValue(integer - sideAmount(), true)) {
                    integer -= sideAmount();
                }

                //如果 输出区存在空间, 索引改为空间的索引
                if(integer >= getOutMoverCapacity())integer = Math.min(integer, getMinimumIndex());
                //否则更新载荷位置
                else updatePayload(positionPayload);

                outputPayloads.put(positionPayload, integer);
                positionPayload.targetPosition = setTargetPosition(integer, moveOutMover);
            });
        }


        //todo 解除帧率限制？
        public void dumpOutputs() {
            if (currentPlan == -1) {
                dump(items.first());
            } else if (timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : getOutputItems()) {
                    //dumpA
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

            outputPayloads.each((positionPayload, integer) -> {
                //如果载荷位于边缘 并且载荷已经到达目标位置
                if (integer < sideAmount() && hasArrived(positionPayload)) {
                    //如果能够输出到某种建筑, 移除该载荷
                    if (dumpPositionPayload(positionPayload)) {
                        remover.add(positionPayload);
                    } else if (positionPayload.payload.dump()) {//如果(单位)载荷自己输出
                        //todo 单位输出时 并且 面前有建筑时，应当给堵上
                        remover.add(positionPayload);
                        positionPayloads.remove(positionPayload);
                    }
                }
            });
            remover.each(positionPayload -> outputPayloads.remove(positionPayload));
            remover.clear();
        }

        @Override
        public boolean shouldConsume() {
            if (currentPlan == -1) return false;
            //如果生产后物品总量大于物品容量，返回否
            for (var output : getOutputItems()) {
                if (items.get(output.item) + output.amount > itemCapacity) return false;
            }

            for (var output : getOutputLiquids()) {
                if (liquids.get(output.liquid) >= liquidCapacity - 0.001f) return false;
            }

            //所有的加工载荷到齐后再进行加工
            for (PositionPayload positionPayload : craftPayloads) {
                if (!hasArrived(positionPayload)) return false;
            }

            //超出输出区容量暂时堆积的载荷, 到达输出区后再进行进行加工
            return !(outputPayloads.size > getOutMoverCapacity()) && enabled;
        }

        @Override
        public float getPowerProduction() {
            return currentPlan == -1 ? 0 : getOutputPower() * efficiency;
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
                //量子纠缠遗址, 一个载荷复制了N遍导致的
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

                    MovePayload movePayload = getOutputMover().get(output.item);
                    PositionPayload positionPayload = new PositionPayload(payload, setTargetPosition(i, movePayload), Vec2.ZERO);

                    handlePositionPayload(positionPayload);
                }

            }

            progress %= 1f;
        }
    }
}
