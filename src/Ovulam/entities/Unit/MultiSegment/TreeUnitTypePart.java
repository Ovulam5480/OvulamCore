package Ovulam.entities.Unit.MultiSegment;

import arc.func.Floatc4;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;

public class TreeUnitTypePart {
    public TreeUnitType type;
    //部位在根单位的相对位置, 单位默认角度为相对位置的角度
    public float x, y;
    //部位相对于根单位的初始角度, 如果单位在"后方"生成同类需要设为180
    public float rotation;
    //部位是否镜像
    public boolean mirrorX, mirrorY;
    //部位的单位的初始编号
    public int initNumber;
    //是否循环移动
    public boolean partMove;
    //循环移动的相对位置, 角度
    public float x2, y2, rotation2;
    //循环移动的进程函数
    public Floatf<TreeUnit> progress;


    //部位跟随根单位的力度
    //todo 待改进?
    public float lerpProgress = 0.3f;
    //部位旋转所需的最小角度差, 最大应当为180
    //不建议改为0, 可能会造成末端剧烈抖动
    public float minAngle = 0.4f;
    //部位与节点之间的链接绘制
    public Floatc4 drawLink = (x1, y1, x2, y2) -> Drawf.dashLine(Color.pink, x1, y1, x2, y2);


    //该部位在根单位的生成时立即生成
    public boolean immediatelyAdd = false;
    //是否独立于核心作为跟单位
    public boolean asRoot;
    //该部位死亡或未生成时, 根单位构造该部位需要的时间,
    public float constructTime = 1 * 60f;
    //部位构造动画
    public Floatc4 drawConstruct = (x, y, rotation, progress) -> Drawf.construct(x, y, type.fullIcon, rotation, progress, progress, Time.time);


    //部位为全体单位(非核心单位直接设置的位置)提供的加成, 同种直接为加算
    //Boost, MultiBoost(加算与乘算, 先加再乘)
    public float
    //攻击
    damageMultiBoost = 0.2f,
    //速度(虽然只有核心单位能够移动)
    speedBoost = 0.05f, speedMultiBoost,
    //血量
    healthMultiBoost,
    //护甲
    armorAmount = 2f,
    //自动回血
    repairAmount, repairPercent;
    //状态效果
    public StatusEffect effect;


    public TreeUnitTypePart(TreeUnitType type, float x, float y) {
        this.type = type;
        this.x = x;
        this.y = y;

        load();
    }

    public TreeUnitTypePart(TreeUnitType type, float x, float y, float rotation) {
        this(type, x, y);
        this.rotation = rotation;
    }

    public TreeUnitTypePart(TreeUnitType type, int angle, float radius) {
        this(type, Mathf.cosDeg(angle) * radius, Mathf.sinDeg(angle) * radius);
    }

    public TreeUnitTypePart(float x, float y) {
        this(null, x, y);
        rotation = rotation2 = y < 0 ? 180 : 0;
    }


    public TreeUnitTypePart(int angle, float radius) {
        this(Mathf.cosDeg(angle) * radius, Mathf.sinDeg(angle) * radius);
    }

    public void setIncludedAngle(float rotation){
        //float angle = Mathf.angle();
    }

    public void load(){
    }
}