package Ovulam.world.type;

import Ovulam.world.drawRecipePayload.DrawPayloadDefault;
import Ovulam.world.drawRecipePayload.DrawRecipePayload;
import Ovulam.world.move.MoveDefault;
import Ovulam.world.move.MovePayload;
import arc.struct.Seq;
import mindustry.ctype.UnlockableContent;
import mindustry.gen.Building;
import mindustry.type.PayloadStack;

public class RecipePayloadManager {
    public PayloadStack payloadStack;
    public MovePayload movePayload;
    public DrawRecipePayload drawRecipePayload;

    public RecipePayloadManager(PayloadStack payloadStack) {
        this(payloadStack, new MoveDefault(), new DrawPayloadDefault());
    }

    public RecipePayloadManager(PayloadStack payloadStack, MovePayload movePayload) {
        this(payloadStack, movePayload, new DrawPayloadDefault());
    }

    public RecipePayloadManager(PayloadStack payloadStack, DrawRecipePayload drawRecipePayload) {
        this(payloadStack, new MoveDefault(), drawRecipePayload);
    }

    public RecipePayloadManager(PayloadStack payloadStack, MovePayload movePayload, DrawRecipePayload drawRecipePayload) {
        this.payloadStack = payloadStack;
        this.movePayload = movePayload;
        this.drawRecipePayload = drawRecipePayload;
    }

    public UnlockableContent content(){
        return payloadStack.item;
    }

    public static Seq<PayloadStack> getPayloadStacks(Seq<RecipePayloadManager> recipePayloadManagers){
        Seq<PayloadStack> payloadStacks = new Seq<>(recipePayloadManagers.size);
        recipePayloadManagers.each(recipePayloadManager -> payloadStacks.add(recipePayloadManager.payloadStack));
        return payloadStacks;
    }

    public void drawRecipePayload(Building building, float progress){
        for (int i = 0; i < movePayload.maxCapital(building.block); i++){
            drawRecipePayload.draw(building, movePayload.setTargetPosition(building, i));
        }
    }

    public void drawInput(Building building){
        for (int i = 0; i < movePayload.maxCapital(building.block); i++){
            drawRecipePayload.draw(building, movePayload.setTargetPosition(building, i));
        }
    }
    public void drawOutput(Building building){
        for (int i = 0; i < movePayload.maxCapital(building.block); i++){
            drawRecipePayload.draw(building, movePayload.setTargetPosition(building, i));
        }
    }
}