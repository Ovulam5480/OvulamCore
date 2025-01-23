package Ovulam.entities.units.MultiSegment;

import Ovulam.entities.units.OvulamUnitType;
import arc.math.Mathf;
import arc.math.geom.Vec2;
import arc.struct.IntMap;
import arc.struct.Seq;
import arc.util.Tmp;
import mindustry.content.UnitTypes;
import mindustry.gen.Unit;
import mindustry.type.UnitType;
import mindustry.type.Weapon;

//多体节单位
public class TreeUnitType extends OvulamUnitType {
    public IntMap<Seq<TreeUnitTypePart>> node;

    public TreeUnitType(String name, IntMap<Seq<TreeUnitTypePart>> node) {
        super(name);
        wobble = false;
        engineSize = 0;

        flying = true;
        health = 80000f;

        physics = false;

        this.node = node;
    }

    @Override
    public void load() {
        super.load();

    }

    @Override
    public void init() {
        super.init();
        node.values().toArray().each(a -> a.each(part -> {
            if(part.type == null)part.type = this;
            if(part.mirrorX){
                TreeUnitTypePart p = part.copy();
                p.x = -p.x;
                p.x2 = -p.x2;
                p.mirrorX = false;
                a.add(p);
            }

            if(part.mirrorY){
                TreeUnitTypePart p = part.copy();
                p.y = -p.y;
                p.y2 = -p.y2;
                p.mirrorY = false;
                a.add(p);
            }
        }));
    }

    public static void setRotation(TreeUnit node, TreeUnitTypePart part, Unit root){
        float partRot = Mathf.lerp(part.rotation, part.rotation2, part.partMove ? part.progress.get(node) : 0);
        float angle = Mathf.angle(node.x - root.x, node.y - root.y);

        node.rotation(angle + partRot);
    }

    public static Vec2 getFramePos(TreeUnit node, TreeUnitTypePart part, TreeUnit root, Vec2 out){
        return getPartPos(part.partMove ? part.progress.get(node) : 0, part, out).rotate(root.rotation - 90).add(root.framePos);
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
}
