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
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.FrameBuffer;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Eachable;
import arc.util.Tmp;
import mindustry.Vars;
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
import mindustry.graphics.Layer;
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

import static arc.Core.camera;
import static mindustry.Vars.content;
import static mindustry.Vars.control;

//能够消耗, 产出载荷和其他资源的多合成工厂
public class MultiPayloadCrafter extends MultiPayloadBlock {
    //配方表
    public Seq<MultiPayloadPlan> plans = new Seq<>(5);
    //方块的自定义渲染
    public DrawBlock drawer = new DrawDefault();
    //改变配方是否清除自身携带的载荷
    public boolean changeClear;

    //行
    public int tableRows = 8;
    //列
    public int tableColumns = 8;

    //该工厂被摧毁的额外效果
    public Effect crafterDestroyEffect = OvulamFx.destroyTitanBlock;
    //工作特效
    public Effect craftEffect = Fx.placeBlock;

    //待加工区的move, 只有一种距离, 这意味着, 建议配方用相同宽度的材料载荷
    public MovePayload moveCapital = new MoveSize();
    //输出区的move, 只有一种距离, 这意味着, 建议配方用相同宽度的产品载荷
    public MovePayload moveOutMover = new MoveOut();

    public float outMoverCapacityMulti = 2f;

    private final PayloadStack[] emptyPayloadStacks = {};

    //todo 方块保存配置 与 地图保存配置
    public MultiPayloadCrafter(String name) {
        super(name);
        destroyEffect = Fx.none;
        clearOnDoubleTap = true;
        consumeBuilder.clear();
        configurable = true;

        consume(new ConsumeItemDynamic((MultiPayloadCrafterBuild e) ->
                e.validCraft() ? e.getInputItems().toArray(ItemStack.class) : ItemStack.empty));

        consume(new ConsumeLiquidsDynamic((MultiPayloadCrafterBuild e) ->
                e.validCraft() && !e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));

        consume(new ConsumeLiquidsDynamicCompletely((MultiPayloadCrafterBuild e) ->
                e.validCraft() && e.getInputLiquidsCompletely() ? e.getInputLiquids().toArray(LiquidStack.class) : LiquidStack.empty));

        consume(new ConsumePositionPayloadsDynamic((MultiPayloadCrafterBuild e) ->
                e.validCraft() ? e.getInputPayloads().toArray(PayloadStack.class) : emptyPayloadStacks));

        consume(new ConsumePowerDynamicCanBeNegative((MultiPayloadCrafterBuild e) ->
                e.validCraft() ? e.getInputPower() : 0));
    }

    @Override
    public void init() {
        tableColumns = Math.min(tableColumns, 8);

        for (MultiPayloadPlan plan : plans) {
            for (ItemStack stack : plan.inputRecipe.itemStacks) {
                itemCapacity = Math.max(itemCapacity, stack.amount * 2);
            }
            for (LiquidStack stack : plan.inputRecipe.liquidStacks) {
                liquidCapacity = Math.max(liquidCapacity, stack.amount * 2f * (plan.inputRecipe.liquidCompletely ? 1 : plan.craftTime));
            }
            plan.inputRecipe.payloadManagers.each(pm -> pm.init(this));
            plan.outputRecipe.payloadManagers.each(pm -> pm.init(this));

            if (!plan.outputRecipe.liquidStacks.isEmpty()) outputsLiquid = true;
            if (!plan.outputRecipe.payloadStacks().isEmpty()) outputsPayload = true;
            if (plan.outputRecipe.power > 0) outputsPower = true;
            if (plan.inputRecipe.power > 0) consumesPower = true;
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
    public void setStats() {
        super.setStats();
        stats.add(Stat.output, table -> {
            table.row();
            for (int i = 0; i < plans.size; i++) {
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
        //作为材料的载荷(inputPayloads)当中, 用于当前工作的载荷
        public Seq<PositionPayload> craftPayloads = new Seq<>();
        //输出的产物载荷
        public ObjectMap<PositionPayload, Integer> outputPayloads = new ObjectMap<>();

        public float inputPayloadsAlpha = 1f;
        public float craftPayloadsAlpha = 1f;
        public float outputPayloadsAlpha = 1f;

        public FrameBuffer inputFrameBuffer = new FrameBuffer();
        public FrameBuffer craftFrameBuffer = new FrameBuffer();
        public FrameBuffer outputFrameBuffer = new FrameBuffer();

        public Seq<PositionPayload> temp = new Seq<>();

        public float progress;
        public float totalProgress;
        public float warmup;

        public ObjectMap<Short, Bar> buildingBars = new ObjectMap<>();

        @Override
        public float progress() {
            return !validCraft() ? 0 : progress / getCurrentPlan().craftTime;
        }

        @Override
        public float totalProgress() {
            return totalProgress;
        }

        @Override
        public float warmup() {
            return warmup;
        }

        @Override
        public void buildConfiguration(Table table) {
            table.table(configTable -> {
                table.table(sliders -> {
                    sliders.slider(0, 1f, 0.01f, inputPayloadsAlpha, f -> inputPayloadsAlpha = f);
                    sliders.row();
                    sliders.slider(0, 1f, 0.01f, craftPayloadsAlpha, f -> craftPayloadsAlpha = f);
                    sliders.row();
                    sliders.slider(0, 1f, 0.01f, outputPayloadsAlpha, f -> outputPayloadsAlpha = f);
                });

                if(plans.size == 1)return;
                table.row();

                Table recipeShow = new Table(Styles.black6);

                Table topCont = new Table();
                int col = Mathf.ceil((float) plans.size / tableColumns);

                Table cont = new Table(Styles.black9);
                cont.defaults().size(40);

                hoveredPlan = currentPlan;
                updateRecipeTable(recipeShow);

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

                table.fill(recipeTable -> {
                    recipeTable.top();
                    recipeTable.setPosition(0, -40 * (col + 3) - 10);
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

        /////////////////////渲染载荷////////////////////////

        //region  block
        //sideRegion blockOver - 1
        //payloads blockOver
        //workRegion blockOver + 1
        //topRegion blockBuilding - 1
        //todo supportRegion blockBuilding - 1, topRegion flyingUnit + 1

        @Override
        public void draw() {
            drawer.draw(this);
            inputFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            craftFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());
            outputFrameBuffer.resize(Core.graphics.getWidth(), Core.graphics.getHeight());

            //渲染非加工区的输入载荷
            if(inputPayloadsAlpha > 0){
                Seq<PositionPayload> in = inputPayloads.select(pp -> !craftPayloads.contains(pp));
                drawAlphaPayloads(inputFrameBuffer, in, inputPayloadsAlpha);
            }

            //渲染加工区材料载荷 或者 加工效果
            if(craftPayloadsAlpha > 0 && validCraft()){
                if(efficiency > 0){
                    drawAlphaPayloadManagers(craftFrameBuffer, getInputManagers(), craftPayloadsAlpha, true);
                    drawAlphaPayloadManagers(craftFrameBuffer, getOutputManagers(), craftPayloadsAlpha, false);
                }else drawAlphaPayloads(craftFrameBuffer, craftPayloads, craftPayloadsAlpha);
            }

            //总是渲染输出载荷
            if(outputPayloadsAlpha > 0){
                drawAlphaPayloads(outputFrameBuffer, outputPayloads.keys().toSeq(), outputPayloadsAlpha);
            }
        }

        //渲染载荷
        public void drawAlphaPayloads(FrameBuffer buffer, Seq<PositionPayload> payloads, float alpha){
            Draw.draw(Layer.blockOver, () -> {
                buffer.begin(Color.clear);
                payloads.each(PositionPayload::draw);
                buffer.end();

                Draw.alpha(alpha);
                Tmp.tr1.set(Draw.wrap(buffer.getTexture()));
                Tmp.tr1.flip(false, true);

                //为什么是 4/镜头缩放, 我也不知道, 我的直觉告诉我的
                Draw.scl(4 / Vars.renderer.getDisplayScale());
                Draw.rect(Tmp.tr1, camera.position.x, camera.position.y);
                Draw.scl();
            });
        }

        //渲染载荷加工效果
        public void drawAlphaPayloadManagers(FrameBuffer buffer, Seq<RecipePayloadManager> managers, float alpha, boolean isInput){
            Draw.draw(Layer.blockOver, () -> {
                buffer.begin(Color.clear);

                if(isInput) managers.each(rpm -> rpm.drawInput(this));
                else managers.each(rpm -> rpm.drawOutput(this));

                buffer.end();

                Draw.alpha(alpha);
                Tmp.tr1.set(Draw.wrap(buffer.getTexture()));
                Tmp.tr1.flip(false, true);

                Draw.scl(4 / Vars.renderer.getDisplayScale());
                Draw.rect(Tmp.tr1, camera.position.x, camera.position.y);
                Draw.scl();
            });
        }

        ///////////////////////////////////////////////////
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

        ////////////////////接收材料//////////////////////

        @Override
        public int getMaximumAccepted(Item item) {
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack == null ? 0 : stack.amount * 2;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            //当前配方不需要载荷, 或者不需要该载荷
            if (!validCraft() || getInputPayloads().size == 0 || !getInputPayloads().contains(ps -> ps.item == payload.content())) return false;

            //必须的空间
            //第一倍的载荷输入到moveIn,所以总是能够输入
            //以外的载荷输入到moveCapital
            int place = getMoverLimit(getInputPayloads(), payload) + moveCapital.maxCapacity(block);

            for (PayloadStack ps : getInputPayloads()) {
                place -= Math.max(getPayloadAmount(ps.item) - (ps.item == payload.content() ? 0 : ps.amount), 0);
            }
            return place > 0;
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            if (!validCraft()) return false;
            return getInputLiquids().contains(liquidStack -> liquidStack.liquid == liquid);
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            if (!validCraft()) return false;
            ItemStack stack = getInputItems().find(itemStack -> itemStack.item == item);
            return stack != null && items.get(stack.item) < getMaximumAccepted(item);
        }

        //////////////////获得配方数据////////////////////

        public boolean validCraft(){
            return currentPlan != -1;
        }

        public Seq<MultiPayloadPlan> getPlans() {
            return plans;
        }

        public MultiPayloadPlan getCurrentPlan() {
            return !validCraft() ? null : plans.get(currentPlan);
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
            getCurrentPlan().inputRecipe.payloadManagers.each(rmp -> map.put(rmp.content(), rmp.movePayload));
            return map;
        }

        public Seq<RecipePayloadManager> getInputManagers(){
            return getCurrentPlan().inputRecipe.payloadManagers;
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
            getCurrentPlan().outputRecipe.payloadManagers.each(rmp -> map.put(rmp.content(), rmp.movePayload));
            return map;
        }

        public Seq<RecipePayloadManager> getOutputManagers(){
            return getCurrentPlan().outputRecipe.payloadManagers;
        }

        ////////////////调整液体栏/////////////////

        public boolean change() {
            return currentPlan != previousPlan;
        }

        public void addBuildingBar(Liquid liquid) {
            buildingBars.put(liquid.id, new Bar(
                            () -> liquid.localizedName,
                            liquid::barColor,
                            () -> this.liquids.get(liquid) / liquidCapacity
            ));
        }

        public void removeBuildingBar(Liquid liquid) {
            buildingBars.remove(liquid.id);
        }

        public void adjustBars(){
            if(previousPlan != -1) getLiquids(previousPlan).each(liquidStack -> removeBuildingBar(liquidStack.liquid));
            if(currentPlan != -1) getLiquids(currentPlan).each(liquidStack -> addBuildingBar(liquidStack.liquid));
        }
        @Override
        public void displayBars(Table table){
            super.displayBars(table);
            //todo 液体栏乱序
            buildingBars.values().toSeq().each(bar -> {
                table.add(bar).growX();
                table.row();
            });
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

        /////////////////////////////////////////////
        @Override
        public void updateTile() {
            if(plans.size == 1)currentPlan = 0;
            if (currentPlan < -1 || currentPlan >= plans.size) currentPlan = -1;
            if (change()) {
                adjustBars();
                previousPlan = currentPlan;
                progress = 0;
                if (changeClear) {
                    positionPayloads.each(pp -> Fx.breakBlock.at(pp.x(this), pp.y(this), pp.payload.size() / 8f));
                    positionPayloads.clear();
                    outputPayloads.clear();
                }
            }

            if (validCraft()) {
                warmup = Mathf.approachDelta(warmup, efficiency, getCurrentPlan().warmupSpeed * delta());
                progress += warmup * edelta();
                totalProgress += warmup * edelta();

                if (!getCurrentPlan().outputRecipe.liquidCompletely) {
                    getOutputLiquids().each(ls -> handleLiquid(this, ls.liquid, edelta() * ls.amount));
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
            Seq<Integer> integers = outputPayloads.values().toSeq();
            for (int i = 0; i < integers.size; i++) {
                if (!integers.contains(i)) return i;
            }
            return integers.size;
        }

        public int getOutMoverCapacity() {
            return (int) (moveOutMover.maxCapacity(block) * outMoverCapacityMulti);
        }

        //区分建筑内的载荷是输入(输入 和 缓存)还是输出
        public void checkPositionPayloads() {
            inputPayloads.clear();
            craftPayloads.clear();

            if (!validCraft() || getInputPayloads().size == 0) {
                //配方中不存在输入载荷时，所有载荷都应该输出
                temp.selectFrom(positionPayloads, p -> !outputPayloads.containsKey(p));
            } else {
                /////////////////////
                //否则将所以非输入的载荷设为输出
                for (PositionPayload pp : positionPayloads) {
                    //如果该 载荷 包含在 加工载荷(使用moveIn) 中
                    if (getInputPayloads().contains(ps -> pp.content() == ps.item)) {

                        //判断数量，如果加工载荷能容纳该载荷，则加入该载荷
                        int amount = craftPayloads.count(pp1 -> pp1.content() == pp.content());
                        if (amount < getMoverLimit(getInputPayloads(), pp.payload)) {
                            craftPayloads.add(pp);
                        }

                        inputPayloads.add(pp);
                    }
                }
                //非输入载荷的载荷, 都应该输出
                temp.selectFrom(positionPayloads, p -> !inputPayloads.contains(p) && !outputPayloads.containsKey(p));
            }
            temp.each(pp -> outputPayloads.put(pp, getMinimumIndex()));
            temp.clear();
        }

        public int sideAmount() {
            return (size - 1) / 2;
        }

        //输入的载荷优先填充moveIn，然后填充moveCapital
        //不以moveInMover的容量去判断moveIn是否填满，加工区容纳一次加工所需要的所以载荷
        public void moveInPayloads() {
            //输入判断，所有非输入的载荷都应该输出
            for (PositionPayload pp : craftPayloads) {

                int index = 0;
                MovePayload movePayload = getInputMover().get(pp.content());

                if (movePayload == null) {
                    movePayload = moveInMover;
                    index = craftPayloads.indexOf(pp);
                } else {
                    for (PositionPayload pp1 : craftPayloads) {
                        if (pp1.content() == pp.content()) {
                            if (pp1 == pp) break;
                            index++;

                        }
                    }
                }
                pp.targetPosition = setTargetPosition(index, movePayload);
            }

            int place = 0;

            for (PositionPayload pp : inputPayloads) {
                int index = inputPayloads.indexOf(pp);
                //如果载荷属于加工区载荷
                if (!craftPayloads.contains(pp)) {
                    pp.targetPosition = setTargetPosition(index - place, moveCapital);
                } else {
                    place++;
                }
                updatePayload(pp);
            }
        }

        //输出载荷
        public void moveOutPayloads() {
            outputPayloads.each((pp, integer) -> {
                //如果载荷的索引 大于 sideAmount()，并且输出载荷不包含 （索引 减去 sideAmount()）的值
                //该载荷“前方”有空位，改变索引移动到空位
                if (integer >= sideAmount() && !outputPayloads.containsValue(integer - sideAmount(), true)) {
                    integer -= sideAmount();
                }

                //如果 输出区存在空间, 索引改为空间的索引(查找合适的位置)
                if (integer >= getOutMoverCapacity()) integer = Math.min(integer, getMinimumIndex());
                    //否则更新载荷位置
                else updatePayload(pp);

                outputPayloads.put(pp, integer);
                pp.targetPosition = setTargetPosition(integer, moveOutMover);
            });
        }

        public void dumpOutputs() {
            if (!validCraft()) {
                dump(items.first());
            } else if (timer(timerDump, dumpTime / timeScale)) {
                for (ItemStack output : getOutputItems()) {
                    dumpAccumulate(output.item);
                }
            }

            if (!validCraft()) {
                liquids.each((liquid, amount) -> dumpLiquid(liquid, 2f));
            } else {
                for (LiquidStack liquidStack : getOutputLiquids()) {
                    dumpLiquid(liquidStack.liquid, 2f);
                }
            }

            outputPayloads.each((pp, integer) -> {
                //如果载荷位于边缘 并且载荷已经到达目标位置
                if (integer < sideAmount() && hasArrived(pp)) {
                    //如果能够输出到某种建筑, 移除该载荷
                    if (dumpPositionPayload(pp)) {
                        temp.add(pp);
                    } else if (pp.payload.dump()) {//如果(单位)载荷自己输出
                        //todo 单位输出时 并且 面前有建筑时，应当给堵上
                        temp.add(pp);
                        positionPayloads.remove(pp);
                    }
                }
            });
            temp.each(pp -> outputPayloads.remove(pp));
            temp.clear();
        }

        @Override
        public boolean shouldConsume() {
            if (!validCraft()) return false;
            //如果生产后物品总量大于物品容量，返回否
            for (ItemStack output : getOutputItems()) {
                if (items.get(output.item) + output.amount > itemCapacity) return false;
            }

            for (LiquidStack output : getOutputLiquids()) {
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
            return !validCraft() ? 0 : getOutputPower() * efficiency;
        }

        public void craft() {
            consume();

            for (ItemStack output : getOutputItems()) {
                this.handleStack(output.item, output.amount, this);
            }

            if (getCurrentPlan().outputRecipe.liquidCompletely) {
                for (LiquidStack output : getOutputLiquids()) {
                    this.handleLiquid(this, output.liquid, output.amount * getCurrentPlan().craftTime);
                }
            }

            for (PayloadStack output : getOutputPayloads()) {
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
                    PositionPayload cp = new PositionPayload(payload, setTargetPosition(i, movePayload), Vec2.ZERO);

                    handlePositionPayload(cp);
                }

            }

            progress %= 1f;
        }

        ///////////////////////////////////
        //用于测试
        public void drawTargetPosition() {
            positionPayloads.each(positionPayload -> Drawf.dashCircle(x + positionPayload.targetPosition.x,
                    y + positionPayload.targetPosition.y, 3, Pal.accent));
        }

        public void drawInputTargetPosition() {
            inputPayloads.each(positionPayload -> Drawf.dashCircle(x + positionPayload.targetPosition.x,
                    y + positionPayload.targetPosition.y, 3, Color.cyan));
        }

        public void drawOutputTargetPosition() {
            for (PositionPayload positionPayload : outputPayloads.keys()) {
                Drawf.dashCircle(x + positionPayload.targetPosition.x,
                        y + positionPayload.targetPosition.y, 3, Color.orange);
            }
        }

        public void drawOutputLine() {
            for (PositionPayload positionPayload : outputPayloads.keys()) {
                Drawf.line(Color.pink,
                        positionPayload.x(this), positionPayload.y(this),
                        x + positionPayload.targetPosition.x,
                        y + positionPayload.targetPosition.y);
            }
        }
    }
}
