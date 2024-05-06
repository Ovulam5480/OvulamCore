package Ovulam.world.block.block;

import Ovulam.UI.RecipeTable;
import Ovulam.world.block.production.ConsumeMultiPayloadBlock;
import Ovulam.world.graphics.OvulamShaders;
import Ovulam.world.move.MovePayload;
import Ovulam.world.type.Recipe;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
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

import java.util.HashMap;

import static mindustry.world.blocks.ConstructBlock.constructed;

public class ManufacturerBlock extends ConsumeMultiPayloadBlock {
    public Block targetBlock = Blocks.router;
    public static Seq<ManufacturerStage> stages = new Seq<>(5);
    public TextureRegion[] stageRegion = new TextureRegion[5];

    public ManufacturerBlock(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public void init() {
        size = targetBlock.size;
        stages.each(stage -> stage.inputRecipe.payloadManagers.forEach(pm -> pm.init(this)));
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

        /*
        public ManufacturerStage(float craftTime,
                                 Object[] inputItems, Object[] inputLiquids, Object[] inputPayloads,
                                 float inputPower, boolean inputLiquidCompletely,
                                 RecipeMover[] recipeMover) {
            this.craftTime = craftTime;

            this.inputRecipe = new Recipe(ItemStack.list(inputItems), LiquidStack.list(inputLiquids),
                    PayloadStack.list(inputPayloads), inputPower, inputLiquidCompletely);

            this.recipeMover = recipeMover;
        }

         */
    }

    public class ManufacturerBuild extends ConsumeMultiPayloadBuild {
        public int currentStage = 0;

        @Override
        public float warmup(){
            return 1;
        }

        @Override
        public void updateTile() {
            moveInPayloads();

            if (efficiency > 0){
                progress += delta();
                totalProgress += delta();
            }

            if (progress >= stages.get(currentStage).craftTime) {

                positionPayloads.each(positionPayload ->
                        Fx.placeBlock.at(positionPayload.x(this), positionPayload.y(this), positionPayload.payload.size() / 8f));

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

        @Override
        public void draw() {
            Draw.rect(stageRegion[0], x, y);

            Draw.draw(Layer.blockOver - 1f, () -> {
                for (int i = 0; i < stages.size; i++) {
                    float stageProgress;

                    if (i == currentStage) stageProgress = progress();
                    else stageProgress = Mathf.sign(i < currentStage);

                    Draw.color(Pal.accent);
                    OvulamShaders.blockManufacturer.progress = stageProgress;
                    OvulamShaders.blockManufacturer.time = Time.time;
                    OvulamShaders.blockManufacturer.region = stageRegion[i + 1];

                    Draw.shader(OvulamShaders.blockManufacturer);
                    Draw.rect(stageRegion[i + 1], x, y);
                    Draw.shader();

                }
            });

            Draw.z(Layer.blockOver);
            Draw.alpha(progress());
            drawPayload();
        }

        @Override
        public boolean shouldConsume() {
            return !positionPayloads.contains(positionPayload -> !hasArrived(positionPayload));
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
        public HashMap<UnlockableContent, MovePayload> getInputMover() {
            HashMap<UnlockableContent, MovePayload> map = new HashMap<>();
            getRecipe().payloadManagers.each(manager -> map.put(manager.content(), manager.movePayload));
            return map;
        }

        @Override
        public float getCraftTime() {
            return stages.get(currentStage).craftTime;
        }
    }
}
