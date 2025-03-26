package Ovulam.world.block;

import Ovulam.world.block.production.MultiPayloadCrafter;
import Ovulam.world.type.Recipe;
import arc.Events;
import arc.graphics.g2d.Font;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.ImageButton;
import arc.scene.ui.ScrollPane;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import mindustry.Vars;
import mindustry.content.Fx;
import mindustry.content.Liquids;
import mindustry.content.UnitTypes;
import mindustry.game.EventType;
import mindustry.gen.Building;
import mindustry.gen.Tex;
import mindustry.gen.Unit;
import mindustry.graphics.Drawf;
import mindustry.graphics.Pal;
import mindustry.type.ItemStack;
import mindustry.ui.Fonts;
import mindustry.ui.ReqImage;
import mindustry.ui.Styles;
import mindustry.world.Block;

import static mindustry.Vars.*;

public class ItemRepeater extends Block {
    public float range = 20;

    public ItemRepeater(String name){
        super(name);
        hasItems = true;
        acceptsItems = true;
        update = true;
        sync = true;
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void load(){
        super.load();
    }

    @Override
    public void drawPlace(int x, int y, int rotation, boolean valid){
        Drawf.dashRect(Pal.accent,
                x * tilesize + offset - range * tilesize / 2,
                y * tilesize + offset - range * tilesize / 2,
                range * tilesize, range * tilesize);
    }

    public class ItemRepeaterBuild extends Building {
        public int previousPlan = -1;
        public int currentPlan = -1;
        public int hoveredPlan = -1;
        public int tableRows = 8;
        public int tableColumns = 8;
        public Seq<MultiPayloadCrafter.MultiPayloadPlan> plans = new Seq<>(5);

        public Seq<ItemStack> getInputItems() {
            return new Seq<>();
        }

        @Override
        public void buildConfiguration(Table table) {
//            table.setBackground(Styles.black3);
//            table.setWidth(800);
//
//            Table recipeShow = new Table(Styles.accentDrawable).top().left().marginRight(15).marginLeft(15);
//            recipeShow.setWidth(40 * 5);
//
//            Table cont = new Table(Styles.black9).top().right().marginRight(5);
//            cont.setWidth(32 * tableRows);
//
//            cont.defaults().size(40);
//
//            if(currentPlan > -1){
//                for (int j = 0; j < getInputItems().size; j++) {
//                    ItemStack itemStack = getInputItems().get(j);
//                    recipeShow.add(new ReqImage(new ItemImage(itemStack.item.uiIcon, itemStack.amount), () -> true)).top();
//                    if (j % 5 == 4) recipeShow.row();
//                }
//            }
//
//            Runnable rebuild = () -> {
//                for (int i = 0; i < plans.size; i++) {
//                    int index = i;
//                    MultiPayloadCrafter.MultiPayloadPlan plan = plans.get(i);
//
//                    ImageButton button = cont.button(Tex.whiteui, Styles.clearNoneTogglei, 32, () ->
//                            control.input.config.hideConfig()).tooltip(plan.name).get();
//
//                    button.update(() -> {
//                        button.setChecked(currentPlan == index);
//                        button.getStyle().imageUp = new TextureRegionDrawable(content.units().get(1).fullIcon);
//                        button.changed(() -> currentPlan = (button.isChecked() ? index : -1));
//                        if(hoveredPlan != -1)return;
//                        button.hovered(() -> hoveredPlan = index);
//                    });
//                    if (i % tableRows == tableRows - 1) cont.row();
//                }
//
//                recipeShow.update(() -> {
//                    recipeShow.clear();
//                    if(hoveredPlan == -1 && currentPlan == -1)return;
//
//                    MultiPayloadCrafter.MultiPayloadPlan plan = plans.get(hoveredPlan > -1 ? hoveredPlan : currentPlan);
//
//                    Recipe inputRecipe = plan.inputRecipe;
//
//                    for (int j = 0; j < inputRecipe.itemStacks.size; j++) {
//                        ItemStack itemStack = inputRecipe.itemStacks.get(j);
//                        recipeShow.add(new ReqImage(new ItemImage(itemStack.item.uiIcon, itemStack.amount), () -> true)).top();
//                        if (j % 5 == 4) recipeShow.row();
//                    }
//                    hoveredPlan = -1;
//
//                });
//                table.add(recipeShow).growY();
//            };
//
//            rebuild.run();
//
//            Table scrollPane = new Table();
//            ScrollPane pane = new ScrollPane(cont, Styles.smallPane);
//            pane.setScrollingDisabled(false, false);
//
//            pane.setScrollYForce(block.selectScroll);
//            pane.update(() -> block.selectScroll = pane.getScrollY());
//
//            pane.setOverscroll(false, true);
//            scrollPane.add(pane).maxHeight(tableColumns * 40);
//
//            table.add(scrollPane);
        }


        @Override
        public void drawConfigure(){
        }

        @Override
        public void updateTile(){
            //Vars.content.block("duo").region = Core.atlas.find("ovulam-mortar-pod");
            if(power.status > 0 && timer.get(3 * 60f)){
                float amount = Math.max(liquidCapacity - liquids.get(Liquids.water), 0);
                liquids.add(Liquids.water, amount);
            }
        }

        @Override
        public void damage(float amount, boolean withEffect) {
            super.damage(amount, withEffect);
        }

        @Override
        public void draw(){
            //Blocks.doorLarge.chainEffect = true
            //Vars.state.teams.get(Vars.state.rules.defaultTeam).getUnits(UnitTypes.flare).each(u -> {});

            //Vars.state.rules.winWave = XXX;

            //Call.sendChatMessage(Time.EventTime);
            //world.tile()
            //Vars.state.rules.canGameOver = false
            /*
            Vars.state.rules.logicUnitBuild = false
            Vars.state.rules.reactorExplosions = false;

            Vars.state.rules.pvpAutoPause = false;
            Vars.state.rules.infiniteResources = true;

            Vars.state.rules.ghostBlocks = false;
            Vars.world.tiles.eachTile(function(tile) {if (tile.block() instanceof ConstructBlock) tile.build.kill();});
            Vars.state.rules.ghostBlocks = true;

            Font font = Fonts.outline;
            font.apply(String.valueOf(fullTime), x, y - 40, Align.center);

            Vars.content.unit(47).targetAir = true;
            Vars.content.unit(47).weapons.each(weapon => weapon.bullet.collidesAir = true);
            Vars.content.unit(52).targetAir = true;

            //Vars.world.tiles.eachTile(tile1 => {if(tile1.build instanceof ConstructBlock.ConstructBuild)tile1.build.remove();});
            Vars.player.unit().type.drag = 0.3;
            Vars.player.unit().type.flying = true;

            Events.run(EventType.Trigger.update, () => {
                Vars.player.unit().type.rotateMoveFirst;
                Vars.player.unit().type.strafePenalty = 1;
                Vars.player.unit().type.omniMovement = true;
                Vars.player.unit().type.rotateMoveFirst = false;
                Vars.player.unit().rotation = Vars.player.unit().type.rotateSpeed * Vars.player.unit().speedMultiplier * Time.time * 15;
            });

                        Events.run(EventType.Trigger.update, () => {
                if(!Vars.state.rules.pvp && Vars.state.rules.unitCapVariable && Vars.state.rules.unitCap + Vars.state.rules.defaultTeam.data().unitCap > 120){
                    Vars.state.rules.unitCapVariable = false;
                    Vars.state.rules.unitCap = 120;
                }

                
                        var time = 20;
        var timer = 20;

        Events.run(Trigger.update, () => {
            timer -= Time.delta;
            if(timer < 0){
                timer = time;

                var u = Vars.player.unit();
                var e = Fx.reactorExplosion;
                e.at(u.x, u.y);
            }
        });
             */


        }
    }
}
