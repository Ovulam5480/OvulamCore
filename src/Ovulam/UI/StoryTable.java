package Ovulam.UI;

import arc.Core;
import arc.graphics.g2d.TextureRegion;
import arc.scene.style.TextureRegionDrawable;
import arc.scene.ui.layout.Table;
import arc.util.Time;
import mindustry.gen.Icon;
import mindustry.gen.Tex;
import mindustry.ui.Styles;

public class StoryTable {
    public Table storyTable = new Table();
    
    public int wordsIndex = 0;
    public int stageIndex = 0;
    public int stagesAmount;
    
    public float timer;
    public float interval = 10f;

    public String[] stageTexts;
    public TextureRegion[] stageRegions;

    //[string1, region1, string2, region2...]
    public StoryTable(Object... objects){
        stagesAmount = objects.length/2;
        stageTexts = new String[stagesAmount];
        stageRegions = new TextureRegion[stagesAmount];

        for (int i = 0; i < stagesAmount; i++){
            stageRegions[i] = (TextureRegion) objects[i * 2];
            stageTexts[i] = (String) objects[i * 2 + 1];
        }

        rebuild();
    }

    void rebuild(){
        storyTable.setHeight(200);
        storyTable.setWidth(400);
        storyTable.setPosition(0, Core.scene.getHeight()/2f - 200);

        storyTable.fill(Styles.black6, table -> {
            timer = Time.time;

            table.table(table1 -> table1.update(() -> {
                table1.clear();

                table1.image(new TextureRegionDrawable(stageRegions[stageIndex], 3)).left().padLeft(20).padTop(10f);

                String test = stageTexts[stageIndex].substring(0, wordsIndex);
                table1.add(test).top().padLeft(20).padTop(10).wrap().width(220).row();

                table1.image(Tex.underline2).width(360f * wordsIndex / stageTexts[stageIndex].length()).left().padLeft(20);

                if (wordsIndex < stageTexts[stageIndex].length() && Time.time - timer > interval) {
                    wordsIndex += 1;
                    timer = Time.time;
                }
            })).row();


            table.table(table1 -> {
                table1.left();

                table1.button(Icon.left,() -> wordsIndex = 0).padLeft(18f).padRight(10);
                table1.button(Icon.right,() -> {
                    if(wordsIndex < stageTexts[stageIndex].length()) wordsIndex = stageTexts[stageIndex].length();
                    else if(stageIndex < stagesAmount - 1){
                        wordsIndex = 0;
                        stageIndex++;
                    }
                    else storyTable.clear();
                });
            });
        });
    }
}
