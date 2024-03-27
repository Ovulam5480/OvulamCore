package Ovulam.type;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.util.Nullable;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.graphics.Pal;

public class OvulamDynamicExplosion {
    public Effect highlight = new Effect(120, e -> {
        Draw.color(Pal.accent);
        Draw.alpha(e.fout());
        Lines.circle(e.x, e.y, e.rotation);
    });

    public OvulamDynamicExplosion(float x, float y, @Nullable Team team,
                                  float flammability, float explosiveness,
                                  float radioactivity, float charge) {
        //物品形式的爆炸性，1 爆炸性理论提供12.5伤害的50范围范围伤害
        //弹药形式的爆炸性，1 爆炸性为蜂群提供总共100伤害(对蜂群在日蚀内攻击，蜂群在日蚀外无法造成范围伤害)的30范围范围伤害
        /*
        if (radioactivity > explosiveness) {
            Damage.damage(team, x, y, Mathf.pow(explosiveness + radioactivity, 0.65f),
                    radioactivity * 60 + explosiveness * 80, true);
            highlight.at(x, y, Mathf.pow(radioactivity, 0.65f));
        } else {
            Angles.randLenVectors((long) Mathf.random(x + y), (int) Mathf.pow(explosiveness, 1f / 4) + 2,
                    Mathf.pow(radioactivity, 0.6f), (x1, y1) -> {

                        Damage.damage(team, x + x1, y + y1, Mathf.pow(explosiveness + radioactivity, 0.45f),
                                (radioactivity * 10 + explosiveness * 20), false);
                        highlight.at(x + x1, y + y1, Mathf.pow(explosiveness, 0.45f));
                    });
        }

         */
        //放电性 效果未知
        LightningTree.create(x, y, team, Mathf.sqrt(charge)*50, (int) Mathf.pow(charge, 1f / 4) + 4, Mathf.pow(charge, 1f / 3) * 3);

    }

}
