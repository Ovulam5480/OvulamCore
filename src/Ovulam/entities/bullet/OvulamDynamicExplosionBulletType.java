package Ovulam.entities.bullet;

import Ovulam.entities.OvulamDynamicExplosion;
import Ovulam.world.type.ItemAttributes;
import arc.struct.ObjectMap;
import arc.util.Nullable;
import mindustry.entities.Mover;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.gen.Entityc;
import mindustry.gen.Teamc;
import mindustry.world.meta.Attribute;

public class OvulamDynamicExplosionBulletType extends BulletType {
    public OvulamDynamicExplosionBulletType(){
        lifetime = 0f;
        speed = 0f;
    }

    @Override
    public @Nullable Bullet create(Bullet parent, float x, float y, float angle){
        Bullet bullet = create(parent.owner, parent.team, x, y, angle);
        if(parent.data instanceof ItemAttributes ia)bullet.data(ia);
        return bullet;
    }

    @Override
    public @Nullable Bullet create(Bullet parent, float x, float y, float angle, float velocityScl, float lifeScale){
        Bullet bullet = create(parent.owner, parent.team, x, y, angle, velocityScl, lifeScale);
        if(parent.data instanceof ItemAttributes ia)bullet.data(ia);
        return bullet;
    }

    @Override
    public @Nullable Bullet create(Bullet parent, float x, float y, float angle, float velocityScl){
        Bullet bullet = create(parent.owner(), parent.team, x, y, angle, velocityScl);
        if(parent.data instanceof ItemAttributes ia)bullet.data(ia);
        return bullet;
    }

    @Override
    public void despawned(Bullet b){
        if(b.data instanceof ItemAttributes ia)new OvulamDynamicExplosion(b.x, b.y, b.team, ia);
        super.despawned(b);
    }
}
