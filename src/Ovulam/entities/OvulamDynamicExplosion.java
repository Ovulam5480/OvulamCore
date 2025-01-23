package Ovulam.entities;

import Ovulam.entities.bullet.PierceContinuousBulletType;
import Ovulam.world.type.ItemAttributes;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.content.Items;
import mindustry.entities.Damage;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.graphics.Pal;

public class OvulamDynamicExplosion {
    public Effect highlight = new Effect(120, e -> {
        Draw.color(Pal.accent);
        Draw.alpha(e.fout());
        Lines.circle(e.x, e.y, e.rotation);
    });

    public OvulamDynamicExplosion(float x, float y, @Nullable Team team, ItemAttributes attributes){
        this(x, y, team, attributes.flammability, attributes.explosiveness, attributes.radioactivity, attributes.charge);
    }

    public OvulamDynamicExplosion(float x, float y, @Nullable Team team,
                                  float flammability, float explosiveness,
                                  float radioactivity, float charge) {
        //todo 非常需要平衡!!
        BulletType fire = new PierceContinuousBulletType(){{
            damage = flammability * 100 / 1200;
            lifetime = 1200;
            hitColor = Items.pyratite.color;
            trailColor = Items.pyratite.color;
            speed = 2f;
            homingPower = 2.5f;
            homingRange = 30;
        }};

        int amount = Mathf.ceil(Mathf.pow(flammability, 1f/6));

        for (int i = 0; i < amount; i++){
            fire.create(null, team, x, y, i * 360f / amount + Mathf.range(90));
        }


        //explosiveness
        Angles.randLenVectors((long) Mathf.random(x + y + explosiveness), (int) Mathf.pow(explosiveness, 1f / 4) + 2,
                Mathf.pow(explosiveness, 0.6f), (x1, y1) -> {

                    Damage.damage(team, x + x1, y + y1, Mathf.pow(explosiveness, 0.45f),
                            explosiveness * 20, false);
                    highlight.at(x + x1, y + y1, Mathf.pow(explosiveness, 0.45f));
                });


        //radioactivity
        Damage.damage(team, x, y, Mathf.pow(radioactivity, 0.65f), radioactivity * 60, true);
        highlight.at(x, y, Mathf.pow(radioactivity, 0.65f));


        //charge
        LightningTree.create(x, y, team, Mathf.sqrt(charge) * 50, (int) Mathf.pow(charge, 1f / 4) + 4, Mathf.pow(charge, 1f / 3) * 3);

    }

}
