package Ovulam.No9527垃圾堆;

import arc.Core;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.game.Team;
import mindustry.gen.Building;
import mindustry.world.Block;
import mindustry.world.Tile;
import mindustry.world.blocks.defense.Wall;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.world;

public class WWaallll extends Wall {
    //是否立即放置, 立即放置则禁止资源不足时禁止放置
    public boolean 立即放置 = false;
    
    //场上数量要求方块不足时禁止放置
    public Block 场上数量要求方块 = null;
    public int 数量 = 4;
    
    //放置位置没有被覆盖方块时禁止放置
    public Block 被覆盖方块 = Blocks.duo;

    public WWaallll(String name) {
        super(name);
    }


    public boolean 资源要求(Team team){
        CoreBlock.CoreBuild core = Vars.state.teams.get(team).core();
        return !立即放置 || core != null && core.items.has(requirements, Vars.state.rules.buildCostMultiplier);
    }

    public boolean 数量要求(Team team){
        return 场上数量要求方块 == null || Vars.state.teams.get(team).getBuildings(场上数量要求方块).size >= 数量;
    }

    public boolean 覆盖要求(Tile tile){
        tile.getLinkedTilesAs(this, tempTiles);
        return 被覆盖方块 == null || tempTiles.contains(o -> o.block() == 被覆盖方块);
    }

    public boolean 沙盒模式(){
        return Vars.state.isEditor() || Vars.state.rules.infiniteResources;
    }

    @Override
    public boolean canReplace(Block other) {
        return super.canReplace(other) || other == 被覆盖方块;
    }

    @Override
    public boolean canPlaceOn(Tile tile, Team team, int rotation) {
        if(!沙盒模式() && !(资源要求(team) && 数量要求(team) && 覆盖要求(tile)))return false;
        return super.canPlaceOn(tile, team, rotation);
    }

    //显示红字
    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid) {
        Team team = Vars.player.team();

        if(沙盒模式()){}
        else if(!资源要求(team))drawPlaceText(Core.bundle.format("bar.noresources"), x, y, valid);
        else if (!数量要求(team))drawPlaceText(Core.bundle.get("QuantityLimit2.缺少前置方块") + 场上数量要求方块.localizedName + " X" + 数量, x, y, false);
        else if (!覆盖要求(world.tile(x, y)))drawPlaceText(Core.bundle.format("cttd.UpgradeFront") + 被覆盖方块.localizedName, x, y, valid);;

        super.drawPlace(x, y, rotation, valid);
    }

    //瞬间替换和扣除物品
    @Override
    public void placeBegan(Tile tile, Block previous) {
        Team team = Vars.player.team();
        
        if(立即放置 && 资源要求(team) && !沙盒模式()){
            CoreBlock.CoreBuild core = Vars.player.team().core();
            core.items.remove(requirements);
            tile.setBlock(this, tile.team());
            tile.block().placeEffect.at(tile, tile.block().size);
        }
        else super.placeBegan(tile, previous);
    }

    public class  ss extends Building{
        @Override
        public void heal(float amount) {
        }

        @Override
        public boolean isHealSuppressed() {
            return false;
        }
    }
}
