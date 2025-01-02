package Ovulam.entities.Unit;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import mindustry.ai.ControlPathfinder;
import mindustry.gen.Unit;

public class RollCubeUnitType extends OvulamUnitType {
    //第一次滚动结束, 到第二次滚动开始的 时间间隔,
    public float rollInterval = 20f;
    //滚动完成总共的时间
    public float rollingTime = 20f;
    //是否根据目标的位置进行随机滚动
    //todo 待测试
    public boolean randomRoll = true;

    public float bScl = 0.8f;

    private final int[] rgb = new int[]{1, 1, 0, 0};

    public boolean deceiveAccurateDelay;
    public float deceiveMulti;

    public RollCubeUnitType(String name) {
        super(name);
        drawCell = false;
        rotateMoveFirst = false;
        omniMovement = false;
        pathCost = ControlPathfinder.costHover;

        allowLegStep = true;
        hovering = true;
        //每5刻造成一次伤害, 每次造成五倍该值的伤害
        crushDamage = 2f;

        rotateSpeed = 100;
        accel = 1000;

    }

    @Override
    public void init() {
        //用于数据库
        speed = hitSize / (rollingTime + rollInterval);
    }

    @Override
    public void drawBody(Unit unit) {
        if (!(unit instanceof RollCubeUnit r)) return;

        int quad = r.targetRot();

        float d4xS = Geometry.d4x(quad) * hitSize;
        float d4yS = Geometry.d4y(quad) * hitSize;

        applyColor(unit);
        Draw.z(Draw.z() + 0.1f);

        if (r.isRolling && !r.zeroTarget) {
            Draw.mixcol(getRGB(r.targetRot()), r.getProgress() * 0.7f);
            //这张贴图代表滚动前直接面朝玩家的面
            Fill.quad(region,
                    d4xS * r.lengths[4] + d4yS / 2f * r.lengths[5] + unit.x,
                    d4yS * r.lengths[4] + d4xS / 2f * r.lengths[5] + unit.y,
                    d4xS * r.lengths[4] - d4yS / 2f * r.lengths[5] + unit.x,
                    d4yS * r.lengths[4] - d4xS / 2f * r.lengths[5] + unit.y,
                    d4xS * r.lengths[2] - d4yS / 2f * r.lengths[3] + unit.x,
                    d4yS * r.lengths[2] - d4xS / 2f * r.lengths[3] + unit.y,
                    d4xS * r.lengths[2] + d4yS / 2f * r.lengths[3] + unit.x,
                    d4yS * r.lengths[2] + d4xS / 2f * r.lengths[3] + unit.y
            );
            Draw.mixcol(getRGB(r.targetRot() + 2), (1 - r.getProgress()) * 0.7f);
            Fill.quad(region,
                    d4xS * r.lengths[0] + d4yS / 2f * r.lengths[1] + unit.x,
                    d4yS * r.lengths[0] + d4xS / 2f * r.lengths[1] + unit.y,
                    d4xS * r.lengths[0] - d4yS / 2f * r.lengths[1] + unit.x,
                    d4yS * r.lengths[0] - d4xS / 2f * r.lengths[1] + unit.y,
                    d4xS * r.lengths[4] - d4yS / 2f * r.lengths[5] + unit.x,
                    d4yS * r.lengths[4] - d4xS / 2f * r.lengths[5] + unit.y,
                    d4xS * r.lengths[4] + d4yS / 2f * r.lengths[5] + unit.x,
                    d4yS * r.lengths[4] + d4xS / 2f * r.lengths[5] + unit.y
            );
            Draw.mixcol();
        } else {
            Draw.rect(region, unit.x, unit.y);
        }

        Draw.reset();
    }

    public Color getRGB(int rot){
        return new Color(rgb[Mathf.mod(rot, 4)], rgb[Mathf.mod(rot, 4)], rgb[Mathf.mod(rot, 4)]);
    }

    @Override
    public void drawShadow(Unit unit) {
        super.drawShadow(unit);
    }

}
