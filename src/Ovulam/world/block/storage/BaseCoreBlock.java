package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.game.Team;
import mindustry.gen.*;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.graphics.Pal;
import mindustry.ui.Bar;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.control;
import static mindustry.Vars.net;

//todo 木马物品(这个应该交给Events实现功能?
//会到处跑的核心？

public class BaseCoreBlock extends CoreBlock {
    public static Seq<Player> waitingPlayers = new Seq<>();
    //玩家单位重生时间
    public float spawnTime = 200f;
    //todo 额外建造的单位数
    public int extraSpawnAmount = 2;
    //这个核心被摧毁时，是否摧毁其他核心, 直接游戏结束
    public boolean destroyGameOver = false;

    public BaseCoreBlock(String name) {
        super(name);
        unitType = UnitTypes.gamma;
    }

    @Override
    public void setBars() {
        super.setBars();
        addBar("progress", (BaseCoreBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        return true;
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
    }

    public class BaseCoreBuild extends CoreBuild {
        public float playerSpawnCounter, unitSpawnCounter;
        public Player spawningPlayer;
        public Building[] coreAugment = new Building[4];

        @Override
        public float progress() {
            return playerSpawnCounter / spawnTime;
        }

        @Override
        public void drawSelect() {
            super.drawSelect();
            float progress = playerSpawnCounter / spawnTime;
            if (progress > 0f) {
                Draw.draw(Layer.blockOver, () -> Drawf.construct(x, y, unitType.fullIcon, rotdeg(), progress, 1f, Time.time));
            }
        }

        @Override
        public void draw() {
            super.draw();

        }

        @Override
        public void updateTile() {
            super.updateTile();

            for (int i = 0; i < coreAugment.length; i++) {
                Building b = coreAugment[i];
                if (!proximity.contains(b)) coreAugment[i] = null;
            }

            if (playerSpawnCounter > spawnTime) {
                if(spawningPlayer == null){
                    if(!waitingPlayers.isEmpty())spawningPlayer = waitingPlayers.first();
                    else return;
                }

                Player player = spawningPlayer;
                Call.playerSpawn(tile, player);

                waitingPlayers.remove(player);
                spawningPlayer = waitingPlayers.isEmpty() ? null : waitingPlayers.first();

                playerSpawnCounter = 0f;
            }else {
                playerSpawnCounter += delta();
                if(playerSpawnCounter > spawnTime){
                    Fx.spawn.at(x, y);
                }
            }
        }

        @Override
        public void requestSpawn(Player player) {
            Building b = proximity.find(building -> building instanceof PlayerSpawnStorage.UnitSpawnStorageBuild s
                    && s.getUnitType().supportsEnv(Vars.state.rules.env) && (s.spawnPlayer == null || s.spawnPlayer == player));

            if (b != null) {
                ((PlayerSpawnStorage.UnitSpawnStorageBuild) b).requestSpawn(player);
                return;
            }

            spawningPlayer = player;

            if (!unitType.supportsEnv(Vars.state.rules.env) || waitingPlayers.contains(player)) return;
            waitingPlayers.add(player);

            //todo ?
            BlockUnitc unit = (BlockUnitc) UnitTypes.block.create(team);
            unit.tile(this);
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            if (destroyGameOver) {
                Seq<CoreBuild> cores = Vars.state.teams.cores(team).copy();
                cores.each(coreBuild -> coreBuild.damage(coreBuild.health * 20f));
            }
        }

        public void unitSpawn() {
            Seq<Unit> units = team.data().units;
            int amount = units == null ? 0 : units.count(unit -> !unit.spawnedByCore());

            if (amount >= extraSpawnAmount || waitingPlayers.size > 0) {
                unitSpawnCounter = Mathf.approachDelta(unitSpawnCounter, 0, 5f);
                return;
            }

            unitSpawnCounter += delta();
            if (unitSpawnCounter >= spawnTime) {
                //todo 特效
                Fx.placeBlock.at(x, y, unitType.hitSize);
                if (!Vars.net.client()) {
                    Unit unit = unitType.create(team);
                    unit.set(this);
                    unit.rotation = 90;
                    unit.add();
                    unitSpawnCounter = 0;
                }
            }
        }
    }
}
