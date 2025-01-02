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
}
