package Ovulam.entities.Unit;

import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;

public class RotationalCubeUnit extends OvulamUnit{
    public Vec3 axis;
    public float rot3D;
    public float cubeRadius;
    public float rotationMulti;

    public Seq<Vec3> vec3s = Seq.with(
            new Vec3(),new Vec3(),new Vec3(),new Vec3(),
            new Vec3(),new Vec3(),new Vec3(),new Vec3());


    @Override
    public void update() {
        super.update();
        rot3D += vel.len() * Time.delta * rotationMulti;

        for (int i = 0; i < 8; i++){
            float rx = i >> 2 & 1;
            float ry = i >> 1 & 1;
            float rz = i & 1;
            vec3s.get(i).set(rx, ry, rz).scl(2).sub(1).rotate(axis, rot3D).scl(cubeRadius).add(x,y,0);
        }
    }


}
