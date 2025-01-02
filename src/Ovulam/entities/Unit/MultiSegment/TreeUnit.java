package Ovulam.entities.Unit.MultiSegment;

import Ovulam.entities.Unit.OvulamUnit;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Font;
import arc.scene.ui.layout.Table;
import arc.struct.ObjectMap;
import arc.struct.Seq;
import arc.util.Align;
import arc.util.Nullable;
import arc.util.Time;
import arc.util.Tmp;
import mindustry.ai.types.CommandAI;
import mindustry.content.Fx;
import mindustry.entities.units.UnitController;
import mindustry.game.Team;
import mindustry.gen.Hitboxc;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.type.UnitType;
import mindustry.ui.Fonts;

public class TreeUnit extends OvulamUnit {
    public ObjectMap<TreeUnit, TreeUnitTypePart> nodePartMap = new ObjectMap<>();

    public ObjectMap<TreeUnitTypePart, Float> constructingPart = new ObjectMap<>();

    public Seq<TreeUnitTypePart> nodeParts;

    //非核心节点
    public @Nullable TreeUnit root;
    public TreeUnit treeRoot;
    public int number;

    //核心节点
    public @Nullable ObjectMap<TreeUnit, TreeUnitTypePart> nodes;
    public float damageMulti,
            speedBoost, speedMulti,
            healthMulti,
            armorBoost,
            repairAmount, repairPercent;

    @Override
    public TreeUnitType getType() {
        return (TreeUnitType) type;
    }

    @Override
    public boolean hittable() {
        return super.hittable();
    }

    @Override
    public boolean targetable(Team targeter) {
        return super.targetable(targeter);
    }

    public void add(TreeUnit root, TreeUnit treeRoot, int number){
        this.root = root;
        this.treeRoot = treeRoot;
        this.number = number;

        this.add();
    }

    @Override
    public void add() {
        super.add();

        if(isTreeRoot()){
            nodes = new ObjectMap<>();
            treeRoot = this;
        }

        for (int item : getType().node.keys().toArray().items) {
            if(item > number){
                nodeParts = getType().node.get(item);
                nodeParts.each(part -> {
                    if(part.immediatelyAdd)addNodeUnit(part);
                    else if(part.constructTime > 0)constructingPart.put(part, 0f);
                });
                break;
            }
        }
    }

    public TreeUnit addNodeUnit(TreeUnitTypePart part){
        UnitType type = part.type;
        int nodeNumber = type == getType() ? number + 1 : part.initNumber;

        Unit unit = type.create(team);
        if (!(unit instanceof TreeUnit n)) return null;

        nodePartMap.put(n, part);

        TreeUnitType.setPosition(n, part, this, 1f);
        TreeUnitType.setRotation(n, part, this, 1f);

        //todo 待测
        n.add(part.asRoot ? null : this, treeRoot, nodeNumber);

        n.treeRoot.nodes.put(n, part);
        return n;
    }

    @Override
    public void update() {
        super.update();

        damageMulti = speedBoost = speedMulti = healthMulti = armorBoost = repairAmount = repairPercent = 0;

        nodePartMap.each((unit, part) -> {
            TreeUnitType.setPosition(unit, part, this, part.lerpProgress * Time.delta);
            TreeUnitType.setRotation(unit, part, this, part.lerpProgress * Time.delta);
        });

        if(dead)return;

        constructingPart.each((part, time) -> {
            time += Time.delta;

            if(time > part.constructTime){
                TreeUnit t = addNodeUnit(part);
                Fx.spawn.at(t.x, t.y);

                constructingPart.remove(part);
            }else constructingPart.put(part, time);
        });

        if(isTreeRoot())nodes.values().toSeq().each(this::countBoost);

        if(treeRoot.repairAmount > 0)heal(treeRoot.repairAmount);
        if(treeRoot.repairPercent > 0)heal(treeRoot.repairPercent * maxHealth);

    }

    public void countBoost(TreeUnitTypePart part){
        damageMulti += part.damageMultiBoost;
        speedBoost += part.speedBoost;
        speedMulti += part.speedMultiBoost;
        healthMulti += part.healthMultiBoost;
        armorBoost += part.armorAmount;
        repairAmount += part.repairAmount;
        repairPercent += part.repairPercent;
    }

    public void show(){
        Font font = Fonts.outline;
        font.draw(String.valueOf(damageMultiplier()), x, y - 40, Align.center);
        font.draw(String.valueOf(speedBoost), x, y - 60, Align.center);
        font.draw(String.valueOf(speedMulti), x, y - 60, Align.center);
    }

    @Override
    public void draw() {
        super.draw();

        if(isTreeRoot())show();

        nodePartMap.each((u, t) -> t.drawLink.get(x, y, u.x, u.y));

        Draw.draw(Layer.flyingUnit, () -> {
            constructingPart.each((part, time) -> {
                TreeUnitType.getPartPos(part.partMove ? 0.5f : 0, part, Tmp.v1).rotate(rotation - 90).add(this);

                part.drawConstruct.get(Tmp.v1.x, Tmp.v1.y, TreeUnitType.getInferRotation(part, this), time / part.constructTime);
            });
        });
    }
    
    public boolean isTreeRoot(){
        return treeRoot == this;
    }

    @Override
    public void collision(Hitboxc other, float x, float y) {
        if(!(other instanceof TreeUnit u && u.treeRoot == treeRoot)) super.collision(other, x, y);
    }

    @Override
    public CommandAI command() {
        return super.command();
    }

    @Override
    public void remove() {
        if(!isTreeRoot()){
            treeRoot.nodes.remove(this);
            TreeUnitTypePart part = root.nodePartMap.get(this);

            if(part.constructTime > 0)root.constructingPart.put(part, 0f);
            root.nodePartMap.remove(this);
        }
        super.remove();
    }

    @Override
    public float damageMultiplier() {
        return treeRoot.damageMulti + super.damageMultiplier();
    }

    @Override
    public float speedMultiplier() {
        return treeRoot.speedMulti + super.speedMultiplier();
    }

    @Override
    public float speed() {
        return treeRoot.speedBoost + super.speed();
    }

    @Override
    public float healthMultiplier() {
        return treeRoot.healthMulti + super.healthMultiplier();
    }

    @Override
    public float armor() {
        return treeRoot.armorBoost + super.armor();
    }

    @Override
    public void display(Table table) {
        if(!isTreeRoot()) root.display(table);
        else super.display(table);
    }

    @Override
    public void controller(UnitController next) {
        if(!isTreeRoot())treeRoot.controller(next);
        else super.controller(next);
    }

    @Override
    public void rawDamage(float amount) {
        if(!isTreeRoot())root.rawDamage(amount);
        else super.rawDamage(amount);
    }

    @Override
    public void kill() {
        Time.run(3f, () -> {
            for (TreeUnit key : nodePartMap.keys()) {
                key.kill();
            }
        });

        super.kill();
    }
}
