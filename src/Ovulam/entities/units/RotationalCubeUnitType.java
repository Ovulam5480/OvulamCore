package Ovulam.entities.units;

import arc.graphics.Color;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.TextureRegion;
import arc.math.Rand;
import arc.math.geom.Vec3;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.entities.Effect;
import mindustry.game.Team;
import mindustry.gen.TimedKillc;
import mindustry.gen.Unit;

public class RotationalCubeUnitType extends OvulamUnitType {
    private final static int[][] index = {{7, 5, 1, 3}, {5, 4, 0, 1}, {7, 6, 4, 5}, {6, 2, 0, 4}, {7, 3, 2, 6}, {3, 1, 0, 2}};
    public float cubeRadius;
    public float rotationMulti = 1f;
    public Effect effect = new Effect(120f, e -> {
        Draw.color(Color.valueOf("6ff4e2"), e.fout());
        Fill.square(e.x, e.y, e.rotation / 2 * (0.8f + 0.2f * e.foutpow()));
        Draw.reset();
    });

    public float cameraZ = 8f;

    public boolean drawBottom;
    public boolean drawCenter;
    public float cTile;

    public boolean randomRoll = true;

    public boolean deceiveAccurateDelay;
    public float deceiveMulti;

    public Rand rand = new Rand();

    public RotationalCubeUnitType(String name) {
        super(name);
        drawCell = false;
        engineSize = 0f;
        wobble = false;
    }

    @Override
    public void init() {
        super.init();
        if(cubeRadius == 0)cubeRadius = hitSize / 2f;
        if(cTile == 0)cTile = hitSize;
    }

    @Override
    public Unit create(Team team) {
        Unit unit = constructor.get();
        unit.team = team;
        unit.setType(this);
        unit.ammo = ammoCapacity; //fill up on ammo upon creation
        unit.elevation = flying ? 1f : 0;
        unit.heal();
        if (unit instanceof TimedKillc u) {
            u.lifetime(lifetime);
        }
        if (unit instanceof RotationalCubeUnit r) {
            rand.setSeed(r.id);
            r.axis.set(rand.random(1f), rand.random(1f), rand.random(1f)).setLength(1);
        }
        return unit;
    }

    @Override
    public void drawBody(Unit unit) {
        if (!(unit instanceof RotationalCubeUnit r)) return;
        applyColor(unit);

        drawCube(r.vec3s, unit, cubeRadius,region, drawCenter);
    }

    public void drawCube(Seq<Vec3> vec3s, Unit unit, float radius, TextureRegion region, boolean applyCenter){
        ObjectMap<Float, Integer> indexs = new ObjectMap<>();

        for (int i = 0; i < 6; i++) {
            float z = 0;

            for (int j = 0; j < 4; j++) {
                z += vec3s.get(index[i][j]).z;
            }

            if (drawBottom || z > 0) indexs.put(z, i);
        }

        Seq<Float> ins = indexs.keys().toSeq().sort();

        for (int i = 0; i < indexs.size; i++) {
            int[] ints = index[indexs.get(ins.get(i))];

            Fill.quad(region,
                    convert(vec3s.get(ints[0]).z, vec3s.get(ints[0]).x, radius) + unit.x,
                    convert(vec3s.get(ints[0]).z, vec3s.get(ints[0]).y, radius) + unit.y,
                    convert(vec3s.get(ints[1]).z, vec3s.get(ints[1]).x, radius) + unit.x,
                    convert(vec3s.get(ints[1]).z, vec3s.get(ints[1]).y, radius) + unit.y,
                    convert(vec3s.get(ints[2]).z, vec3s.get(ints[2]).x, radius) + unit.x,
                    convert(vec3s.get(ints[2]).z, vec3s.get(ints[2]).y, radius) + unit.y,
                    convert(vec3s.get(ints[3]).z, vec3s.get(ints[3]).x, radius) + unit.x,
                    convert(vec3s.get(ints[3]).z, vec3s.get(ints[3]).y, radius) + unit.y);

            if(applyCenter && i == 2)drawCenter(unit);
        }
    }

    public void drawCenter(Unit unit){
    }


    public float convert(float z, float c, float scl) {
        return (cameraZ / (cameraZ - z / cubeRadius)) * c * scl;
    }

    //todo
    @Override
    public void drawShadow(Unit unit) {
    }
}
