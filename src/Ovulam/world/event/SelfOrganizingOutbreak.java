package Ovulam.world.event;

import Ovulam.modContent.OvulamEventAnimations;

//自组织爆发事件线
public class SelfOrganizingOutbreak extends OvulamEvent{

    public SelfOrganizingOutbreak(){
        startAnimation = OvulamEventAnimations.selfOrganization;
        //三十分钟
        EventTime = 30 * 60 * 60;
    }

    @Override
    public void trigger() {

    }

    public void begin() {

        super.begin();
    }

    public void end() {

        super.end();
    }

    public void update(){
        super.update();
    }
}
