package Ovulam.mod;

import Ovulam.mod.Blocks.Crafting;
import Ovulam.mod.Blocks.Effect;
import Ovulam.mod.Blocks.Turret;
import Ovulam.world.No9527.LaserLaserBulletType;
import Ovulam.world.No9527.平行线weapon;
import Ovulam.world.block.AAABlock;
import Ovulam.world.block.block.ItemBlock;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.entities.pattern.ShootAlternate;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;
import mindustry.world.blocks.defense.turrets.ItemTurret;
import mindustry.world.blocks.defense.turrets.LaserTurret;

public class OvulamBlocks {
    //大型垃圾堆
    public static Block[] itemBlocks;
    public static Block SSSSS;

    public static void load() {
        Turret.load();
        Crafting.load();
        Effect.load();

        UnitTypes.dagger.weapons = Seq.with(new 平行线weapon("large-weapon"){{
            reload = 13f;
            x = 4f;
            y = 2f;
            top = false;
            ejectEffect = Fx.casing1;
            bullet = new BasicBulletType(2.5f, 9){{
                width = 7f;
                height = 9f;
                lifetime = 60f;
            }};
            shoot = new ShootAlternate(){{
                barrels = shots = 10;
                spread = 3f;
            }};
        }});

        UnitTypes.dagger.weapons.each(weapon -> {
            weapon.rotate = false;
            weapon.reload = 5f;
            weapon.shoot = new ShootAlternate(){{
                barrels = shots = 10;
                spread = 3f;
            }};
        });

        (((ItemTurret) Blocks.duo)).reload = 5f;
        (((ItemTurret) Blocks.duo)).shoot = new ShootAlternate(){{
            barrels = shots = 10;
            spread = 3f;
        }};

        SSSSS = new AAABlock("SSSSS") {{
            requirements(Category.defense, new ItemStack[]{});
        }};

        ((LaserTurret) Blocks.meltdown).shootType = new LaserLaserBulletType() {{
            length = 180f;
            drawSize = 420f;
        }};

        itemBlocks = new Block[Vars.content.items().size];
        for (int i = 0; i < Vars.content.items().size; i++) {
            itemBlocks[i] = new ItemBlock(Vars.content.item(i), 2);
        }
    }
}


