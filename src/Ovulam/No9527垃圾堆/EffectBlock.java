package Ovulam.No9527垃圾堆;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.math.Mathf;
import arc.struct.FloatSeq;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.type.Category;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.world.Block;
import mindustry.world.Tile;

import static arc.math.Angles.randLenVectors;

public class EffectBlock extends Block {
    public float etime = 150f;
    public EffectBlock(String name) {
        super(name);
        requirements(Category.defense, new ItemStack[]{});
        update = true;
        sync = true;
        clipSize = 800f;
    }

    public Block target = null;

    public boolean canReplace(Block other){
        if(other.alwaysReplace) return true;
        return target == null ? super.canReplace(other) : target == other;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        if(tile == null) return false;
        if(Vars.state.isEditor() || target == null) return true;

        tile.getLinkedTilesAs(this, tempTiles);
        return tempTiles.contains(o -> o.block() == target);
    }

    public class EffectBuild extends Building {
        public float etimer = 0;

        public int 数量 = 6;
        public float 特效粒子的范围 = 120f;
        public float 特效粒子外径 = 120;
        //虽然规定内外径，但是并没有内径必须比外径小的必要
        public float 特效粒子内径 = 12;
        //这里填X角星,五角星就填5，四角星就填4
        public int X角星 = 16;

        public FloatSeq 星星图形(float x, float y, float out, float in, int side, float rotation){
            FloatSeq floatSeq = new FloatSeq(side * 4 + 4);
            floatSeq.add(x, y);

            for (int i = 0; i < side; i++){
                float pointRotation = rotation + 360f / side * i;
                floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * pointRotation)),
                        y + (float)(out * Math.sin(Mathf.degreesToRadians * pointRotation)));
                floatSeq.add((float) (x + in * Math.cos(Mathf.degreesToRadians * (pointRotation + 180f / side))),
                        y + (float)(in * Math.sin(Mathf.degreesToRadians * (pointRotation + 180f / side))));
            }

            floatSeq.add((float) (x + out * Math.cos(Mathf.degreesToRadians * rotation)),
                    y + (float)(out * Math.sin(Mathf.degreesToRadians * rotation)));

            return floatSeq;
        }

        public Effect 四角星 = new Effect(120, e -> {
            Draw.color(Color.pink);
            randLenVectors(e.id, 数量, 特效粒子的范围, (x, y) -> {
                float angle = Mathf.angle(x, y);
                Fill.poly(星星图形(e.finpow() * x + e.x, e.finpow() * y + e.y,
                        特效粒子外径 * (1 - Mathf.sqr(1 - e.fslope())),
                        特效粒子内径 * (1 - Mathf.sqr(1 - e.fslope())), X角星, angle));
            });
        });

        @Override
        public void updateTile() {
            etimer += Time.delta;
            if(etime > etimer) return;

            四角星.at(x,y);
            etimer -= etime;

            Vars.state.teams.get(Team.sharded).units.each(unit -> unit.elevation(1));
        }

        @Override
        public void draw() {
            Font font = Fonts.outline;
            font.draw(String.valueOf(etimer), x, y - 20, Align.center);
            font.draw(String.valueOf(星星图形(x, y, 32, 16, 6, 0).size), x, y - 40, Align.center);
        }
    }
}
