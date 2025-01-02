package Ovulam.world.graphics;

import mindustry.graphics.CacheLayer;

public class OvulamCacheLayers {
    public static CacheLayer subspaceCacheLayer;

    public static void init() {
        CacheLayer.add(subspaceCacheLayer = new CacheLayer.ShaderLayer(OvulamShaders.red));
    }
}
