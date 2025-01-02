package Ovulam.No9527垃圾堆;

import mindustry.content.Liquids;
import mindustry.type.Liquid;
import mindustry.world.blocks.production.GenericCrafter;

/*
 *@Author:LYBF
 *@Date  :2024/3/8
 *@Desc  :
 * 核心工厂 直接产出物品至核心
 * 直接输出Items至核心,拒绝中间商赚差价
 */
public class CoreLiquidGenericCrafter extends GenericCrafter {
    public CoreLiquidGenericCrafter(String name) {
        super(name);
        hasLiquids = true;
        liquid=Liquids.water;
        time=1.2f;
    }
    public Liquid liquid;
    public float time;

    @Override
    public void setBars(){
        super.setBars();
        addLiquidBar(liquid);
    }


    public class CoreGenericCrafterBuilding extends GenericCrafterBuild {
        public float timer;

        @Override
        public void updateTile() {
            if(timer < time){
                timer += delta() * efficiency;
            }

            if (hasLiquids && (!hasPower || power.status > 0) && timer >= time) {
                float amount = Math.max(liquidCapacity - liquids.get(liquid), 0);
                liquids.add(liquid, amount/2f);
            }
            super.updateTile();
        }
    }
}
