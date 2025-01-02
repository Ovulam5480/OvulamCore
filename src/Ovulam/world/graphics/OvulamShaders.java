package Ovulam.world.graphics;

import Ovulam.OvulamCore;
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
    public static Test red;


    public static void init() {
        blockManufacturer = new BlockManufacturerShader();
        subspaceShader = new OvulamSurfaceShader("Subspace");
        red = new Test();
    }

    public static Texture loadTexture(String name){
        Texture t = new Texture(Vars.mods.getMod(OvulamCore.class).root.child("textures").child(name + ".png"));
        t.setFilter(Texture.TextureFilter.linear);
        t.setWrap(Texture.TextureWrap.repeat);

        return t;
    }

    ///////////////////////////////////////////////////////////
    public static class Test extends OvulamFragShader {
        public Test() {
            super("inspire");
        }

        @Override
        public void apply() {
            setUniformf("u_time", Time.time / 60);
            setUniformf("u_campos", Core.camera.position.x - Core.camera.width / 2, Core.camera.position.y - Core.camera.height / 2);
            setUniformf("u_resolution", Core.graphics.getWidth(), Core.graphics.getHeight());
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
        Texture noiseTex, noiseTex2;

        public OvulamSurfaceShader(String frag) {
            super(frag);
            loadNoise();
        }

        public void loadNoise() {
            noiseTex = loadTexture("median");
            noiseTex2 = loadTexture("gaussian");
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

                if (noiseTex2 == null) {
                    noiseTex2 = Core.assets.get("sprites/noise.png", Texture.class);
                }

                noiseTex2.bind(1);
                renderer.effectBuffer.getTexture().bind(0);

                setUniformi("u_noise2", 1);
            }
        }
    }

    ///////////////////////////////////////////////////////////
    public static class OvulamFragShader extends Shader {
        public OvulamFragShader(String frag) {
            super(getShaderFi("screenspace.vert"), getModShaderFi(frag));
        }

        public static Fi getModShaderFi(String file) {
            return Vars.mods.getMod(OvulamCore.class).root.child("shaders").child(file + ".frag");
        }
    }
}
