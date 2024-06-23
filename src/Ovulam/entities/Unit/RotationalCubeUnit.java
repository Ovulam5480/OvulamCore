package Ovulam.entities.Unit;

import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;

public class RotationalCubeUnit extends OvulamUnit{
    public Vec3 axis;
    public float rot3D;
    public float cubeRadius;
    public float rotationMulti;

    public Seq<Vec3> vec3s = Seq.with(new Vec3(),new Vec3(),new Vec3(),new Vec3(), new Vec3(),new Vec3(),new Vec3(),new Vec3());


    @Override
    public void update() {
        super.update();
        rot3D += vel.len() * Time.delta * rotationMulti;

        //todo 二进制
        int index = 0;
        for (int i = -1; i <= 1; i += 2){
            for (int j = -1; j <= 1; j += 2){
                for (int k = -1; k <= 1; k += 2){
                    vec3s.get(index).set(i,j,k).rotate(axis, rot3D).scl(cubeRadius).add(x,y,0);
                    index++;
                }
            }
        }
    }

}
