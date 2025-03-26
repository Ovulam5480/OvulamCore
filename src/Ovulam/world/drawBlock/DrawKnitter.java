package Ovulam.world.drawBlock;

import arc.Core;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.struct.ObjectMap;
import mindustry.content.Items;
import mindustry.gen.Building;
import mindustry.graphics.Drawf;
import mindustry.graphics.Layer;
import mindustry.world.Block;
import mindustry.world.draw.DrawBlock;

public class DrawKnitter extends DrawBlock {
    public TextureRegion region, spindleRegion, iconRegion, nodeRegion, topRegion;
    //轴距离设定中心的半径, 轴本身的半径(节点围绕半径)根据轴数计算出
    public float spindleRadius;
    //轴的数量
    public int spindleAmount;
    //旋转速度倍率
    public float speedMultiplier;
    //中心
    public float centerRadiusFrom;
    public float centerRadiusTo;

    public ObjectMap<Building, Float> buildProgress = new ObjectMap<>();

    public DrawKnitter(float spindleRadius, int spindleAmount, float speedMultiplier, float centerRadius){
        this(spindleRadius, spindleAmount, speedMultiplier, centerRadius, centerRadius);
    }
    
    public DrawKnitter(float spindleRadius, int spindleAmount, float speedMultiplier,
                       float centerRadiusFrom, float centerRadiusTo){
        this.spindleRadius = spindleRadius;
        this.spindleAmount = spindleAmount;
        this.speedMultiplier = speedMultiplier;
        this.centerRadiusFrom = centerRadiusFrom;
        this.centerRadiusTo = centerRadiusTo;
    }

    @Override
    public void load(Block block){
        region = Core.atlas.find(block.name);
        topRegion = Core.atlas.find(block.name + "-top");
        spindleRegion = Core.atlas.find(block.name + "-spindle");
        iconRegion = Core.atlas.find(block.name + "-icon");
        nodeRegion = Core.atlas.find(block.name + "-node");
    }
    @Override
    public void draw(Building build){
        Draw.rect(region, build.x, build.y);
        if(!buildProgress.containsKey(build))buildProgress.put(build, 0f);

        //最后记录的半径
        float progress = buildProgress.get(build);
        //根据进程,获得的半径
        float centerRadius = Mathf.lerp(centerRadiusFrom, centerRadiusTo, build.progress());

        float realRadius = Mathf.approach(progress, centerRadius, Math.abs(progress - centerRadius) * 0.1f);
        buildProgress.put(build, realRadius);

        Draw.z(Layer.blockBuilding - 1f);
        Draw.rect(topRegion, build.x, build.y);

        for (int i = 0; i < spindleAmount; i++){
            Draw.z(Layer.blockBuilding - 0.9f);
            int pow = Mathf.pow(-1, i);

            //正多边形内角的角度
            float sizeAngle = (1 - 2f / spindleAmount) * 360;

            //建筑的totalProgress(用于旋转, pow控制旋转方向) + 初始值(用于初始放置,调整错位)
            float rotate = pow * build.totalProgress() * speedMultiplier + 180f / spindleAmount - sizeAngle * Mathf.floor(i / 2f);

            float sx = (float) (Math.cos(360f * i / spindleAmount * Mathf.degreesToRadians) * spindleRadius);
            float sy = (float) (Math.sin(360f * i / spindleAmount * Mathf.degreesToRadians) * spindleRadius);

            Drawf.spinSprite(spindleRegion, build.x + sx, build.y + sy, rotate - 45);

            Draw.z(Layer.blockBuilding - 0.8f);

            float nodeRadius = (float) (Math.cos((90 - 180f / spindleAmount) * Mathf.degreesToRadians) * spindleRadius);

            for (int j = 0; j < 4; j ++){
                float rot = rotate + j * 90;
                boolean b = j % 2 == 0;

                float lx = (float) (Math.cos(rot * Mathf.degreesToRadians) * nodeRadius);
                float ly = (float) (Math.sin(rot * Mathf.degreesToRadians) * nodeRadius);

                float centerAngle = Mathf.angle(sx + lx, sy + ly) - Mathf.sign(b) * 90;
                float lx2 = (float) (Math.cos(centerAngle * Mathf.degreesToRadians) * realRadius);
                float ly2 = (float) (Math.sin(centerAngle * Mathf.degreesToRadians) * realRadius);

                float angle = rot - 360f * i / spindleAmount;

                //Lines.stroke();

                if(angleWithin(angle, 67.5f) != (pow == 1) && b ||
                        angleWithin(angle, 67.5f) == (pow == 1) && !b){
                    Draw.rect(nodeRegion, build.x + sx + lx, build.y + sy + ly);
                    Draw.color(Items.phaseFabric.color);
                    Lines.line(build.x + sx + lx, build.y + sy + ly,
                            build.x + lx2, build.y + ly2);
                }
                Draw.reset();
            }
        }
    }

    public boolean angleWithin(float angle, float targetAngle){
        return Math.abs(Mathf.mod(angle, 360) - 180) < targetAngle;
    }

    @Override
    public TextureRegion[] icons(Block block){
        return new TextureRegion[]{iconRegion};
    }
}