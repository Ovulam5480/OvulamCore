package Ovulam.No9527垃圾堆;

import arc.math.geom.Rect;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.Item;
import mindustry.type.Liquid;
import mindustry.world.Block;
import mindustry.world.blocks.liquid.LiquidRouter;
import mindustry.world.blocks.production.GenericCrafter;
import mindustry.world.blocks.production.Separator;

import static mindustry.Vars.tilesize;

public class liq发散器 extends Block {
    public float range = 20f;
    public float 输入阈值 = 0.7f;
    public liq发散器(String name) {
        super(name);
        hasLiquids = true;
        //最大传输速率与液体容量上限相关
        liquidCapacity = 1000;
        destructible = true;
        canOverdrive = false;
        update = true;
    }

    @Override
    public void setBars() {
        super.setBars();
        removeBar("liquid");
    }

    public Rect rangeRect(float x, float y, float range) {
        return Tmp.r1.set(x - range, y - range, range * 2, range * 2);
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Drawf.dashRect(Pal.accent, rangeRect(x * 8 + offset, y * 8 + offset, range * tilesize));
    }

    public class liq发散器Building extends Building {

        public boolean vailBuild(Building target, Liquid liquid){
            //eff
            return target.block.hasLiquids
                    //不包括分离器
                    && (target.block instanceof GenericCrafter || target.block instanceof LiquidRouter)
                    && target.acceptLiquid(this, liquid)
                    && target.liquids.get(liquid) < target.block.liquidCapacity * 输入阈值
                    && (!hasConsumers || efficiency == 1);
        }

        @Override
        public boolean acceptLiquid(Building source, Liquid liquid) {
            return Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, liquid) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y)) != null;

        }

        @Override
        public void handleLiquid(Building source, Liquid liquid, float amount) {
            Building target = Vars.indexer.findTile(team, x, y, range * tilesize * 2, b -> vailBuild(b, liquid) && rangeRect(x, y, range * tilesize).grow(0.1f).contains(b.x, b.y));
            amount = Math.min(amount, target.block.liquidCapacity - target.liquids.get(liquid));
            target.handleLiquid(this, liquid, amount);
        }

        @Override
        public void drawSelect() {
            Drawf.dashRect(Pal.accent, rangeRect(x, y, range * tilesize));
        }

    }
}
