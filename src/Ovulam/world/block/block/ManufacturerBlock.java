package Ovulam.world.block.block;

import Ovulam.world.block.payload.ConsumeMultiPayloadBlock;
import Ovulam.world.graphics.OvulamShaders;
import Ovulam.world.other.Recipe;
import Ovulam.world.other.RecipeMover;
import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.LiquidStack;
import mindustry.type.PayloadStack;
import mindustry.ui.Bar;
import mindustry.world.Block;

import static mindustry.world.blocks.ConstructBlock.constructed;

public class ManufacturerBlock extends ConsumeMultiPayloadBlock {
    public Block targetBlock = Blocks.router;
    public Seq<ManufacturerStage> stage = new Seq<>(5);
    public TextureRegion[] stageRegion = new TextureRegion[5];

    public ManufacturerBlock(String name) {
        super(name);
        rotate = false;
    }

    @Override
    public void init() {
        size = targetBlock.size;
        super.init();
    }

    @Override
    public TextureRegion[] icons() {
        return new TextureRegion[]{stageRegion[0]};
    }

    @Override
    public void load() {
        super.load();
        stageRegion = new TextureRegion[stage.size + 1];
        for (int i = 0; i <= stage.size; i++) {
            stageRegion[i] = Core.atlas.find(targetBlock.name + "-stage-" + i);
        }
    }


    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (ManufacturerBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    public static class ManufacturerStage {
        public float craftTime;
        public Recipe inputRecipe;
        public RecipeMover[] recipeMover;

        public ManufacturerStage(float craftTime, Recipe inputRecipe, RecipeMover[] recipeMover) {
            this.craftTime = craftTime;
            this.inputRecipe = inputRecipe;
            this.recipeMover = recipeMover;
        }
    }

    public class ManufacturerBuild extends ConsumeMultiPayloadBuild {
        public int currentStage = 0;
        public float progress;

        @Override
        public float progress() {
            return progress / stage.get(currentStage).craftTime;
        }

        @Override
        public void updateTile() {
            moveInPayloads();

            if (efficiency > 0) progress += delta();

            if (progress >= stage.get(currentStage).craftTime) {

                positionPayloads.forEach(positionPayload ->
                        Fx.placeBlock.at(positionPayload.currentPosition.x + x,
                                positionPayload.currentPosition.y + y,
                                positionPayload.payload.size() / 8f));

                consume();
                progress %= 1f;

                //Fx.placeBlock.at(x, y, size);
                if (currentStage + 1 == stage.size) {
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
                for (int i = 0; i < stage.size; i++) {
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
            return stage.get(currentStage).inputRecipe;
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
        public Seq<PayloadStack> getInputPayloads() {
            return getRecipe().payloadStacks;
        }

        @Override
        public RecipeMover[] getMoveInMover() {
            return stage.get(currentStage).recipeMover;
        }
    }


}
