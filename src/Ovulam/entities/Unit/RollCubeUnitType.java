package Ovulam.entities.Unit;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Font;
import arc.math.geom.Geometry;
import arc.util.Align;
import mindustry.ai.ControlPathfinder;
import mindustry.gen.Unit;
import mindustry.ui.Fonts;

public class RollCubeUnitType extends OvulamUnitType {
    //第一次滚动结束, 到第二次滚动开始的 时间间隔,
    public float rollInterval = 20f;
    //滚动完成总共的时间
    public float rollingTime = 20f;
    //是否根据目标的位置进行随机滚动
    //todo 待测试
    public boolean randomRoll = true;

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

        int quad = r.getChangeQuad();

        float d4xH = Geometry.d4x(quad) * hitSize;
        float d4yH = Geometry.d4y(quad) * hitSize;

        applyColor(unit);

        if (r.isRolling) {
            Fill.quad(region,
                    d4xH * r.drawXs[2] + d4yH / 2f + unit.x,
                    d4yH * r.drawXs[2] + d4xH / 2f + unit.y,
                    d4xH * r.drawXs[2] - d4yH / 2f + unit.x,
                    d4yH * r.drawXs[2] - d4xH / 2f + unit.y,
                    d4xH * r.drawXs[1] - d4yH / 2f + unit.x,
                    d4yH * r.drawXs[1] - d4xH / 2f + unit.y,
                    d4xH * r.drawXs[1] + d4yH / 2f + unit.x,
                    d4yH * r.drawXs[1] + d4xH / 2f + unit.y
            );
            Fill.quad(region,
                    d4xH * r.drawXs[0] + d4yH / 2f + unit.x,
                    d4yH * r.drawXs[0] + d4xH / 2f + unit.y,
                    d4xH * r.drawXs[0] - d4yH / 2f + unit.x,
                    d4yH * r.drawXs[0] - d4xH / 2f + unit.y,
                    d4xH * r.drawXs[2] - d4yH / 2f + unit.x,
                    d4yH * r.drawXs[2] - d4xH / 2f + unit.y,
                    d4xH * r.drawXs[2] + d4yH / 2f + unit.x,
                    d4yH * r.drawXs[2] + d4xH / 2f + unit.y
            );
        } else {
            Draw.rect(region, unit.x, unit.y);
        }

        Draw.reset();
    }

    //用于测试
    public void show(Unit unit) {
        Font font = Fonts.outline;
        if (!(unit instanceof RollCubeUnit r)) return;
        font.draw("滚动时间 : " + r.rollingTimer, unit.x, unit.y - 40, Align.center);
        font.draw("间隔时间 : " + r.intervalTimer, unit.x, unit.y - 60, Align.center);
        font.draw("滚动中 : " + r.isRolling, unit.x, unit.y - 80, Align.center);
        font.draw("是否无目标 : " + r.pauseRolling, unit.x, unit.y - 100, Align.center);
    }
}
