package Ovulam.world.block.defense;

import arc.func.Cons;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.math.Mathf;
import arc.math.geom.Rect;
import arc.struct.IntMap;
import mindustry.Vars;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.graphics.Shaders;
import mindustry.world.Block;

import static arc.util.Time.time;
import static mindustry.Vars.indexer;
import static mindustry.world.meta.BuildVisibility.sandboxOnly;

public class TeamChangeTower extends Block {
    public float range = 15f;
    public int maxCapital = 3;
    protected static int returnInt = 0;
    public float changeSpeed = 0.2f;


    public TeamChangeTower(String name){
        super(name);
        update = true;
        sync = true;
        size = 2;
        destructible = true;
        configurable = true;
        health = 1000;
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashRect(Pal.placing, (x - range / 2) * 8 + offset, (y - range / 2) * 8 + offset, range * 8, range * 8);
    }


    public class DerelictChangeTowerBuild extends Building {
        IntMap<Float> cprogress = new IntMap<>();
        public float progress;

        public Rect rect(float x, float y, float size){
            return Rect.tmp.set(x - size / 2, y - size / 2, size, size);
        }

        public void nearBuildings(Team team, int maxCapture, Cons<Building> target){
            Rect rectRange = rect(this.x, this.y, range * 8);
            tempBuilds.clear();

            //todo tempBuilds
            indexer.eachBlock(team, rectRange, building -> building.block.buildVisibility != sandboxOnly, tempBuilds::addUnique);
            if(tempBuilds.size == 0){
                return;
            }


            tempBuilds.sort((a, b) -> Float.compare(a.dst2(tile), b.dst2(tile)));

            returnInt = 0;
            tempBuilds.each(n -> {
                if(returnInt++ < maxCapture){
                    target.get(n);
                }
            });
        }

        @Override
        public void drawConfigure(){
            Rect rectRange = rect(this.x, this.y, range * 8);
            Drawf.dashRect(Pal.placing, rectRange);

            nearBuildings(Team.derelict, maxCapital, t -> {
                Drawf.square(t.x, t.y, t.block.size * 2);
            });
        }

        @Override
        public void updateTile(){
            nearBuildings(Team.derelict, maxCapital, t -> {
                cprogress.put(t.pos(), 0f);
                if(cprogress.get(t.pos()) >= t.block.buildCost){
                    t.changeTeam(this.team);
                    t.enabled = true;
                    cprogress.remove(t.pos());
                    return;
                }
                progress = delta() * changeSpeed + cprogress.get(t.pos());
                cprogress.put(t.pos(), progress);
            });
        }


        @Override
        public void draw(){
            nearBuildings(Team.derelict, maxCapital, t -> {
                if(cprogress.get(t.pos()) == null){
                    return;
                }

                if(cprogress.get(t.pos()) >= t.block.buildCost){
                    t.block.placeEffect.at(t.x, t.y, t.block.size);
                }

                Draw.draw(Layer.blockBuilding, () -> {
                    Draw.color(Pal.accent);

                    Shaders.blockbuild.region = t.block.fullIcon;
                    Shaders.blockbuild.time = time;
                    Shaders.blockbuild.progress = cprogress.get(t.pos()) / t.block.buildCost;

                    Draw.rect(t.block.fullIcon, t.x, t.y, t.rotation);
                    Draw.flush();
                    Draw.color();
                });

                Draw.reset();

                Draw.z(Layer.blockBuilding + 1f);
                Draw.color(Pal.accent);
                Lines.lineAngleCenter(t.x + Mathf.sin(time, 10f, Vars.tilesize / 2f * t.block.size + 1f), t.y, 90, t.block.size * Vars.tilesize + 1f);

                Draw.reset();
            });
        }
    }
}
