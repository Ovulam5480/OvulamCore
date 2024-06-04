package Ovulam.entities.unit;

import Ovulam.entities.OvulamEventType;
import arc.Core;
import arc.Events;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.util.Structs;
import mindustry.game.Team;
import mindustry.gen.Unit;
import mindustry.gen.UnitEntity;
import mindustry.type.UnitType;

import java.lang.reflect.Constructor;

public class OvulamUnitType extends UnitType {
    //固定伤害减免
    public float damageReduction = 0;
    //复活次数
    public int rebirthTime;
    //损坏的贴图数量
    public int wornStage = 0;
    ////////////////////
    //从0到stage, 单位的血量降至0
    public TextureRegion[] wornRegion;
    public OvulamUnitType(String name) {
        super(name);
        initUnit();
    }

    @SuppressWarnings("unchecked")
    protected void initUnit(){
        try{
            Class<?> current = getClass();
            if(current.isAnonymousClass()){
                current = current.getSuperclass();
            }

            Class<?> type = Structs.find(current.getDeclaredClasses(), t -> Unit.class.isAssignableFrom(t) && !t.isInterface());
            if(type != null){
                Constructor<? extends Unit> cons = (Constructor<? extends Unit>)type.getDeclaredConstructor(type.getDeclaringClass());
                constructor = () -> {
                    try{
                        return cons.newInstance(this);
                    }catch(Exception e){
                        throw new RuntimeException(e);
                    }
                };
            }
        }catch(Throwable ignored){}
    }

    @Override
    public void load(){
        super.load();

        if(wornStage > 0){
            wornRegion = new TextureRegion[wornStage];
            for (int i = 0; i < wornStage; i++) {
                wornRegion[i] = Core.atlas.find(name + "-stage-" + i);
            }
        }
    }

    @Override
    public void drawBody(Unit unit){
        //只有在最后一段生命显示损坏贴图
        if(wornStage > 0 || ((OvulamUnit)unit).rebirthStage < rebirthTime)super.drawBody(unit);
        else {
            int per = Mathf.floor(unit.health / unit.maxHealth * wornStage);
            Draw.rect(wornRegion[per], unit.x, unit.y, unit.rotation - 90);
        }
    }

    public class OvulamUnit extends UnitEntity{
        public int rebirthStage;
        public boolean resurrecting = false;

        //减免固定伤害, 包括穿甲伤害
        @Override
        public void rawDamage(float amount) {
            amount = Math.max(amount - damageReduction, 0);
            super.rawDamage(amount);
        }

        @Override
        public boolean hittable() {
            if (resurrecting) return false;
            return super.hittable();
        }

        @Override
        public boolean targetable(Team targeter) {
            if (resurrecting) return false;
            return super.targetable(targeter);
        }

        @Override
        public void kill() {
            if (rebirthStage < rebirthTime) {
                Events.fire(new OvulamEventType.invitationResurrection(this));
                resurrecting = true;
                rebirthStage++;
                health = 1f;
            } else {
                super.kill();
            }
        }

        public void resurrection() {
            health = Mathf.approachDelta(health, maxHealth, maxHealth * 0.003f);
            if (Mathf.equal(health, maxHealth)) resurrecting = false;
        }

        @Override
        public void update() {
            if (resurrecting) resurrection();
            super.update();
        }
    }
}
