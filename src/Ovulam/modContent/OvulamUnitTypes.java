package Ovulam.modContent;

import Ovulam.OvulamMod;
import Ovulam.entities.units.*;
import Ovulam.entities.units.MultiSegment.TreeUnit;
import Ovulam.entities.units.MultiSegment.TreeUnitType;
import Ovulam.entities.units.MultiSegment.TreeUnitTypePart;
import Ovulam.gen.EntityRegistry;
import Ovulam.gen.MyTankUnit;
import Ovulam.gen.Myc;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Time;
import ent.anno.Annotations.EntityDef;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.*;
import mindustry.type.UnitType;

public class OvulamUnitTypes {
    public static UnitType delta;
    public static UnitType S, SS;
    public static UnitType SSS;
    public static TreeUnitType S1, S2, S3, S4;

    public static @EntityDef({Unitc.class, Tankc.class, Myc.class}) UnitType myut;

    public static void load() {
        EntityRegistry.register(OvulamMod.modName() + "delta", OvulamUnit.class, OvulamUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "S", RotationalCubeUnit.class, RotationalCubeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "SS", RotationalCubeUnit.class, RotationalCubeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "SSS", RollCubeUnit.class, RollCubeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "delta", OvulamUnit.class, OvulamUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "S1", TreeUnit.class, TreeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "S2", TreeUnit.class, TreeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "S3", TreeUnit.class, TreeUnit::new);
        EntityRegistry.register(OvulamMod.modName() + "S4", TreeUnit.class, TreeUnit::new);

        //myut = EntityRegistry.content("myut", MyTankUnit.class, UnitType::new);

        SSS = new RollCubeUnitType("SSS") {{
            hitSize = 24f;
            health = 1000;
            armor = 10f;

            deceiveAccurateDelay = true;
            deceiveMulti = 2f;
        }};

        delta = new OvulamUnitType("delta") {{
            coreUnitDock = true;
            isEnemy = false;

            accel = 0.05f;
            drag = 0.08f;
            speed = 12f;
            mineFloor = false;
            flying = true;
            rotateSpeed = 12f;
            itemCapacity = 0;
            hitSize = 24f;
            buildRange = Vars.bufferSize * 2f;
            buildSpeed = 25f;
            buildBeamOffset = 8f;
            targetPriority = 999f;
            drawCell = false;

            engineOffset = 7.5f;
            engineSize = 3.4f;

            trailLength = 31;

            insideTrailLength = 20;
            insideTrailScl = 0.4f;
            insideTrailColor = Color.white;

            setEnginesMirror(
                    new UnitEngine(4f, -13, 2.7f, 345f),
                    new UnitEngine(12f, -11, 2.7f, 331f)
            );

            faceTarget = false;
        }};

        S = new RotationalCubeUnitType("S") {{
            hitSize = 16f;
            rotationMulti = 3f;
            flying = true;

            deceiveAccurateDelay = true;
            deceiveMulti = 2f;
            randomRoll = true;
        }};

        SS = new RotationalCubeUnitType("SS") {{
            hitSize = 24f;
            rotationMulti = 3f;
            drawBottom = true;
            drawCenter = true;
            flying = true;
        }
            private final Seq<Vec3> vs = new Seq<>();
            private final Vec3 ax = new Vec3(1, 1, 0);
            private TextureRegion centerRegion;

            @Override
            public void drawCenter(Unit unit) {
                if (!(unit instanceof RotationalCubeUnit r)) return;
                vs.clear();
                r.vec3s.each(vec3 -> vs.add(vec3.cpy().rotate(ax, 45)));

                drawCube(vs, unit, cubeRadius / 2.5f, centerRegion, false);
            }

            @Override
            public void load(){
                super.load();
                centerRegion = Core.atlas.find(S.name);
            }
        };

        S4 = new TreeUnitType("S4", IntMap.of()){{
            weapons.add(UnitTypes.toxopid.weapons.get(1));
        }};

        S1 = new TreeUnitType("S1", IntMap.of(
                2, Seq.with(new TreeUnitTypePart( 0f, 10f)),
                3, Seq.with(new TreeUnitTypePart(S4, 0f, 15))
        ));

        S2 = new TreeUnitType("S2", IntMap.of(
                1,  Seq.with(new TreeUnitTypePart(S1, 0f, 10f))));

        TreeUnitTypePart S3part = new TreeUnitTypePart(10f, -20f){{
            lerpProgress = 0.7f;

            partMove = true;
            x2 = -10f;
            y2 = -20f;

            rotation2 = rotation + 30f;
            rotation = rotation - 30f;

            progress = (tu) -> Mathf.sin(Time.time + tu.number * 80f, 60f, 0.5f) + 0.5f;
        }};

        S3 = new TreeUnitType("S3", IntMap.of(
                1, Seq.with(S3part,
                        new TreeUnitTypePart(S1, 10f, 10),
                        new TreeUnitTypePart(S1, -10f, 10)),
                4, Seq.with(S3part,
                        new TreeUnitTypePart(S1, 20f, 0),
                        new TreeUnitTypePart(S1, -20f, 0)),
                10, Seq.with(S3part,
                        new TreeUnitTypePart(S2, 20f, 0),
                        new TreeUnitTypePart(S2, -20f, 0)),
                12, Seq.with(S3part,
                        new TreeUnitTypePart(S1, 20f, 0),
                        new TreeUnitTypePart(S1, -20f, 0)),
                14, Seq.with(S3part,
                        new TreeUnitTypePart(S1, 20f, -20),
                        new TreeUnitTypePart(S1, -20f, -20))
        )){{
            speed = 0.5f;
        }};
    }
}
