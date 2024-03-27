package Ovulam.world.block.defense;

import Ovulam.type.bullet.OvulamDynamicExplosionBulletType;
import arc.graphics.g2d.Font;
import arc.util.Align;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
import mindustry.ui.Fonts;
import mindustry.world.blocks.defense.turrets.Turret;

public class ItemStackTurret extends Turret {
    public BulletType bullet;
    public int minAmmo = 40;
    public float flammabilityMultiplier, explosivenessMultiplier, radioactivityMultiplier, chargeMultiplier;

    public ItemStackTurret(String name) {
        super(name);
        hasItems = true;
        maxAmmo = 150;
        range = 400;
    }


    @Override
    public void init(){
        itemCapacity = maxAmmo;
        super.init();
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("ammo", (ItemStackTurretBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float)entity.totalAmmo / maxAmmo
                )
        );
    }

    @Override
    public void limitRange(BulletType bullet, float margin){
        super.limitRange(bullet, margin);
    }

    public class ItemStackTurretBuild extends TurretBuild{

        @Override
        public void updateTile(){
            totalAmmo = items.total();

            unit.ammo((float)unit.type().ammoCapacity * totalAmmo / maxAmmo);

            super.updateTile();
        }

        @Override
        public void draw(){
            super.draw();
            Font font = Fonts.outline;

            float flammability = items.sum((item, amount) -> item.flammability * amount);
            float explosiveness = items.sum((item, amount) -> item.explosiveness * amount);
            float radioactivity = items.sum((item, amount) -> item.radioactivity * amount);
            float charge = items.sum((item, amount) -> item.charge * amount);

            font.draw(String.valueOf(flammability), x, y - 20, Align.center);
            font.draw(String.valueOf(explosiveness), x, y - 40, Align.center);
            font.draw(String.valueOf(radioactivity), x, y - 60, Align.center);
            font.draw(String.valueOf(charge), x, y - 80, Align.center);
        }

        @Override
        public BulletType useAmmo(){
            return peekAmmo();
        }

        @Override
        protected void shoot(BulletType type){
            super.shoot(type);
            items.clear();
        }

        @Override
        public boolean hasAmmo(){
            return totalAmmo >= minAmmo;
        }

        @Override
        public BulletType peekAmmo(){
            BulletType bulletType = bullet.copy();

            float flammability = items.sum((item, amount) -> item.flammability * amount * flammabilityMultiplier);
            float explosiveness = items.sum((item, amount) -> item.explosiveness * amount * explosivenessMultiplier);
            float radioactivity = items.sum((item, amount) -> item.radioactivity * amount * radioactivityMultiplier);
            float charge = items.sum((item, amount) -> item.charge * amount * chargeMultiplier);

            bulletType.fragBullet = new OvulamDynamicExplosionBulletType(flammability, explosiveness, radioactivity, charge);

            return bulletType;
        }

        @Override
        public byte version(){
            return 2;
        }

        @Override
        public boolean acceptItem(Building source, Item item){
            return items.get(item) < getMaximumAccepted(item);
        }
    }
}
