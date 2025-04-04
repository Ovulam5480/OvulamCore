package Ovulam.world.graphics;

import Ovulam.OvulamMod;
import arc.Core;
import arc.audio.Music;
import arc.files.Fi;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.graphics.gl.Shader;
import arc.struct.Seq;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.Vars;

import static mindustry.Vars.control;
import static mindustry.Vars.renderer;
import static mindustry.graphics.Shaders.getShaderFi;

public class OvulamShaders {
    public static BlockManufacturerShader blockManufacturer;
    public static Test red;

    public static void init() {
        blockManufacturer = new BlockManufacturerShader();
        red = new Test();
    }

    public static Texture loadTexture(String name){
        Texture t = new Texture(Vars.mods.getMod(OvulamMod.class).root.child("textures").child(name + ".png"));
        t.setFilter(Texture.TextureFilter.linear);
        t.setWrap(Texture.TextureWrap.repeat);

        return t;
    }

    ///////////////////////////////////////////////////////////
    public static class Test extends OvulamFragShader {
        public Test() {
            super("inspire", false);
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
            super("BlockManufacturer", true);
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
    public static class OvulamFragShader extends Shader {
        public OvulamFragShader(String frag, boolean isDefault) {
            super(getShaderFi(isDefault ? "default.vert" : "screenspace.vert"), getModShaderFi(frag));
        }

        public static Fi getModShaderFi(String file) {
            return Vars.mods.getMod(OvulamMod.class).root.child("shaders").child(file + ".frag");
        }
    }
}
