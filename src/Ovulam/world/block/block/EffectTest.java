package Ovulam.world.block.block;

import Ovulam.UI.EventAnimation;
import Ovulam.modContent.OvulamEventAnimations;
import arc.scene.ui.layout.Table;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.gen.Icon;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public class EffectTest extends Block {
    public Effect effect = Fx.blastExplosion;
    //水平移动, 只影响X
    public EventAnimation A = OvulamEventAnimations.selfOrganization;
    public EventAnimation S = OvulamEventAnimations.selfOrganizationConfusion;
    public EventAnimation D = OvulamEventAnimations.selfOrganizationDecompose;
    public EventAnimation W = OvulamEventAnimations.selfOrganizationCollapse;


    public EffectTest(String name) {
        super(name);
        configurable = true;
        update = true;
        requirements(Category.defense, new ItemStack[]{});
        clipSize = 60000;
    }

    public class EffectTestBuild extends Building{

        @Override
        public void buildConfiguration(Table table) {
            table.button(Icon.tree, () -> A.reset());
            table.button(Icon.tree, () -> S.reset());
            table.button(Icon.tree, () -> W.reset());
            table.button(Icon.tree, () -> D.reset());

        }
    }
}
