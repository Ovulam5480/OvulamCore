package Ovulam.UI;

import Ovulam.OvulamCore;
import arc.Core;
import arc.graphics.Color;
import arc.graphics.Texture;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.struct.Seq;
import arc.util.Log;
import mindustry.Vars;
import mindustry.graphics.Drawf;
import mindustry.ui.Styles;
import mindustry.ui.WarningBar;

public class EventAnimation{
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

    public int giantsAmount = 4;
    public float totalHeight = 400f;
    public String name = "点击输入文字";

    public Table iconTable;
    public Table[] giants;
    public Table rightTable, leftTable;
    public Seq<Table> info = new Seq<>();

    public TextureRegion icon;
    public Color color = new Color();

    public EventAnimation(String name) {
        this.name = name;

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

        icon = Core.atlas.find(OvulamCore.OvulamCoreName() + name);

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
        iconTable.image(new TextureRegionDrawable(icon)).size(iconSize);

        rightTable = setSizeTable(true);
        leftTable = setSizeTable(false);

        rightTable.add("This is right", 2).style(Styles.outlineLabel).center();
        leftTable.add("这也是右边", 2).style(Styles.outlineLabel).center();

        info.add(iconTable);
        info.add(rightTable).add(leftTable);

        for(Table g : giants){
            Vars.ui.hudGroup.addChild(g);
        }

        info.each(c -> Vars.ui.hudGroup.addChild(c));
        setVisible(false);
    }

    public Table setSizeTable(boolean isRight){
        Table table = new Table();
        float y = (Core.graphics.getHeight() - totalHeight) / 2f;

        table.setSize((Core.graphics.getWidth() - iconSize) / 2f, totalHeight);
        table.setPosition(isRight ? 0 : (Core.graphics.getWidth() + iconSize) / 2f, y);

        return table;
    }

    public void applyTable(float frame){
        float fin = frame / animationTime;
        Log.info(fin);

        for (int i = 0; i < giantsAmount; i++) {
            Table g = giants[i];

            float x = Core.graphics.getWidth() * Mathf.pow(-1, i);
            float y = totalHeight / giantsAmount * (i - giantsAmount / 2f) + Core.graphics.getHeight() / 2f;

            giants[i] = horizontalMovement(g, x, y, Mathf.curve(fin, 0, giantsEnd));
        }

        iconTable.getChildren().first().setColor(iconColor(fin));
    }

    public void setVisible(boolean visible){
        for(Table g : giants){
            g.visible(() -> visible);
        }

        info.each(c -> c.visible(() -> visible));
    }

    public float pad() {
        return totalHeight / giantsAmount - warmingBarHeight;
    }

    public Color iconColor(float fin){
        return color.set(from).lerp(to, circulate(fin)).a(iconAlpha(fin));
    }

    public float iconAlpha(float fin) {
        return Mathf.curve(fin, iconStart, iconEnd);
    }

    public float circulate(float fin) {
        return Mathf.curve(fin, colorStart, colorEnd);
    }

    public Table horizontalMovement(Table giant, float x1, float y, float progress) {
        float x = Mathf.lerp(x1, 0, progress);
        giant.setPosition(x, y);

        return giant;
    }
}
