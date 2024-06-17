package Ovulam.world.No9527;

import mindustry.entities.units.WeaponMount;
import mindustry.gen.Unit;
import mindustry.type.Weapon;

public class 平行线weapon extends Weapon {
    public 平行线weapon(String name){
        super(name);
    }
    @Override
    protected float bulletRotation(Unit unit, WeaponMount mount, float bulletX, float bulletY){
        return unit.rotation + mount.rotation;
    }
}
