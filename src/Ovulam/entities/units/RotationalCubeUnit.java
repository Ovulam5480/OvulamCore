package Ovulam.entities.units;

import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.io.Reads;
import arc.util.io.Writes;

public class RotationalCubeUnit extends OvulamUnit{
    public Vec3 axis = new Vec3();
    public float rot3D;

    public Vec2 target = new Vec2();
    public float len, passed;
    public int section;

    public float chance;

    private final static Seq<Vec3> points = Seq.with(
            new Vec3(1,1,1),
            new Vec3(1,1,-1),
            new Vec3(1,-1,1),
            new Vec3(1,-1,-1),
            new Vec3(-1,1,1),
            new Vec3(-1,1,-1),
            new Vec3(-1,-1,1),
            new Vec3(-1,-1,-1));
    public Seq<Vec3> vec3s = Seq.with(
            new Vec3(),new Vec3(),new Vec3(),new Vec3(),
            new Vec3(),new Vec3(),new Vec3(),new Vec3());

    @Override
    public void write(Writes write) {
        super.write(write);
        write.f(axis.x);
        write.f(axis.y);
        write.f(axis.z);
    }

    @Override
    public void read(Reads read) {
        super.read(read);
        axis.set(read.f(), read.f(), read.f());
    }

    public RotationalCubeUnitType asType(){
        return (RotationalCubeUnitType) type;
    }

    public float cTile(){
        return asType().cTile;
    }

    @Override
    public void moveAt(Vec2 vector, float acceleration) {
        if (passed >= len && !vector.epsilonEquals(Vec2.ZERO, 0.01f)) {
            section = (int) vector.angle() / 90;
            float c = vector.angle() % 90;

            if(Mathf.randomBoolean((c + chance) / 90f)){
                section++;
                chance = 0;
            }else chance += c;

            if(asType().randomRoll && !isPlayer()){
                if(Mathf.randomBoolean(0.1f))section++;
                else if(Mathf.randomBoolean(0.1f))section--;
                else if(Mathf.randomBoolean(0.05f))section = section + 2;
            }

            target.set(Geometry.d4x(section), Geometry.d4y(section));

            passed = 0;

            if(section % 2 == 0)len = Math.abs(toCTile(target.x * cTile() + x, cTile()) * cTile() - x);
            else len = Math.abs(toCTile(target.y * cTile() + y, cTile()) * cTile() - y);
        }
    }

    public static int toCTile(float coord, float cTile){
        return Math.round(coord / cTile);
    }

    @Override
    public void update() {
        super.update();
        if(passed < len){
            vel.set(target).setLength(speed());
            passed += speed() * Time.delta;

            if(passed >= len){
                asType().effect.at(toCTile(x, cTile()) * cTile(), toCTile(y, cTile()) * cTile(), cTile());
                vel.setZero();

                //for (WeaponMount mount : mounts()) {}
            }
        }

        rot3D += (vel.len() + 0.2f) * Time.delta * asType().rotationMulti;

        for (int i = 0; i < 8; i++){
            vec3s.get(i).set(points.get(i)).rotate(axis, rot3D).scl(1 + Mathf.sin(160f, 0.1f));
        }
    }

    @Override
    public float deltaX() {
        return asType().deceiveAccurateDelay ? vel.x * asType().deceiveMulti : super.deltaX();
    }

    @Override
    public float deltaY() {
        return asType().deceiveAccurateDelay ? vel.y * asType().deceiveMulti : super.deltaY();
    }
}
