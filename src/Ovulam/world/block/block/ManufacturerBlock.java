package Ovulam.world.block.block;

import Ovulam.UI.RecipeTable;
import Ovulam.world.block.production.ConsumeMultiPayloadBlock;
import Ovulam.world.graphics.OvulamShaders;
import Ovulam.world.move.MovePayload;
import Ovulam.world.type.Recipe;
import Ovulam.world.type.RecipePayloadManager;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Tex;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.meta.Stat;

import static mindustry.world.blocks.ConstructBlock.constructed;

public class ManufacturerBlock extends ConsumeMultiPayloadBlock {
    public Block targetBlock = Blocks.router;
    public Seq<ManufacturerStage> stages = new Seq<>(5);
    public TextureRegion[] stageRegion = new TextureRegion[5];

    public ManufacturerBlock(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public void init() {
        size = targetBlock.size;
        stages.each(stage -> stage.inputRecipe.payloadManagers.each(pm -> pm.init(this)));
        super.init();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{stageRegion[0]};
    }

    @Override
    public void load() {
        super.load();
        stageRegion = new TextureRegion[stages.size + 1];
        for (int i = 0; i <= stages.size; i++) {
            stageRegion[i] = Core.atlas.find(targetBlock.name + "-stage-" + i);
        }
        stages.each((stage) -> stage.inputRecipe.payloadManagers.each(rpm -> rpm.drawRecipePayload.load(this)));
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.input, table -> {
            table.row();
            for(int i = 0; i < stages.size; i++){
                ManufacturerStage stage = stages.get(i);
                int finalI = i;

                table.table(stageTable -> {
                    stageTable.setBackground(Tex.buttonSelectTrans);
                    stageTable.add("stage-" + finalI).center().marginBottom(10f).row();

                    stageTable.table(stageTableTable -> RecipeTable.addRecipeTable(stageTableTable, stage.inputRecipe)).center();

                }).growX().pad(10);

                table.row();
            }
        });
    }


    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (ManufacturerBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    public static class ManufacturerStage {
        public float craftTime;
        public Recipe inputRecipe;

        public ManufacturerStage(float craftTime, Recipe inputRecipe) {
            this.craftTime = craftTime;
            this.inputRecipe = inputRecipe;
        }

        public ManufacturerStage(float craftTime, Object[] inputItems, Object[] inputLiquids,
                                 Object[] inputPayloads, float inputPower, boolean inputLiquidCompletely){
            this.craftTime = craftTime;
            this.inputRecipe = new Recipe(ItemStack.list(inputItems), LiquidStack.list(inputLiquids),
                    RecipePayloadManager.list(inputPayloads), inputPower, inputLiquidCompletely);
        }
    }

    //todo stage 为0引发的问题
    public class ManufacturerBuild extends ConsumeMultiPayloadBuild {
        public int currentStage = 0;

        @Override
        public float warmup(){
            return 1;
        }

        @Override
        public float progress() {
            return progress / getCraftTime();
        }

        @Override
        public void updateTile() {
            moveInPayloads();

            if (efficiency > 0){
                progress += delta();
                totalProgress += delta();
            }

            if (progress >= stages.get(currentStage).craftTime) {

                positionPayloads.each(pp -> Fx.placeBlock.at(pp.x(this), pp.y(this), pp.payload.size() / 8f));

                consume();
                progress %= 1f;

                //Fx.placeBlock.at(x, y, size);
                if (currentStage + 1 == stages.size) {
                    if (!Vars.net.client()) {
                        constructed(tile, targetBlock, null, (byte) rotation, team, null);
                    }
                } else currentStage++;

            }
        }

        //todo 显然没必要复制一份一模一样的贴图
        @Override
        public void draw() {
            Draw.rect(stageRegion[0], x, y);

            Draw.draw(Layer.blockOver, () -> {
                for (int i = 0; i < stages.size; i++) {
                    float stageProgress;

                    if (i == currentStage) stageProgress = progress();
                    //todo 测试!!!
                    else stageProgress = Mathf.num(i < currentStage);
                    Draw.shader(OvulamShaders.blockManufacturer);

                    Draw.color(Pal.accent);
                    OvulamShaders.blockManufacturer.progress = stageProgress;
                    OvulamShaders.blockManufacturer.time = Time.time;
                    OvulamShaders.blockManufacturer.region = stageRegion[i + 1];

                    Draw.rect(stageRegion[i + 1], x, y);
                    Draw.shader();
                }
            });

            Draw.z(Layer.blockOver + 1f);
            drawPayload();
        }

        @Override
        public boolean shouldConsume() {
            return !positionPayloads.contains(pp -> !hasArrived(pp));
        }

        public Recipe getRecipe() {
            return stages.get(currentStage).inputRecipe;
        }

        @Override
        public Seq<ItemStack> getInputItems() {
            return getRecipe().itemStacks;
        }

        @Override
        public Seq<LiquidStack> getInputLiquids() {
            return getRecipe().liquidStacks;
        }

        @Override
        public boolean getInputLiquidsCompletely(){
            return getRecipe().liquidCompletely;
        }

        @Override
        public Seq<PayloadStack> getInputPayloads() {
            return stages.get(currentStage).inputRecipe.payloadStacks();
        }

        @Override
        public float getInputPower(){
            return getRecipe().power;
        }

        @Override
        public ObjectMap<UnlockableContent, MovePayload> getInputMover() {
            ObjectMap<UnlockableContent, MovePayload> map = new ObjectMap<>();
            getRecipe().payloadManagers.each(rpm -> map.put(rpm.content(), rpm.movePayload));
            return map;
        }

        @Override
        public float getCraftTime() {
            return stages.get(currentStage).craftTime;
        }
    }
}
