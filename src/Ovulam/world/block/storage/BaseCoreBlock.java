package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.math.Mathf;
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
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

//todo 木马物品(这个应该交给Events实现功能?
//会到处跑的核心？


public class BaseCoreBlock extends CoreBlock{
    //玩家单位重生时间
    public float spawnTime = 200f;
    //额外建造的单位数
    public int extraSpawnAmount = 1;
    //这个核心被摧毁时，是否直接游戏结束
    public boolean destroyGameOver = false;
    //超过复活限制时,核心才能被攻击
    public boolean spawnProtect = false;
    //玩家复活次数限制,超过限制时,核心才能被攻击
    public int spawnLimitation = -1;

    public BaseCoreBlock(String name){
        super(name);
        unitType = UnitTypes.gamma;
    }

    @Override
    public void setBars(){
        super.setBars();
        addBar("progress", (BaseCoreBuild e) -> new Bar("bar.progress", Pal.ammo, e::progress));
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation){
        return true;
    }


    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
    }

    public class BaseCoreBuild extends CoreBuild {
        public float unitSpawnCounter = 0, playerSpawnCounter = 0;
        public Player spawnPlayer;
        public Building[] coreAugment = new Building[4];
        public int spawnCounter = 0;

        public boolean destroyGameOver(){
            return destroyGameOver;
        }

        //如果启用玩家保护, 并且玩家都存活, 则核心处于无敌状态
        public boolean protectValid(){
            return spawnProtect && spawnCounter < spawnLimitation;
        }

        @Override
        public boolean collision(Bullet other) {
            if(protectValid())return false;
            return super.collision(other);
        }

        @Override
        public void damage(float damage) {
            if(protectValid())return;
            super.damage(damage);
        }

        @Override
        public void drawConfigure(){
        }

        @Override
        public float progress(){
            return unitSpawnCounter / spawnTime;
        }

        @Override
        public void draw(){
            super.draw();
            //Font font = Fonts.outline;
            //font.draw(Arrays.toString(coreAugment), x, y - 20, Align.center);
            //font.draw(String.valueOf(unitSpawnCounter), x, y + 20, Align.center);

            float spawnCounter = Math.max(playerSpawnCounter, unitSpawnCounter);
            if(spawnCounter > 0.1f){
                Draw.draw(Layer.blockOver, () -> Drawf.construct(x, y, unitType.fullIcon,
                        rotdeg(), spawnCounter / spawnTime, 1f, Time.time));
            }
        }

        @Override
        public void updateTile(){
            super.updateTile();

            for (int i = 0; i < coreAugment.length; i++){
                Building b = coreAugment[i];
                if(!proximity.contains(b)) coreAugment[i] = null;
            }

            if(spawnPlayer == null) unitSpawn();
            else playerSpawnCounter += delta();

            if(playerSpawnCounter > spawnTime) requestSpawn(spawnPlayer);
        }

        @Override
        public void requestSpawn(Player player){
            Building b = proximity.find(building -> building instanceof PlayerSpawnStorage.UnitSpawnStorageBuild s
            && s.getUnitType().supportsEnv(Vars.state.rules.env) && (s.spawnPlayer == null || s.spawnPlayer == player));

            if(b != null){
                ((PlayerSpawnStorage.UnitSpawnStorageBuild)b).requestSpawn(player);
                return;
            }

            if(!unitType.supportsEnv(Vars.state.rules.env) || !(spawnPlayer == null || spawnPlayer == player)) return;
            spawnPlayer = player;
            //请求重生需要队列里面为空
            if(!(playerSpawnCounter > spawnTime)) return;
            Call.playerSpawn(tile, player);
            spawnCounter ++;
            spawnPlayer = null;
            playerSpawnCounter = 0f;
        }

        @Override
        public void onDestroyed(){
            super.onDestroyed();
            if(destroyGameOver){
                Seq<CoreBuild> cores = Vars.state.teams.cores(team).copy();
                cores.each(coreBuild -> coreBuild.damage(coreBuild.health * 20f));
            }
        }

        public void unitSpawn(){
            Seq<Unit> units = team.data().units;
            int amount = units == null ? 0 : units.count(unit -> !unit.spawnedByCore());

            if(amount >= extraSpawnAmount){
                unitSpawnCounter = Mathf.approachDelta(unitSpawnCounter, 0, 5f);
                return;
            }

            unitSpawnCounter += delta();
            if(unitSpawnCounter >= spawnTime){
                //todo 特效
                Fx.placeBlock.at(x, y, unitType.hitSize);
                if(!Vars.net.client()){
                    Unit unit = unitType.create(team);
                    unit.set(this);
                    unit.rotation = 90;
                    //我都不知道这玩意干嘛的
                    unit.add();
                    unitSpawnCounter = 0;
                }
            }
        }
    }
}
