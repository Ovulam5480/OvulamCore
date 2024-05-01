package Ovulam.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import mindustry.entities.Effect;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;

public class OvulamFx {
    public static final Effect
    none = new Effect(0, 0f, e -> {}),

    destroyTitanBlock = new Effect(200f, e -> {
        if(!(e.data instanceof Block block)) return;

        int index = Mathf.floor(e.time / 2f);
        float progress = e.time / 2f - index;

        Vec2 from = new Vec2();
        Angles.randLenVectors(e.id + index, 1, 12, from::add);

        Vec2 to = new Vec2();
        Angles.randLenVectors(e.id + index + 1, 1, 12, to::add);

        float rx = e.x + Mathf.lerp(from.x, to.x, progress) * e.foutpow();
        float ry = e.y + Mathf.lerp(from.y, to.y, progress) * e.foutpow();

        Draw.z(Layer.block);
        Drawf.squareShadow(rx, ry, block.size * 8 * 1.85f, e.foutpow());

        Draw.alpha(e.foutpow());
        Draw.rect(block.fullIcon, rx, ry);

        Draw.reset();

        Draw.mixcol(Color.white, e.fout());
        Draw.alpha(e.fout());
        Draw.rect(block.fullIcon, e.x, e.y);

        Draw.reset();
    });
}
