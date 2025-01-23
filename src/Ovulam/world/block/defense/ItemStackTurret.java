package Ovulam.world.block.defense;

import Ovulam.entities.bullet.OvulamDynamicExplosionBulletType;
import Ovulam.world.type.ItemAttributes;
import mindustry.content.Fx;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.ui.Bar;
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
        separateItemCapacity = false;

        shootEffect = Fx.shootBig;
    }


    @Override
    public void init() {
        itemCapacity = maxAmmo;

        bullet.fragBullet = new OvulamDynamicExplosionBulletType();
        bullet.fragBullets = 1;

        limitRange(bullet, 0f);

        super.init();
    }

    @Override
    public void setBars() {
        super.setBars();

        addBar("ammo", (ItemStackTurretBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float) entity.totalAmmo / maxAmmo
                )
        );
    }

    @Override
    public void limitRange(BulletType bullet, float margin) {
        super.limitRange(bullet, margin);
    }

    public class ItemStackTurretBuild extends TurretBuild {

        @Override
        public void updateTile() {
            totalAmmo = items.total();

            unit.ammo((float) unit.type().ammoCapacity * totalAmmo / maxAmmo);

            super.updateTile();
        }

        @Override
        public BulletType peekAmmo() {
            return bullet;
        }

        @Override
        public BulletType useAmmo() {
            return peekAmmo();
        }

        @Override
        protected void shoot(BulletType type) {
            super.shoot(type);
            items.clear();
        }

        @Override
        public boolean hasAmmo() {
            return totalAmmo >= minAmmo;
        }

        @Override
        protected void handleBullet(Bullet bullet, float offsetX, float offsetY, float angleOffset) {
            float flammability = items.sum((item, amount) -> item.flammability * amount) * flammabilityMultiplier;
            float explosiveness = items.sum((item, amount) -> item.explosiveness * amount) * explosivenessMultiplier;
            float radioactivity = items.sum((item, amount) -> item.radioactivity * amount) * radioactivityMultiplier;
            float charge = items.sum((item, amount) -> item.charge * amount) * chargeMultiplier;

            bullet.data(new ItemAttributes(flammability, explosiveness, radioactivity, charge));
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return items.get(item) < getMaximumAccepted(item);
        }
    }
}
