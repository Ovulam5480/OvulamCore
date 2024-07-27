package Ovulam.modContent;

import Ovulam.UI.EventAnimation;

public class OvulamEventAnimations {
    public static  EventAnimation
            researchLost,
            selfOrganization, selfOrganizationCollapse, selfOrganizationConfusion, selfOrganizationDecompose;

    public static void init() {
        researchLost = new EventAnimation("Research-Lost");
        selfOrganization = new EventAnimation("Self-Organization");
        selfOrganizationCollapse = new EventAnimation("Self-Organization-Collapse");
        selfOrganizationConfusion = new EventAnimation("Self-Organization-Confusion");
        selfOrganizationDecompose = new EventAnimation("Self-Organization-Decompose");
    }
}
