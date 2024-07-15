package Ovulam.No9527垃圾堆;

import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.entities.bullet.BulletType;
import mindustry.gen.Building;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Bar;
import mindustry.world.blocks.defense.turrets.Turret;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

public class 扣核心资源ItemTurret extends Turret {
    public ItemStack[] itemStacks;
    public BulletType shootType;
    public 扣核心资源ItemTurret(String name) {
        super(name);
        //弹药上限
        maxAmmo = 5;
    }

    @Override
    public void setStats(){
        super.setStats();
        stats.add(Stat.ammo, StatValues.items(false, itemStacks));
        stats.add(Stat.ammo, StatValues.ammo(ObjectMap.of(this, shootType)));
    }

    @Override
    public void setBars(){
        super.setBars();

        addBar("ammo", (扣核心资源ItemTurretBuild entity) ->
                new Bar(
                        "stat.ammo",
                        Pal.ammo,
                        () -> (float)entity.totalAmmo / maxAmmo
                )
        );
    }

    public class 扣核心资源ItemTurretBuild extends TurretBuild{
        @Override
        public void updateTile(){
            unit.ammo((float)unit.type().ammoCapacity * totalAmmo / maxAmmo);

            if(totalAmmo < maxAmmo){
                Building core = Vars.state.teams.get(team).core();
                if(core != null && core.items.has(itemStacks)){
                    core.items.remove(itemStacks);
                    totalAmmo ++;
                }
            }

            super.updateTile();
        }

        @Override
        public BulletType useAmmo(){
            totalAmmo --;
            return shootType;
        }

        @Override
        public boolean hasAmmo(){
            return totalAmmo > 0;
        }

        @Override
        public BulletType peekAmmo(){
            return shootType;
        }
    }
}
