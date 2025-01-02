package Ovulam.No9527垃圾堆;

import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.entities.Effect;
import mindustry.entities.Units;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.type.StatusEffect;
import mindustry.world.Block;
import mindustry.world.blocks.storage.CoreBlock;
import mindustry.world.meta.Stat;
import mindustry.world.meta.StatValues;

import static mindustry.Vars.tilesize;

public class BUFF投影 extends Block {
    public ItemStack[] itemStacks;
    //施加BUFF的持续世界
    public float duration = 60f;
    //消耗间隔
    public float reload = 60f;
    public StatusEffect effect;
    public Effect applyEffect = Fx.none;
    public Effect activeEffect = Fx.overdriveWave;
    public float range = 60f;

    public BUFF投影(String name){
        super(name);
        solid = true;
        update = true;
        sync = true;
        hasItems = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(Stat.input, StatValues.items(reload, itemStacks));

    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        super.drawPlace(x, y, rotation, valid);

        Drawf.dashCircle(x * tilesize + offset, y * tilesize + offset, range, Pal.accent);
    }

    public class BUFF投影Build extends Building {
        public float timer;

        public void draw(){
            super.draw();
            Drawf.circles(x, y, range, Pal.accent);
        }

        @Override
        public void updateTile(){
            CoreBlock.CoreBuild coreBuild = Vars.state.rules.defaultTeam.core();
            if(coreBuild.items.has(itemStacks))timer += delta();
            if(efficiency > 0){
                Units.nearby(team, x, y, range, other -> {
                    other.apply(effect, duration);
                    applyEffect.at(other);
                });
            }
            if(timer > reload){
                timer -= reload;
                activeEffect.at(x, y, range);
                coreBuild.items.remove(itemStacks);
            }
        }


        @Override
        public void drawSelect(){
            Drawf.dashCircle(x, y, range, Pal.accent);
        }
    }
}
