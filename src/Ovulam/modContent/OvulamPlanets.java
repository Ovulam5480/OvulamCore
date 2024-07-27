package Ovulam.modContent;

import arc.graphics.Color;
import mindustry.content.Planets;
import mindustry.graphics.g3d.HexMesh;
import mindustry.maps.planet.SerpuloPlanetGenerator;
import mindustry.type.Planet;

public class OvulamPlanets {
    public static Planet AAA;

    public static void load(){
        AAA = new Planet("AAA", Planets.sun, 1f, 3){{
            meshLoader = () -> new HexMesh(this, 5);
            alwaysUnlocked = true;
            generator = new SerpuloPlanetGenerator();
            atmosphereColor = Color.valueOf("6ff4e2");
        }};
    }
}
