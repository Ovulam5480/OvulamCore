package Ovulam;

import Ovulam.UI.EventAnimation;
import Ovulam.modContent.OvulamEventAnimations;
import arc.util.Nullable;
import arc.util.Time;
import mindustry.Vars;

public class OvulamRenderer {
    public float animalFrames;
    public @Nullable EventAnimation currentAnimation = OvulamEventAnimations.researchLost;

    public void draw(){
    }

    public void apply(){
        applyAnimation();
    }

    public void applyAnimation(){
        if(currentAnimation != null){
            animalFrames += Vars.state.isPaused() || !Vars.state.isGame() ? 0 : Time.delta;

            if(animalFrames > currentAnimation.animationTime){
                currentAnimation.setVisible(false);
                currentAnimation = null;
                animalFrames = 0;
            }
            else currentAnimation.applyTable(animalFrames);
        }
    }

    public void setAnimal(EventAnimation animation){
        if(currentAnimation != null){
            currentAnimation.setVisible(false);
        }
        animalFrames = 0;
        currentAnimation = animation;
        currentAnimation.setVisible(true);
    }
}
