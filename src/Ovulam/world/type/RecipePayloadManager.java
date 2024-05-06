package Ovulam.world.type;

import Ovulam.world.drawRecipePayload.DrawRecipePayload;
import Ovulam.world.move.MovePayload;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.PayloadStack;
import mindustry.world.Block;

public class RecipePayloadManager {
    public PayloadStack payloadStack;
    public MovePayload movePayload;
    public DrawRecipePayload drawRecipePayload;


    public RecipePayloadManager(UnlockableContent payload, int amount, MovePayload movePayload, DrawRecipePayload drawRecipePayload) {
        this(new PayloadStack(payload, amount), movePayload, drawRecipePayload);
    }

    public RecipePayloadManager(PayloadStack payloadStack, MovePayload movePayload, DrawRecipePayload drawRecipePayload) {
        this.payloadStack = payloadStack;
        this.movePayload = movePayload;
        this.drawRecipePayload = drawRecipePayload;
    }

    public static Seq<RecipePayloadManager> list(Object... items){
        Seq<RecipePayloadManager> list = new Seq<>();
        for (int i = 0; i < items.length; i = i + 4){
            list.add(new RecipePayloadManager((
                    UnlockableContent) items[i],
                    (Integer) items[i + 1],
                    (MovePayload) items[i + 2],
                    (DrawRecipePayload) items[i + 3]));
        }
        return list;
    }

    public UnlockableContent content(){
        return payloadStack.item;
    }

    public static Seq<PayloadStack> getPayloadStacks(Seq<RecipePayloadManager> recipePayloadManagers){
        Seq<PayloadStack> payloadStacks = new Seq<>(recipePayloadManagers.size);
        recipePayloadManagers.each(recipePayloadManager -> payloadStacks.add(recipePayloadManager.payloadStack));
        return payloadStacks;
    }

    public void init(Block block){
        drawRecipePayload.init(block, content());
    }

    public void drawRecipePayload(Building building, float progress){
        for (int i = 0; i < movePayload.maxCapacity(building.block); i++){
            drawRecipePayload.draw(content(), building, progress, movePayload.setTargetPosition(building, i));
        }
    }

    public void drawInput(Building building){
        drawRecipePayload(building, 1 - building.progress());
    }
    public void drawOutput(Building building){
        drawRecipePayload(building, building.progress());
    }
}