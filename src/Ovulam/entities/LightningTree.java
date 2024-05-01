package Ovulam.entities;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Angles;
import arc.math.Mathf;
import arc.math.Rand;
import arc.math.geom.Position;
import arc.math.geom.Vec2;
import arc.struct.IntSet;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.content.Bullets;
import mindustry.content.Items;
import mindustry.core.World;
import mindustry.entities.Effect;
import mindustry.entities.bullet.BulletType;
import mindustry.game.Team;
import mindustry.gen.Bullet;
import mindustry.world.Tile;

import static arc.math.Mathf.rand;
import static mindustry.Vars.tilesize;

public class LightningTree {
    //闪电树
    //其实就是原版的闪电换闪电链的特效
    //然后加上分支

    private static final Rand random = new Rand();
    private static final IntSet hit = new IntSet();
    private static final float hitRange = 30f;
    private static boolean bhit = false;
    public static float change = 0.4f;

    public static void create(float x, float y ,Team team, float damage, int amount, float range) {
        create(x, y, team, Items.surgeAlloy.color, Items.surgeAlloy.color, damage, amount, range);
    }

    public static void create(Bullet bullet , float damage, int amount, float range) {
        create(bullet, Items.surgeAlloy.color, Items.surgeAlloy.color, damage, amount, range);
    }

    public static void create(float x, float y ,Team team, Color roofColor, Color leafColor, float damage, int amount, float range) {
        Bullet bullet = Bullets.damageLightning.create(null, team, x, y, 0, 1, 1);
        create(bullet, x, y, team, roofColor, leafColor, damage, amount, range, Mathf.random(360), 30);
    }

    public static void create(Bullet bullet, Color roofColor, Color leafColor, float damage, int amount, float range) {
        create(bullet, bullet.x, bullet.y, bullet.team, roofColor, leafColor, damage, amount, range, Mathf.random(360),30);
    }

    public static void create(Bullet bullet, float x, float y ,Team team,
                              Color roofColor, Color leafColor,
                              float damage, int amount, float range, float rotation, float angle) {

        for (int i = 0; i < amount; i++){
            float rotation1 = (float) 360 / amount * i + rotation;
            Seq<Vec2> points = createLightningInternal(bullet, bullet.id + i, team, roofColor,
                    damage, x, y, rotation1, (int)range, 0.85f);

            points.each(vec2 -> {
                float indexFin = (float) points.indexOf(vec2) / points.size;
                if(indexFin > 0.2f && indexFin < 0.9f && Mathf.chance(change * (1 - indexFin))){

                    float angle1 = Mathf.angle(vec2.x - x,vec2.y - y);
                    //Fx.placeBlock.at(vec2.x, vec2.y, 1);

                    createLightningInternal(bullet, bullet.id, team, leafColor, damage, vec2.x, vec2.y,
                            angle1 + (Mathf.range(angle) + angle) * Mathf.num(Mathf.chance(0.5f)),
                            Math.round(range * (1 - indexFin)), 0.95f);
                }
            });
        }
    } 

    private static Seq<Vec2> createLightningInternal(
            Bullet hitter, int seed, Team team, Color color, float damage,
            float x, float y, float rotation, int length, float offset) {

        float rot = rotation;

        random.setSeed(seed);
        hit.clear();

        BulletType hitCreate = hitter.type.lightningType == null ? Bullets.damageLightning : hitter.type.lightningType;
        Seq<Vec2> lines = new Seq<>();
        bhit = false;

        for (int i = 0; i < length / 2; i++) {
            hitCreate.create(null, team, x, y, rotation, damage * hitter.damageMultiplier(),
                    1f, 1f, hitter);
            lines.add(new Vec2(x + Mathf.range(3f), y + Mathf.range(3f)));

            if (lines.size > 1) {
                bhit = false;
                Vec2 from = lines.get(lines.size - 2);
                Vec2 to = lines.get(lines.size - 1);
                World.raycastEach(World.toTile(from.getX()), World.toTile(from.getY()),
                        World.toTile(to.getX()), World.toTile(to.getY()), (wx, wy) -> {
                    Tile tile = Vars.world.tile(wx, wy);
                    if (tile != null && (tile.build != null && tile.build.isInsulated()) && tile.team() != team) {
                        bhit = true;
                        lines.get(lines.size - 1).set(wx * tilesize, wy * tilesize);
                        return true;
                    }
                    return false;
                });
                if (bhit) break;

                chainLightning.at(from.x, from.y, Mathf.sqr(1 - (float) i / length), color, to);
            }

            rotation = (Mathf.range(20f) + rotation) * offset + rot * (1 - offset);
            x += Angles.trnsx(rotation, hitRange / 2f);
            y += Angles.trnsy(rotation, hitRange / 2f);
        }
        return lines;
    }

    static Effect chainLightning = new Effect(40f, 300f, e -> {
        if(!(e.data instanceof Position p)) return;
        float tx = p.getX(), ty = p.getY(), dst = Mathf.dst(e.x, e.y, tx, ty);
        Tmp.v1.set(p).sub(e.x, e.y).nor();

        float normx = Tmp.v1.x, normy = Tmp.v1.y;
        float range = 6f;
        int links = Mathf.ceil(dst / range);
        float spacing = dst / links;

        Lines.stroke(5f * e.fout() * e.rotation);
        Draw.color(Color.white, e.color, e.fin());

        Lines.beginLine();

        Lines.linePoint(e.x, e.y);

        rand.setSeed(e.id);

        for(int i = 0; i < links; i++){
            float nx, ny;
            if(i == links - 1){
                nx = tx;
                ny = ty;
            }else{
                float len = (i + 1) * spacing;
                Tmp.v1.setToRandomDirection(rand).scl(range / 2f);
                nx = e.x + normx * len + Tmp.v1.x;
                ny = e.y + normy * len + Tmp.v1.y;
            }

            Lines.linePoint(nx, ny);
        }

        Lines.endLine();
    }).followParent(false).rotWithParent(false);

}
