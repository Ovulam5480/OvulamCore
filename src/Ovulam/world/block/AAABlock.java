package Ovulam.world.block;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Fill;
import arc.graphics.g2d.Lines;
import arc.math.geom.Vec2;
import arc.math.geom.Vec3;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.content.Blocks;
import mindustry.entities.Fires;
import mindustry.entities.bullet.FireBulletType;
import mindustry.gen.Building;
import mindustry.gen.Bullet;
import mindustry.gen.Fire;
import mindustry.gen.Groups;
import mindustry.graphics.Layer;
import mindustry.world.Block;

import static mindustry.Vars.tilesize;

public class AAABlock extends Block {
    //
    public Vec3 axis = new Vec3(2,1,3);
    public float cubeRadius = 4f;

    public AAABlock(String name) {
        super(name);
        update = true;
        sync = true;
        configurable = true;
    }

    public class AAABuild extends Building{
        public Seq<Vec3> vec3s = Seq.with(new Vec3(),new Vec3(),new Vec3(),new Vec3(), new Vec3(),new Vec3(),new Vec3(),new Vec3());

        private final int[][] index = {{7,5,1,3},{5,4,0,1},{7,6,4,5},{6,2,0,4},{7,3,2,6},{3,1,0,2}};

        public Vec2 v1 = new Vec2();
        public Vec2 v2 = new Vec2();
        public Vec2 v3 = new Vec2();
        public Vec2 v4 = new Vec2();

        public float rot3D;


        public void buildConfiguration(Table table) {
            table.table(configTable -> table.table(sliders -> {
                sliders.slider(-1, 1f, 0.01f, axis.x, f -> axis.x = f);
                sliders.row();
                sliders.slider(-1, 1f, 0.01f, axis.y, f -> axis.y = f);
                sliders.row();
                sliders.slider(-1, 1f, 0.01f, axis.z, f -> axis.z = f);
            }));
        }

        @Override
        public void updateTile(){

            tile.getLinkedTiles(t -> {
                if(t == null || Fires.has(t.x, t.y))return;

                Fire fire = Fires.get(t.x, t.y);
                fire.remove();
            });

            float radiusT = size / 2f * tilesize;
            Groups.bullet.intersect(x - radiusT, y - radiusT, radiusT * 2, radiusT * 2).each(fire -> fire.type instanceof FireBulletType, Bullet::remove);






            rot3D += delta();
            axis.setLength(1);

            //todo 二进制
            int index = 0;
            for (int i = -1; i <= 1; i += 2){
                for (int j = -1; j <= 1; j += 2){
                    for (int k = -1; k <= 1; k += 2){
                        vec3s.get(index).set(i,j,k).rotate(axis, rot3D).scl(cubeRadius).add(x,y,0);
                        index++;
                    }
                }
            }

        }

        @Override
        public void draw(){
            Draw.z(Layer.blockOver);
            Lines.stroke(4f);

            for (int i = 0; i < 6; i++){
                int[] ints = index[i];
                v1.set(vec3s.get(ints[0]));
                v2.set(vec3s.get(ints[1]));
                v3.set(vec3s.get(ints[2]));
                v4.set(vec3s.get(ints[3]));

                Fill.quad(Blocks.airFactory.fullIcon,
                        vec3s.get(ints[0]).x, vec3s.get(ints[0]).y,
                        vec3s.get(ints[1]).x, vec3s.get(ints[1]).y,
                        vec3s.get(ints[2]).x, vec3s.get(ints[2]).y,
                        vec3s.get(ints[3]).x, vec3s.get(ints[3]).y);
            }
        }
    }
}
