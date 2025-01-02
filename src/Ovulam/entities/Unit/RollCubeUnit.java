package Ovulam.entities.Unit;

import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;
import mindustry.entities.Units;
import mindustry.input.DesktopInput;
import mindustry.world.Tile;

import static mindustry.Vars.state;
import static mindustry.Vars.tilesize;

public class RollCubeUnit extends OvulamUnit{
    public boolean isRolling;
    public boolean hasInitRolling;
    public boolean zeroTarget = true;
    public int changeQuad;

    public float damageTimer;

    public float intervalTimer, rollingTimer;
    public float[] lengths = new float[6];

    public Vec2 target = new Vec2();

    public RollCubeUnitType getType(){
        return (RollCubeUnitType) type;
    }

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        //初始化每一次的滚动, 保证滚动时变量不会改变
        if(isRolling && !hasInitRolling){
            //目标速度向量为0时, 停止计时器和初始化, 不设置目标
            zeroTarget = vector.epsilonEquals(Vec2.ZERO, 0.01f);
            if(zeroTarget)return;

            if(!Vars.mobile && isPlayer())target.set(((DesktopInput)Vars.control.input).movement);
            else target.set(vector);

            float quad = target.angle() / 90 % 1;
            float change = getType().randomRoll ? quad : quad > 0.5f ? 1 : 0;
            changeQuad = Mathf.num(Mathf.randomBoolean(change));

            hasInitRolling = true;
        }
    }

    public float getProgress(){
        return Mathf.clamp(rollingTimer / getType().rollingTime, 0, 1);
    }

    public float getVel(){
        return getType().hitSize / getType().rollingTime;
    }

    public float reversal(float timer, float time){
        timer += Time.delta;
        if(timer > time){
            isRolling = !isRolling;
            hasInitRolling = false;

            timer = 0;
            changeQuad = -1;
        }
        return timer;
    }

    public int targetRot(){
        return Mathf.floor(target.angle() / 90) + changeQuad;
    }

    @Override
    public void update() {
        super.update();

        if(hasInitRolling) vel.trns(targetRot() * 90, getVel());
        else {
            zeroTarget = true;
            vel.setZero();
        }

        //位于(0,0)的点不需要移动
        for (int i = 1; i < 4; i++){
            Tmp.v1.set(i & 1, i >> 1 & 1);
            Tmp.v1.rotate(getProgress() * 90f);
            //以方块当前位置为中心, 因此要额外减掉滚动进度
            int index = (i - 1) * 2;
            lengths[index] = -(Tmp.v1.x - 0.5f + getProgress());
            lengths[index + 1] = Mathf.lerp(getType().bScl, 1, Tmp.v1.y);
        }

        //滚动并且目标速度不是0, 则进行滚动
        if(!isRolling) intervalTimer = reversal(intervalTimer, getType().rollInterval);
        else if(!zeroTarget) rollingTimer = reversal(rollingTimer, getType().rollingTime);

        if((damageTimer += Time.delta) > 5f){
            float radius = hitSize / tilesize / 2f;
            for(float dx = -radius; dx <= radius; dx++){
                for(float dy = -radius; dy <= radius; dy++){
                    Tile t = Vars.world.tileWorld(x + dx * tilesize, y + dy * tilesize);

                    if(type.crushDamage > 0 && t != null && t.build != null && t.build.team != team){
                        t.build.damage(team, type.crushDamage * 5f * t.block().crushDamageMultiplier * state.rules.unitDamage(team));
                    }
                }
            }

            Units.nearbyEnemies(team, x - radius, y - radius, radius*2f, radius*2f, (unit) -> {
                if(!unit.isFlying()) unit.damagePierce(type.crushDamage * 5f * state.rules.unitDamage(team));
            });
            damageTimer -= 5f;
        }
    }

    @Override
    public float deltaX() {
        return getType().deceiveAccurateDelay ? vel.x * getType().deceiveMulti : super.deltaX();
    }

    @Override
    public float deltaY() {
        return getType().deceiveAccurateDelay ? vel.y * getType().deceiveMulti : super.deltaY();
    }

}
