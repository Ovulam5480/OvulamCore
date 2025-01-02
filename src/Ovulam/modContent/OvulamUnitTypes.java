package Ovulam.modContent;

import Ovulam.OvulamCore;
import Ovulam.entities.Unit.MultiSegment.TreeUnit;
import Ovulam.entities.Unit.MultiSegment.TreeUnitType;
import Ovulam.entities.Unit.MultiSegment.TreeUnitTypePart;
import Ovulam.entities.Unit.*;
import arc.Core;
import arc.func.Prov;
import arc.graphics.Color;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Vec3;
import arc.struct.IntMap;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.UnitTypes;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.gen.Unit;
import mindustry.type.UnitType;

public class OvulamUnitTypes {
    public static UnitType delta, SSS, SS, S;
    public static TreeUnitType S1, S2, S3, S4;

    public static ObjectMap<Class<? extends Entityc>, Integer> ids = new ObjectMap<>();

    public static int getId(Class<? extends Entityc> key) {
        return ids.get(key);
    }

    public static void put(Class<? extends Entityc> unitClass, UnitType type, Prov prov){
        ids.put(unitClass, EntityMapping.register(OvulamCore.OvulamCoreName() + type.name, prov));
    }

    public static void load() {
        ids.put(OvulamUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "delta", OvulamUnit::new));
        ids.put(RotationalCubeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "S", RotationalCubeUnit::new));
        ids.put(RotationalCubeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "SS", RotationalCubeUnit::new));
        ids.put(RollCubeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "SSS", RollCubeUnit::new));
        ids.put(OvulamUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "delta", OvulamUnit::new));

        ids.put(TreeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "S1", TreeUnit::new));
        ids.put(TreeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "S2", TreeUnit::new));
        ids.put(TreeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "S3", TreeUnit::new));
        ids.put(TreeUnit.class, EntityMapping.register(OvulamCore.OvulamCoreName() + "S4", TreeUnit::new));

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
