package Ovulam;

/*
火焰的攻击会受到布墙反弹
铅分裂,硫蜂群不能有效攻击T4T5空军, 废料分裂,爆混蜂群不能有效攻击T5空军
裂解重设子弹目标有1/12秒的间隔, 导致裂解无法应对大量的子弹
浪涌子弹的平均落点位置是浪涌到攻击目标的0.9距离
子弹击中特效有时候并不代表子弹的溅射, 比如爆混气旋的子弹溅射范围实际上跟泰坦差不多
对付泰坦最好的方法就是另一个泰坦

闪电实际上是一次生成大量的子弹, 因此闪电攻击对力墙的效果极佳
闪电生成的闪电子弹的数量是闪电长度的一半向下取整
持续性激光一秒造成12次攻击, 也就是说, 持续性激光要吃12次护甲, 龙王的激光单次伤害只有26
对于抛射炮子弹本体伤害, 冰雹与浪涌是20或者25, 飞鲨是20, 戟鲸是15, 泰坦是350
对于轰炸子弹本体伤害天垠是0, 雷霆是1, 力墙能够完全保护范围内的建筑不受伤害

龙王预估DPS为2.3K, 领主DPS为162, 但是领主不但能杀龙王还能剩一半血量
压制场的视觉效果就是T4T5飞船中的圆球, T5飞船实际上有三个压制场, 并且两边的压制场不进行工作, 纯粹为了好看
水电反应的所有伤害在V8中将会受到护甲减免, 其他反应仍然是真实伤害
反应由作为影响因素的状态效果进行, 比如油火反应中, 打反应伤害的是石油
死星射击时,每隔4.375格产生一次最低5长度, 最高20长度, 伤害为50的闪电
硫火焰的DPS与海神差不多
合金气旋和钍幽灵上冷冻液在气旋不用水电反应的情况下, 同样打高甲单位时, 气旋伤害仍然能比幽灵高一大截
空军单位坠落造成的溅射伤害半径是单位的碰撞体积半径的两倍(公式实际上就是一个接近2的函数)
空军单位坠落伤害是无视护甲, 能够贯穿的范围伤害, 建筑体积越大, 受到的伤害越多
埃里克尔飞船T3以下单位攻击高甲敌人时效果非常不好
埃里克尔T4飞船与T5飞船本体的压制场范围一致, T5飞船炮弹的压制场范围直径等于再生投影对角线长度

龙王主炮无法加速瘤变反应堆, 因为瘤变反应堆压根不能被超速, 所有的发热建筑也都无法被超速
埃里克尔冲击钻头的挖矿效率不如赛普罗的气动钻头, 要的水也是气动的三倍多, 而且耗电也多
一个小石墨厂配4个煤炭机械钻头, 一个玻璃厂配5个沙机械钻头, 两个小硅厂配15个沙机械钻头
埃里克尔一个电解厂供2个T2厂的氢, 一个布热的氮气正好供一个T3厂
组装厂需要的氰气效率是400%氰厂(坦克是300%), T5需要的碳化物速率是600%碳化物厂
矿渣热合金中, 一半多的矿渣用来制热
将煤炭转石墨再供电弧硅, 对煤炭和沙的利用率是小硅的两倍
载荷装载器和卸载器能够运输3*3的载荷, 并且可以通过激光节点或者电池传输电力
钻头不加水, 等于没加水, 钻机不加氢,等于没加氢
RTG与四个火力发电差不多
埃里克尔核心会销毁输入的无法用于建造的物品, 例如煤炭,硫,爆混
赛普罗的高级核心核心铺在低级核心时不受核心地板限制, 埃里克尔则必须要求核心内覆盖核心地板
埃里克尔的矿渣焚烧机无法焚烧瘤液, 瘤液描述中的"矿渣池"指的是矿渣地板

交叉器,传送带桥不消耗钛, 但是运输速度都略大于11, 能够在没有钛资源的情况下大量运输资源
在赛普罗中, 带桥末端,布桥末端(物品),质驱本体的主动输出速率是不受帧数影响的60/秒
质驱本体可以通过额外加卸载器被动输出, 但是不要只用卸载器输出
质驱在+150%的超速情况下, 子弹从最远距离发射到接收的时间与发射质驱的冷却时间一致
路由器输出速度是帧数上限(统称为60), 路由器串联或者路由器与光传原件串联会被限制为7.5物品/秒
运输大量流体建议使用流体容器配合液体交叉器
液体带桥和布液体带桥的运量一致
液体系统的运量都与帧数相关, 如果服务器的TPS低于40则不建议使用冲反发电

//todo 钍反冲反通量瘤变爆炸范围和爆炸伤害
//下面是新加入的
构筑器建造速度为60%, 大型构筑器建造速度为75%, 解构器拆解速度为100%, 大型解构器拆解速度为200%
载荷传送带运输速度为4格/秒, 强化载荷传送带为5.14格/秒, 其他可承载载荷的方块运输速度是5.25格/秒
T4组装厂需要需要8.16~9.91秒来输入载荷, T5厂需要15.17秒分别输入13个载荷, 因此对应碳化物厂效率为553.3%
载荷质驱有3.67秒的发射间隔, 大型载荷质驱有3.83秒的发射间隔
 */