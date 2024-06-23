package Ovulam.entities;

import Ovulam.math.OvulamMath;
import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Angles;
import arc.math.Mathf;
import mindustry.entities.Effect;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.world.Block;

public class OvulamFx {
    public static final Effect
    none = new Effect(0, 0f, e -> {}),

    destroyTitanBlock = new Effect(200f, e -> {
        if (!(e.data instanceof Block block)) return;

        int index = Mathf.floor(e.time / 2f);
        float progress = e.time / 2f - index;

        Angles.randLenVectors(e.id + index, 1, 12, ((x1, y1) ->
                Angles.randLenVectors(e.id + index + 1, 1, 12, (x2, y2) -> {
                    float rx = e.x + Mathf.lerp(x1, x2, progress) * OvulamMath.fparabola(e.fin());
                    float ry = e.y + Mathf.lerp(y1, y2, progress) * OvulamMath.fparabola(e.fin());

                    Drawf.squareShadow(rx, ry, block.size * 8 * 1.85f, e.fout());

                    Draw.alpha(e.foutpow());
                    Draw.rect(block.fullIcon, rx, ry);
                    Draw.reset();

                    Draw.mixcol(Color.white, e.foutpow());
                    Draw.alpha(e.foutpow());
                    Draw.rect(block.fullIcon, e.x, e.y);

                    Draw.reset();
                })));
    }),

    //todo 相位碎块
    phaseFragment = new Effect(200f, e -> {
        if (!(e.data instanceof Building building)) return;
        int size = building.block.size;
        Angles.randLenVectors(e.id, size * 2, size * 8 / 4f, size * 8 / 2f, ((x, y) -> {
            Fill.circle(e.x + x, e.y + y, 4);
        }));
    });


}
