package Ovulam.modContent;

import Ovulam.UI.EventAnimation;

public class OvulamEventAnimations {
    public static  EventAnimation
            researchLost,
            selfOrganization, selfOrganizationCollapse, selfOrganizationConfusion, selfOrganizationDecompose;

    public static void init() {
        researchLost = new EventAnimation("Research-Lost");
        //爆发
        selfOrganization = new EventAnimation("Self-Organization");
        //坍缩
        selfOrganizationCollapse = new EventAnimation("Self-Organization-Collapse");
        //异化
        selfOrganizationConfusion = new EventAnimation("Self-Organization-Confusion");
        //崩溃
        selfOrganizationDecompose = new EventAnimation("Self-Organization-Decompose");
    }
}
