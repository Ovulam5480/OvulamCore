package Ovulam.modContent.Blocks;

import Ovulam.entities.bullet.MortarBulletType;
import Ovulam.world.block.defense.ItemStackTurret;
import Ovulam.world.block.defense.Mortar;
import Ovulam.world.drawBlock.DrawOrganize;
import mindustry.content.Fx;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.Sounds;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.world.Block;

public abstract class turret {
    public static Block order;
    public static Block organize;
    public static Block Mortar;

    public static void load(){
        Mortar = new Mortar("mortar") {{
            requirements(Category.turret, new ItemStack[]{});
            size = 15;
        }};

        order = new ItemStackTurret("order"){{
            shootSound = Sounds.shootBig;
            bullet = new BasicBulletType(7f, 75f){{
                width = 20f;
                height = 35f;
            }};
            range = 300f;

            size = 4;
            reload = 30f;
            itemCapacity = 120;
            requirements(Category.turret, new ItemStack[]{});

            flammabilityMultiplier = 100f;
            explosivenessMultiplier = 100f;
            radioactivityMultiplier = 100f;
            chargeMultiplier = 100f;
        }};

        organize = new ItemStackTurret("organize"){{
            shootSound = Sounds.shootBig;
            bullet = new MortarBulletType("organize", 400){{
                lifetime = 240f;
                offsideMultiplier = 2f;
                rotateMultiplier = 3f;
                collides = false;
            }};
            range = 400f;

            drawer = new DrawOrganize();

            shootEffect = Fx.launchPod;
            size = 4;
            reload = 120f;
            itemCapacity = 300;
            requirements(Category.turret, new ItemStack[]{});

            shootX = shootY = 0;

            flammabilityMultiplier = 100f;
            explosivenessMultiplier = 100f;
            radioactivityMultiplier = 100f;
            chargeMultiplier = 100f;
        }};


    }

    public abstract void draw();
}
