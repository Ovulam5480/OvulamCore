package Ovulam.entities.Unit.MultiSegment;

import Ovulam.entities.Unit.OvulamUnitType;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.gen.Unit;

//多体节单位
public class TreeUnitType extends OvulamUnitType {
    public IntMap<Seq<TreeUnitTypePart>> node;

    public TreeUnitType(String name, IntMap<Seq<TreeUnitTypePart>> node) {
        super(name);
        wobble = false;
        engineSize = 0;

        flying = true;
        health = 80000f;

        this.node = node;
    }

    @Override
    public void load() {
        super.load();

        //todo 待修改
        //node.values().toArray().each(a -> a.each(TreeUnitTypePart::load));
    }

    @Override
    public void init() {
        super.init();
        node.values().toArray().each(a -> a.each(part -> {
            if(part.type == null)part.type = this;
        }));
    }

    public static void setRotation(TreeUnit node, TreeUnitTypePart part, Unit root, float progress){
        float partRot = Mathf.lerp(part.rotation, part.rotation2, part.partMove ? part.progress.get(node) : 0);
        float angle = Mathf.angle(node.x - root.x, node.y - root.y);
        float targetRot = angle + partRot;

        float nodeRot = node.rotation;

        if(Math.abs((targetRot - nodeRot + 360 + 180) % 360 - 180) < part.minAngle)return;

        node.rotation(Mathf.slerp(nodeRot, targetRot, progress));
    }

    public static void setPosition(TreeUnit node, TreeUnitTypePart part, Unit root, float progress){
        getPartPos(part.partMove ? part.progress.get(node) : 0, part, Tmp.v1).rotate(root.rotation - 90).add(root);

        node.set(lerpPolar(Tmp.v2.set(node), Tmp.v1, progress));
    }

    public static Vec2 getPartPos(float progress, TreeUnitTypePart part, Vec2 out){
        out.set(Mathf.lerp(part.x, part.x2, progress), Mathf.lerp(part.y, part.y2, progress));
        return out;
    }

    public static float getInferRotation(TreeUnitTypePart part, Unit root){
        float partRot = Mathf.lerp(part.rotation, part.rotation2, part.partMove ? 0.5f : 0);
        float angle = getPartPos(part.partMove ? 0.5f : 0, part, Tmp.v1).angle();

        return angle + partRot + root.rotation + 180f;
    }

    public static Vec2 lerpPolar(Vec2 from, Vec2 to, float alpha){
        float len = Mathf.lerp(from.len(), to.len(), alpha);
        float ang = Mathf.slerp(from.angle(), to.angle(), alpha);
        from.trns(ang, len);
        return from;
    }
}
