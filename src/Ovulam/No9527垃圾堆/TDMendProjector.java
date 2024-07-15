package Ovulam.No9527垃圾堆;

import arc.math.Mathf;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.world.blocks.defense.MendProjector;

import static mindustry.Vars.indexer;

public class TDMendProjector extends MendProjector {
    public float healAmount = 40f;
    public TDMendProjector(String name) {
        super(name);
        healPercent = 0;
        phaseBoost = 40f;
    }

    public class TDMendBuild extends MendBuild {
        @Override
        public void updateTile(){


            Vars.content.units().get(42).speed = 15;




            boolean canHeal = !checkSuppression();

            smoothEfficiency = Mathf.lerpDelta(smoothEfficiency, efficiency, 0.08f);
            heat = Mathf.lerpDelta(heat, efficiency > 0 && canHeal ? 1f : 0f, 0.08f);
            charge += heat * delta();

            phaseHeat = Mathf.lerpDelta(phaseHeat, optionalEfficiency, 0.1f);

            if(optionalEfficiency > 0 && timer(timerUse, useTime) && canHeal){
                consume();
            }

            if(charge >= reload && canHeal){
                float realRange = range + phaseHeat * phaseRangeBoost;
                charge = 0f;

                indexer.eachBlock(this, realRange, b -> b.damaged() && !b.isHealSuppressed(), other -> {
                    other.heal((healAmount + phaseHeat * phaseBoost) * efficiency);
                    other.recentlyHealed();
                    Fx.healBlockFull.at(other.x, other.y, other.block.size, baseColor, other.block);
                });
            }
        }
    }
}
