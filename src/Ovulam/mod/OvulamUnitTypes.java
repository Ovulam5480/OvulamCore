package Ovulam.mod;

import Ovulam.OvulamMod;
import Ovulam.entities.Unit.OvulamUnit;
import Ovulam.entities.Unit.OvulamUnitType;
import Ovulam.entities.Unit.RotationalCubeUnit;
import Ovulam.entities.Unit.RotationalCubeUnitType;
import Ovulam.world.No9527.大圈圈weapon;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.entities.bullet.BasicBulletType;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.type.UnitType;

public class OvulamUnitTypes {
    public static UnitType delta, test, SS, S;

    public static ObjectMap<Class<? extends Entityc>, Integer> ids = new ObjectMap<>();

    static {
        ids.put(OvulamUnit.class, EntityMapping.register(OvulamMod.OvulamModName() + "delta", OvulamUnit::new));
        ids.put(RotationalCubeUnit.class, EntityMapping.register(OvulamMod.OvulamModName() + "SS", RotationalCubeUnit::new));
        ids.put(RotationalCubeUnit.class, EntityMapping.register(OvulamMod.OvulamModName() + "S", RotationalCubeUnit::new));
    }

    public static int getId(Class<? extends Entityc> key) {
        return ids.get(key);
    }

    public static void load() {

        test = new UnitType("test") {
            {
                weapons.add(new 大圈圈weapon("large-weapon") {{
                    mirror = false;
                    reload = 13f;
                    x = 16;
                    y = 16;
                    top = false;
                    ejectEffect = Fx.casing1;

                    bullet = new BasicBulletType(2.5f, 9) {{
                        width = 7f;
                        height = 9f;
                        lifetime = 60f;
                    }};
                }});

                weapons.add(new 大圈圈weapon("large-weapon") {{
                    mirror = false;
                    reload = 13f;
                    x = 16;
                    y = 16;
                    top = false;
                    ejectEffect = Fx.casing1;
                    X初相 = 180f;

                    bullet = new BasicBulletType(2.5f, 9) {{
                        width = 7f;
                        height = 9f;
                        lifetime = 60f;
                    }};
                }});
                constructor = UnitTypes.flare.constructor;
                flying = true;
            }
        };

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

            trailLength = 30;

            insideTrailLength = 20;
            insideTrailScl = 0.4f;
            insideTrailColor = Color.white;

            setEnginesMirror(
                    new UnitEngine(4f, -13, 2.7f, 345f),
                    new UnitEngine(12f, -11, 2.7f, 330f)
            );

            faceTarget = false;
        }};

        SS = new RotationalCubeUnitType("SS") {{
            hitSize = 8f;
            flying = true;
            rotationMulti = 3f;
        }};

        S = new RotationalCubeUnitType("S") {{
            hitSize = 16f;
            flying = true;
            rotationMulti = 3f;
            drawCircle = true;
            circleColor = Color.cyan;
            circleRadius = 4f;
        }};
    }
}
