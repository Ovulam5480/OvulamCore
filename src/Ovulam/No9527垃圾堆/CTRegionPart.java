package Ovulam.No9527垃圾堆;

import arc.graphics.g2d.TextureRegion;
import mindustry.entities.part.RegionPart;

public class CTRegionPart extends RegionPart {
    public float wScl, hScl;

    @Override
    public void load(String name){
        super.load(name);

        for (TextureRegion region : regions) {
            TextureRegion textureRegion = new TextureRegion();
            region.setWidth(region.width * wScl);
            region.setWidth(region.height * hScl);
        }

        for (TextureRegion region : outlines) {
            region.setWidth(region.width * wScl);
            region.setWidth(region.height * hScl);
        }
    }
};

