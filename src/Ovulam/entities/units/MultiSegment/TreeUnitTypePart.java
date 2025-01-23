package Ovulam.entities.units.MultiSegment;

import arc.func.Floatc4;
import arc.func.Floatf;
import arc.graphics.Color;
import arc.math.Mathf;
import arc.util.Time;
import mindustry.graphics.Drawf;
import mindustry.type.StatusEffect;

//nodeManager
public class TreeUnitTypePart implements Cloneable{
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

    //"折断"的角度差
    public float fractureAngle = 80f;
    //部位瞬移所需的最小角度差
    public float minSettingAngle = 45f;
    //在最小角度差内, 部位回归到应当位置的力度
    public float homingLerp = 0.01f;
    // 部位回归的角度
    public float minHomingAngle = 10f;

    //该部位存在时, 根无法成为攻击目标
    public boolean rootTargetable;
    public boolean rootHittable;

    //该部位在根单位的生成时立即生成
    public boolean immediatelyAdd = false;
    //是否基于核心单位作为次级核心单位
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
    //速度(只有核心单位能获得加成)
    speedBoost = -0.001f, speedMultiBoost,
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

        rotation = rotation2 = y < 0 ? 180 : 0;
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

    public void drawLink(float x1, float y1, float x2, float y2){
        Drawf.dashLine(Color.pink, x1, y1, x2, y2);
    }

    public void load(){
    }

    public TreeUnitTypePart copy(){
        try{
            return (TreeUnitTypePart) clone();
        }catch(CloneNotSupportedException suck){
            throw new RuntimeException("man, what can i say!", suck);
        }
    }
}