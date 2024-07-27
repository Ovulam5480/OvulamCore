package Ovulam.math;

import arc.math.Mathf;
import arc.math.geom.Vec3;

public class OvulamMath {
    //开口朝下的抛物线
    public static float fparabola(float x){
        return (-Mathf.sqr(x) + x) * 4;
    }

    //scale X倍率, 也可以看作函数在哪里回到0
    public static float fparabola(float x, float scale){
        if(x > scale)return 0;
        x = x * scale;
        return (-Mathf.sqr(x) + x) * 4;
    }

    //向量叉乘
    public static Vec3 crossProduct(Vec3 axis, Vec3 target){
        float x1 = axis.x;
        float y1 = axis.y;
        float z1 = axis.z;
        float x2 = target.x;
        float y2 = target.y;
        float z2 = target.z;;

        return new Vec3(y1 * z2 - y2 * z1, -(x1 * z2 - x2 * z1), x1 * y2 - x2 * z1);
    }

    //罗德里格旋转公式
    //等价于Vec3.rotate()
    public static Vec3 RodriguesRotationFormula(Vec3 axis, Vec3 target, float angle){
        Vec3 vec3 = new Vec3();
        vec3.mulAdd(target, Mathf.cosDeg(angle));
        vec3.add(crossProduct(axis, target).scl(Mathf.sinDeg(angle)));
        vec3.add(axis.cpy().scl(axis.cpy().dot(target)).scl(1 - Mathf.cosDeg(angle)));
        return vec3;
    }


}
