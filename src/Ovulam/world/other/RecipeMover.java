package Ovulam.world.other;

import mindustry.ctype.UnlockableContent;
import Ovulam.world.move.MovePayload;

public class RecipeMover {
    public UnlockableContent unlockableContent;
    public MovePayload movePayload;

    public RecipeMover(UnlockableContent unlockableContent, MovePayload movePayload) {
        this.unlockableContent = unlockableContent;
        this.movePayload = movePayload;
    }
}