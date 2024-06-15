package Ovulam.mod;

import Ovulam.OvulamMod;
import Ovulam.entities.Unit.OvulamUnit;
import Ovulam.entities.Unit.OvulamUnitType;
import arc.graphics.Color;
import arc.struct.ObjectMap;
import mindustry.Vars;
import mindustry.gen.EntityMapping;
import mindustry.gen.Entityc;
import mindustry.type.UnitType;

public class OvulamUnitTypes {
    public static UnitType delta;

    public static ObjectMap<Class<? extends Entityc>, Integer> ids = new ObjectMap<>();

    public static int getId(Class<? extends Entityc> key){
        return ids.get(key);
    }

    static {
        ids.put(OvulamUnit.class, EntityMapping.register(OvulamMod.OvulamModName() + "delta", OvulamUnit::new));
    }

    public static void load() {

        delta = new OvulamUnitType("delta"){{
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
            );;

            faceTarget = false;
        }};
    }
}
