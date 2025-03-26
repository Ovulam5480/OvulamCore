package Ovulam;

import Ovulam.UI.BossBarFragment;
import Ovulam.modContent.OvulamContents;
import Ovulam.modContent.OvulamEventAnimations;
import Ovulam.modContent.OvulamStages;
import arc.graphics.Color;
import mindustry.Vars;
import mindustry.content.*;
import mindustry.graphics.Shaders;
import mindustry.mod.Mod;


public class OvulamMod extends Mod {
    public static OvulamRenderer renderer = new OvulamRenderer();
    public static BossBarFragment bossBar;

    public OvulamMod() {
    }

    public static String modName() {
        return "ovulam-";
    }

    @Override
    public void init() {
        OvulamEventAnimations.init();
        //OvulamMechanicsEvents.init();
        OvulamStages.init();

//        bossBar = new BossBarFragment(Vars.ui.hudGroup);
//        bossBar.putBarMap(u -> u.hasEffect(StatusEffects.boss), Color.red, f -> {
//            if(f < 0.5f)return Shaders.buildBeam;
//            return Shaders.water;
//        });

//        Vars.content.blocks().each(b -> {
//            if(b.underBullets){Log.info(b.localizedName)}
//        });


//        Events.on(EventType.ClientLoadEvent.class, e -> {
//            Time.runTask(800f, () -> {
//                BaseDialog dialog = new BaseDialog("frog");
//
//                dialog.cont.add(new BossBar(Color.red, () -> Mathf.sinDeg(Time.time) * 0.5f + 0.5f, f -> {
//                    if(f < 0.5f)return Shaders.buildBeam;
//                    return Shaders.shield;
//                })).size(900, 64);
//
//                dialog.cont.row();
//                dialog.cont.button("11111", dialog::hide).size(100f, 50f);
//                dialog.show();
//            });
//        });
//
//        UnitTypes.disrupt.targetAir = true;
//        UnitTypes.disrupt.weapons.first().bullet.spawnUnit.targetAir = true;
//        UnitTypes.disrupt.weapons.first().bullet.spawnUnit.weapons.first().bullet.collidesAir = true;


    }

    @Override
    public void loadContent() {
        OvulamContents.load();
    }
}

