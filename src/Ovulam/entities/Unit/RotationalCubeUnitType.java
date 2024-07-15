package Ovulam.entities.Unit;

import arc.graphics.Color;
import arc.graphics.g2d.Bloom;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.game.Team;
import mindustry.gen.TimedKillc;
import mindustry.gen.Unit;

public class RotationalCubeUnitType extends OvulamUnitType {
    private final int[][] index = {{7, 5, 1, 3}, {5, 4, 0, 1}, {7, 6, 4, 5}, {6, 2, 0, 4}, {7, 3, 2, 6}, {3, 1, 0, 2}};
    public float cubeRadius = 64f;
    public float rotationMulti = 1f;
    public boolean drawCircle;
    public float circleRadius;
    public Color circleColor;

    public Bloom bloom;

    public RotationalCubeUnitType(String name) {
        super(name);
        drawCell = false;
        engineSize = 0f;
    }

    @Override
    public void init() {
        super.init();
        cubeRadius = hitSize / 2f;
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
            r.axis = new Vec3(Mathf.random(1), Mathf.random(1), Mathf.random(1)).setLength(1);
            r.cubeRadius = cubeRadius;
            r.rotationMulti = rotationMulti;
        }
        return unit;
    }

    @Override
    public void drawBody(Unit unit) {
        if (!(unit instanceof RotationalCubeUnit r)) return;

        Seq<Vec3> vec3s = r.vec3s;

        for (int i = 0; i < 6; i++) {
            int[] ints = index[i];
            Tmp.v1.set(vec3s.get(ints[0]));
            Tmp.v2.set(vec3s.get(ints[1]));
            Tmp.v3.set(vec3s.get(ints[2]));
            Tmp.v4.set(vec3s.get(ints[3]));

            Fill.quad(region,
                    vec3s.get(ints[0]).x, vec3s.get(ints[0]).y,
                    vec3s.get(ints[1]).x, vec3s.get(ints[1]).y,
                    vec3s.get(ints[2]).x, vec3s.get(ints[2]).y,
                    vec3s.get(ints[3]).x, vec3s.get(ints[3]).y);

            if (drawCircle && i == 3) {
                float radius = circleRadius * (1f + Mathf.absin(2f, 0.1f));

                //todo 泛光
                Draw.color(Color.white);
                Fill.circle(unit.x, unit.y, radius);
                Draw.color(circleColor);
                Fill.circle(unit.x, unit.y, radius * 0.8f);
                Draw.color();
            }
        }
    }

    //todo
    @Override
    public void drawShadow(Unit unit) {
    }
}
