package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.struct.ObjectMap;
import arc.util.Align;
import arc.util.Time;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.UnitTypes;
import mindustry.gen.BlockUnitc;
import mindustry.gen.Building;
import mindustry.gen.Player;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;


public class PlayerSpawnStorage extends BaseStorageBlock{
    public UnitType unitType = UnitTypes.alpha;
    public float spawnTime = 200f;

    public static ObjectMap<Player, BlockUnitc> waitingPlayers = new ObjectMap<>();

    public PlayerSpawnStorage(String name) {
        super(name);
    }

    public class UnitSpawnStorageBuild extends BaseStorageBuild{
        public float playerSpawnCounter = 0;
        public Player spawnPlayer;

        public UnitType getUnitType(){
            return unitType;
        }

        @Override
        public void onControlSelect(Unit unit){
            if(!unit.isPlayer()) return;
            Player player = unit.getPlayer();

            Fx.spawn.at(player);
            if(Vars.net.client() && player == Vars.player) Vars.control.input.controlledType = null;

            player.clearUnit();
            player.deathTimer = Player.deathDelay + 1f;
            requestSpawn(player);
        }

        public void requestSpawn(Player player){
            if(!unitType.supportsEnv(Vars.state.rules.env) || waitingPlayers.containsKey(player)) return;

            if(spawnPlayer == null)spawnPlayer = player;

            BlockUnitc unit = (BlockUnitc) UnitTypes.block.create(team);
            unit.tile(this);
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(spawnPlayer == null) return;
            if(coreAugment() || playerSpawnCounter > spawnTime){
                playerSpawn(spawnPlayer);
                return;
            }
            if(!coreAugment()) playerSpawnCounter += delta();
        }

        public void playerSpawn(Player player){
            if(wasVisible)Fx.spawn.at(this);
            player.set(this);

            if(!Vars.net.client()){
                Unit unit = unitType.create(team);
                unit.set(this);
                unit.rotation(90f);
                unit.impulse(0f, 3f);
                unit.spawnedByCore(true);
                unit.controller(player);
                unit.add();
            }

            if(Vars.state.isCampaign() && player == Vars.player){
                unitType.unlock();
            }
        }

        @Override
        public void draw(){
            Font font = Fonts.outline;
            font.draw(String.valueOf(playerSpawnCounter), x, y - 20, Align.center);
            super.draw();
            if(playerSpawnCounter > 0.1f){
                Draw.draw(Layer.blockOver, () -> Drawf.construct(x, y, unitType.fullIcon,
                        rotdeg(), playerSpawnCounter / spawnTime, 1f, Time.time));
            }
        }
    }
}
