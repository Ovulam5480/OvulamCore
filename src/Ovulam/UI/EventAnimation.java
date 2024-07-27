package Ovulam.UI;

import Ovulam.OvulamMod;
import arc.Core;
import arc.Events;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.Scaled;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Time;
import mindustry.Vars;
import mindustry.game.EventType;
import mindustry.ui.Styles;
import mindustry.ui.WarningBar;

public class EventAnimation implements Scaled {
    public float animationTime = 360f;
    public float warmingBarHeight = 24f;
    public float iconSize = 512f;

    public float giantsEnd = 0.1f;

    public float iconStart = 0.12f;
    public float iconEnd = 0.25f;

    public float colorStart = 0.4f;
    public float colorEnd = 0.6f;

    public boolean colorCirculate = false;
    public Color from = Color.white;
    public Color to = Color.white;

    public int giantsAmount;
    public float totalHeight;
    public String name;

    public Table iconTable;
    public Table[] giants;
    public Table rightTable, leftTable;
    public Seq<Table> childs = new Seq<>();

    public float timer;
    public boolean hasInit;
    public TextureRegion icon;
    public Color color = new Color();

    public EventAnimation(String name){
        this(4, 400f, name);
    }

    public EventAnimation(int giantsAmount, float totalHeight, String name) {
        this.giantsAmount = giantsAmount;
        this.totalHeight = totalHeight;
        this.name = name;

        init();
        run();
    }

    public void init() {
        //用于文字的抗锯齿设置
        for (Texture tex : Core.atlas.getTextures()) {
            Texture.TextureFilter filter = Texture.TextureFilter.nearest;
            tex.setFilter(filter, filter);
        }
        if (Core.settings.getBool("linear")) {
            for (Texture tex : Core.atlas.getTextures()) {
                Texture.TextureFilter filter = Texture.TextureFilter.linear;
                tex.setFilter(filter, filter);
            }
        }

        icon = Core.atlas.find(OvulamMod.OvulamModName() + name);

        giants = new Table[giantsAmount];
        for (int i = 0; i < giantsAmount; i++) {
            Table g = new Table();
            giants[i] = g;
            g.setSize(Core.graphics.getWidth(), totalHeight / giantsAmount);
            g.setBackground(Styles.black6);

            if (i == 0) g.add(new WarningBar()).growX().height(warmingBarHeight).padTop(pad());
            else if (i == giantsAmount - 1) {
                g.add(new WarningBar()).growX().height(warmingBarHeight).padBottom(pad()).row();
            }
        }

        iconTable = new Table();
        iconTable.setPosition((Core.graphics.getWidth() - iconSize) / 2f, (Core.graphics.getHeight() - iconSize) / 2f);
        iconTable.setSize(iconSize);

        rightTable = setSizeTable(true);
        leftTable = setSizeTable(false);

        childs.add(iconTable);
        childs.add(rightTable).add(leftTable);
    }

    public Table setSizeTable(boolean isRight){
        Table table = new Table();
        float y = (Core.graphics.getHeight() - totalHeight) / 2f;

        table.setSize((Core.graphics.getWidth() - iconSize) / 2f, totalHeight);
        table.setPosition(isRight ? 0 : (Core.graphics.getWidth() + iconSize) / 2f, y);

        return table;
    }

    void run() {
        Events.run(EventType.Trigger.update, () -> {
            if (timer > animationTime) {
                if(hasInit)stop();
                return;
            }

            timer += Vars.state.isPaused() ? 0 : Time.delta;

            for (int i = 0; i < giantsAmount; i++) {
                Table g = giants[i];

                float x = Core.graphics.getWidth() * Mathf.pow(-1, i);
                float y = totalHeight / giantsAmount * (i - giantsAmount / 2f) + Core.graphics.getHeight() / 2f;

                giants[i] = horizontalMovement(g, x, y, curve(0, giantsEnd));
            }

            iconTable.clear();
            iconTable.image(new TextureRegionDrawable(icon).tint(iconColor())).size(iconSize);

            rightTable.clear();
            leftTable.clear();

            rightTable.add("This is right", 2).style(Styles.outlineLabel).center();
            leftTable.add("这是右边", 2).style(Styles.outlineLabel).center();
        });
    }

    public float pad() {
        return totalHeight / giantsAmount - warmingBarHeight;
    }

    public Color iconColor(){
        return color.set(from).lerp(to, circulate()).a(iconAlpha());
    }

    public float iconAlpha() {
        return curve(iconStart, iconEnd);
    }

    public float circulate() {
        return curve(colorStart, colorEnd);
    }

    public void reset() {

        if(!hasInit){
            for(Table g : giants){
                Vars.ui.hudGroup.addChild(g);
            }
            childs.each(c -> Vars.ui.hudGroup.addChild(c));
            hasInit = true;
        }
        timer = 0;
    }

    public void stop() {
        for(Table g : giants){
            Vars.ui.hudGroup.removeChild(g);
        }
        childs.each(c -> Vars.ui.hudGroup.removeChild(c));
        hasInit = false;
    }

    public Table horizontalMovement(Table giant, float x1, float y, float progress) {
        float x = Mathf.lerp(x1, 0, progress);
        giant.setPosition(x, y);

        return giant;
    }

    public float curve(float from, float to){
        return Mathf.curve(fin(), from, to);
    }


    @Override
    public float fin() {
        return timer / animationTime;
    }
}
