package Ovulam.world.event;

import Ovulam.OvulamMod;
import Ovulam.UI.EventAnimation;
import arc.util.Nullable;

public class OvulamEvent {
    public @Nullable EventAnimation startAnimation, endAnimation;
    public float completeTime;

    public boolean inDuration = false;

    public OvulamEvent(EventAnimation startAnimation, EventAnimation endAnimation, float completeTime) {
        this.startAnimation = startAnimation;
        this.endAnimation = endAnimation;
        this.completeTime = completeTime;
    }

    public OvulamEvent(float completeTime) {
        this.completeTime = completeTime;
    }

    public void init(){

    }

    public void begin(){
        inDuration = true;
        if(startAnimation != null){
            OvulamMod.renderer.setAnimal(startAnimation);
        }
    }

    public void finish(){
        inDuration = false;
        if(endAnimation != null){
            OvulamMod.renderer.setAnimal(endAnimation);
        }
    }

    public void update(){

    }

    public void draw(){

    }

    public void setCompleteTime(float completeTime) {
        this.completeTime = completeTime;
    }
}
