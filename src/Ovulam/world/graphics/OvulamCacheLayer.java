package Ovulam.world.graphics;

import mindustry.graphics.CacheLayer;

public class OvulamCacheLayer {
    public static CacheLayer subspaceCacheLayer;

    public static void init() {
        subspaceCacheLayer = new CacheLayer.ShaderLayer(OvulamShaders.subspaceShader);
    }

    public static void load(){
        CacheLayer.add(subspaceCacheLayer);
    }

}
