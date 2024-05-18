package Ovulam.world.graphics;

import Ovulam.OvulamMod;
import arc.Core;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.util.Time;
import mindustry.Vars;

import static mindustry.Vars.renderer;
import static mindustry.graphics.Shaders.getShaderFi;

public class OvulamShaders {
    public static BlockManufacturerShader blockManufacturer;
    public static OvulamSurfaceShader subspaceShader;
    public static Alpha alpha;


    public static void init() {
        blockManufacturer = new BlockManufacturerShader();
        subspaceShader = new OvulamSurfaceShader("Subspace");
        alpha = new Alpha();
    }

    ///////////////////////////////////////////////////////////
    public static class Alpha extends OvulamFragShader {
        public float alpha;
        public Alpha() {
            super("Alpha");
        }
    }

    ///////////////////////////////////////////////////////////
    public static class BlockManufacturerShader extends OvulamFragShader {
        public float progress;
        public TextureRegion region = new TextureRegion();
        public float time;

        public BlockManufacturerShader() {
            super("BlockManufacturer");
        }

        @Override
        public void apply() {
            setUniformf("u_progress", progress);
            setUniformf("u_uv", region.u, region.v);
            setUniformf("u_uv2", region.u2, region.v2);
            setUniformf("u_time", time);
            setUniformf("u_texsize", region.texture.width, region.texture.height);
        }
    }

    ///////////////////////////////////////////////////////////
    public static class OvulamSurfaceShader extends OvulamFragShader {
        Texture noiseTex;

        public OvulamSurfaceShader(String frag) {
            super(frag);
            loadNoise();
        }

        public void loadNoise() {
            Core.assets.load("sprites/noise.png", Texture.class).loaded = t -> {
                t.setFilter(Texture.TextureFilter.linear);
                t.setWrap(Texture.TextureWrap.repeat);
            };
        }

        @Override
        public void apply() {
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.camera.width, Core.camera.height);
            setUniformf("u_time", Time.time);

            if (hasUniform("u_noise")) {
                if (noiseTex == null) {
                    noiseTex = Core.assets.get("sprites/noise.png", Texture.class);
                }

                noiseTex.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise", 1);
            }
        }
    }

    ///////////////////////////////////////////////////////////
    public static class OvulamFragShader extends Shader {
        public OvulamFragShader(String frag) {
            super(getShaderFi("default.vert"), getModShaderFi(frag));
        }

        public static Fi getModShaderFi(String file) {
            //咱也不知道为啥变成小写的了
            return Vars.mods.getMod(OvulamMod.class).root.child("shaders").child(file + ".frag");
        }
    }
}
