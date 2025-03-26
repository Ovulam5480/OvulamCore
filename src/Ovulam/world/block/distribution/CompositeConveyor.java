package Ovulam.world.block.distribution;

import Ovulam.math.OvulamMath;
import arc.graphics.g2d.Draw;
import arc.graphics.g2d.Lines;
import arc.graphics.g2d.TextureRegion;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.ObjectIntMap;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Nullable;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.content.Items;
import mindustry.core.World;
import mindustry.gen.Building;
import mindustry.gen.Teamc;
import mindustry.gen.Unit;
import mindustry.type.Item;
import mindustry.world.blocks.distribution.Conveyor;
import mindustry.world.blocks.distribution.StackConveyor;
import mindustry.world.blocks.payloads.Payload;
import mindustry.world.blocks.payloads.PayloadBlock;
import mindustry.world.blocks.payloads.PayloadConveyor;
import mindustry.world.meta.Stat;

import java.util.Arrays;

import static mindustry.Vars.itemSize;
import static mindustry.Vars.tilesize;

public class CompositeConveyor extends PayloadConveyor {
    public int tileCapacity = 3;

    //在允许输出的40%时间内, 如果物品移动的距离, 能够到达输出边, 则能够输出
    public float itemsSpeed = 1f;

    public TextureRegion stackRegion;

    public CompositeConveyor(String name) {
        super(name);
        hasItems = true;
        unloadable = false;

        //conveyorPlacement = true;
    }

    @Override
    public void setStats() {
        super.setStats();
        stats.add(new Stat("单次物品运输移动的列数"), validLines());
    }

    @Override
    public void init() {
        super.init();
        itemCapacity = tileCapacity * size * size;
    }

    @Override
    public void load() {
        super.load();
        stackRegion = ((StackConveyor) Blocks.plastaniumConveyor).stackRegion;
    }

    public float validLines() {
        return moveTime * 0.4f / (itemsSpeed * tilesize);
    }

    public float radius() {
        return (size - 1) / 2f;
    }}

    /*
    public class CompositeConveyorBuild extends PayloadConveyorBuild implements StackManager<>{
        private final Vec2 tmp1 = new Vec2(), tmp2 = new Vec2();

        private final Vec2 target = new Vec2();
        public PositionItemStack[] itemStacks = new PositionItemStack[size * size];
        public int[] maxSize = new int[size];
        public Building[] nexts = new Building[size];

        //输入物品的来源建筑
        public ObjectIntMap<Building> sources = new ObjectIntMap<>();

        @Override
        public boolean canControlSelect(Unit unit) {
            return super.canControlSelect(unit) && availablePayload();
        }

        @Override
        public void onProximityUpdate() {
            noSleep();
            //输出
            next = null;
            blocked = false;
            Arrays.fill(nexts, null);

            int d4x = Geometry.d4x(rotation);
            int d4y = Geometry.d4y(rotation);

            for (int i = 0; i < size; i++) {
                float o = (i - radius()) * tilesize;
                float r = (size / 2f + 0.1f) * tilesize;

                Building n = Vars.world.buildWorld(x + d4x * r + d4y * o, y + d4y * r - d4x * o);
                nexts[i] = n;

                if (n != null) blocked = true;

                if ((Mathf.floor((size + 0.1f) / 2f) == i)
                        && n != null
                        && (size % 2 == 1 || n == nexts[i - 1])) {
                    next = n;
                }
            }

            //输入
            sources.clear();
            proximity.each(source -> {
                if (!source.block.hasItems) return;

                //假设建筑向左
                Vec2 vec = Tmp.v1.set(source).sub(this).rotate(-rotation * 90);
                int rotation = OvulamMath.angleToRotation(vec.angle());

                if(rotation == 0)return;

                int sourceSize = source.block.size;
                float difference = (rotation & 1) == 1 ? vec.x : vec.y;

                if(source instanceof CompositeConveyorBuild || rotation == 2){
                    //位于后方的建筑 或者 其他复合带无限制
                    sources.put(source, packSource(difference, sourceSize, rotation));
                } else if (Math.abs((size - 0.5f) * 8 / 2 + vec.x) - sourceSize * 8 / 2f < 4) {
                    //如果源建筑位于该复合带侧边, 只允许最后一格输入物品
                    sources.put(source, packData(size - 1, 1, 0, rotation));
                }
            });
        }

        public int packSource(float difference, int sourceSize, int rotation){
            int lx = Math.max(0, (int) difference + (size - sourceSize) * 8 / 2) / 8;
            int rx = Math.min(size * 8, (int) difference + (size + sourceSize) * 8 / 2) / 8;
            int width = rx - lx;

            return packData(lx, width, 0, rotation);
        }

        @Override
        public void draw() {
            super.draw();
            Draw.reset();

            if (!availablePayload()) {

                for (int i = 0; i < itemStacks.length; i++) {
                    PositionItemStack stack = itemStacks[i];
                    //if (stack.amount == 0) continue;

                    float iconSize = itemSize * Mathf.lerp(0.4f, 1f, Math.min((float) stack.amount / tileCapacity, 1));

                    Draw.rect(stackRegion,
                            stack.cx(this),
                            stack.cy(this), rotation * 90f);

                    Draw.rect(stack.item.fullIcon,
                            stack.cx(this),
                            stack.cy(this),
                            iconSize, iconSize, 0);

                    Lines.line(stack.cx(this),
                            stack.cy(this),x, y);

                    drawPlaceText(i / size + "",
                            World.toTile(stack.cx(this)),
                            World.toTile(stack.cy(this) - size * 4), true);
                }

            }
        }


        @Override
        public void updateTile() {
            if (!enabled) return;
            //Vars.world.build(82, 171).itemStacks

            if (animation > fract()) {
                animation = Mathf.lerp(animation, 0.8f, 0.15f);
            }

            animation = Math.max(animation, fract());

            float fract = animation;

            if (fract < 0.5f) {
                target.trns(itemRotation + 180, (0.5f - fract) * tilesize * 2);
            } else {
                target.trns(rotdeg(), (fract - 0.5f) * tilesize * 2);
            }

            if (item != null) {
                item.update(null, this);
            }

            lastInterp = curInterp;
            curInterp = fract();
            if (lastInterp > curInterp) lastInterp = 0f;
            progress = time() % moveTime;

            if (!availableItem()) {
                updatePayload();
                if (next == null) {
                    PayloadBlock.pushOutput(item, progress / moveTime);
                }
            } else if (!availablePayload()) {
                //todo
                if (animation > 0.95f) {
                    for (int i = 0; i < size; i++) {
                        Building target = nexts[i];
                        if (target == null) continue;

                        PositionItemStack stack = itemStacks[i];
                        if (stack.amount == 0) continue;

                        if(stack.arrive(0.5f)){
                            int amount = Math.max(
                                    target.acceptItem(this, stack.item) ? 1 : 0,
                                    target.acceptStack(stack.item, stack.amount, this));

                            if(amount == 0)continue;

                            target.updateTile();
                            if(amount > 1){
                                target.handleStack(stack.item, amount, this);
                            }else {
                                target.handleItem(this, stack.item);
                            }

                            //todo removeStack
                            items.remove(stack.item, amount);

                            stack.amount -= amount;

                            if (stack.amount == 0) {
                                for (int j = 0; j < maxSize[i]; j++) {
                                    int index = i + j * size;

                                    if (index + size < itemStacks.length) {
                                        PositionItemStack next = itemStacks[index + size];

                                        itemStacks[index].setStack(next.item, next.amount).currentPosition.set(next.currentPosition);
                                        if (next.amount == 0) {
                                            break;
                                        }
                                    } else {
                                        itemStacks[index].amount = 0;
                                    }
                                }
                                maxSize[i]--;
                            }
                        }
                    }
                } else {
                    for (int i = 0; i < itemStacks.length; i++) {
                        setIndexVec(i, itemStacks[i].targetPosition, true);
                    }
                }

                updateItems();
            }

            int curStep = curStep();
            if (curStep > step) {
                boolean valid = step != -1;
                step = curStep;
                boolean had = item != null;

                if (valid && stepAccepted != curStep) {
                    if (!availableItem()) {
                        if (next != null) {
                            next.updateTile();

                            if (next != null && next.acceptPayload(this, item)) {
                                next.handlePayload(this, item);
                                item = null;
                                moved();
                            }
                        } else if (!blocked) {
                            if (item.dump()) {
                                item = null;
                                moved();
                            }
                        }
                    } else if (!availablePayload()) {
                    }
                }

                if (had && item != null) {
                    moveFailed();
                }
            }
        }

        @Override
        public void updatePayload() {
            float rot = Mathf.slerp(itemRotation, rotdeg(), animation);
            item.set(x + target.x, y + target.y, rot);
        }

        public void updateItems() {
            for (int i = 0; i < itemStacks.length; i++) {
                PositionItemStack stack = itemStacks[i];
                if (stack.amount != 0) stack.currentPosition.approach(stack.targetPosition, itemsSpeed * delta());
                //else setIndexVec(i % size, stack.currentPosition, true);
            }
        }

        public Vec2 setIndexVec(int index, Vec2 vec2, boolean follow) {
            int tx = index / size, ty = index % size;

            tmp1.set(tx, ty).sub(radius(), radius()).rotate((rotation + 2) * 90f);
            float dx = tmp1.x, dy = tmp1.y;
            vec2.set(dx * 8, dy * 8);

            if(follow) {
                tmp2.set(target).setLength(target.len() - 4 + 1f);
                float vx = tmp2.x, vy = tmp2.y;
                vec2.add(vx, vy);
            }

            return vec2;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload) {
            return super.acceptPayload(source, payload) && availablePayload();
        }

        @Override
        public boolean acceptItem(Building source, Item item) {
            return buildAccept(source, item) > 0;
        }

        @Override
        public int acceptStack(Item item, int amount, Teamc source) {
            //Vars.world.build(x, y).acceptStack(Items.metaglass, 20, Vars.world.build(x, y))
            int max;
            if(source instanceof Building b){
                max = buildAccept(b, item);
            }else {
                //todo
                max = 0;
            }

            return Math.min(max, amount);
        }

        public int buildAccept(Building source, Item item){
            if(sources.containsKey(source)){
                return acceptPosition(item, sources.get(source));
            }

            return 0;
        }

        public int acceptPosition(Item item, int data){
            int accepted = 0;

            InputData pd = unpackData(data);
            int lx = pd.left;
            int width = pd.width;
            int rotation = pd.rotation;

            pd.free();

            if(rotation == 2) {
                for (int i = 0; i < width; i++) {
                    int index = lx + i;
                    if (maxSize[index] >= size) continue;

                    PositionItemStack lastStack = getFirstEmptyStack(index);

                    setIndexVec(index + (size - 1) * size, Tmp.v1, false);
                    if ((lastStack.amount == 0 || (lastStack.item == item && lastStack.amount < tileCapacity))
                            && !lastStack.currentPosition.within(Tmp.v1, 4)) {
                        accepted += (tileCapacity - lastStack.amount);
                    }
                }
            }else {
                int index = rotation == 1 ? 0 : size - 1;
            }
            return accepted;
        }

        public void buildHandle(Building source, Item item, int amount){
            if(sources.containsKey(source)){
                sources.put(source, handlePosition(item, amount, sources.get(source)));
            }
        }

        public int handlePosition(Item item, int amount, int data){
            int prv = amount;
            InputData pd = unpackData(data);

            int lx = pd.left;
            int width = pd.width;
            int initialAccum = pd.accum;
            int rotation = pd.rotation;

            pd.free();

            int accum = 0;
            //遍历每一列
            for (int i = 0; i < width; i++) {
                int index = lx + (i + initialAccum) % width;

                //最后的非空载荷
                PositionItemStack last = getLastStack(index);
                //阻挡
                float dst;

                if(last != null){
                    int tx = Geometry.d4x(rotation) * 4;
                    //传入物品的该建筑格中心
                    Vec2 tile = setIndexVec(index + (size - 1) * size, Tmp.v1, false);
                    dst = Tmp.v2.set(tile).sub(Geometry.d4x(rotation) * 4, Geometry.d4y(rotation) * 4).dst(last.currentPosition);

                    //如果物品堆靠近输入位置时无法输入
                    if(dst < 4 && last.item != item && last.amount > 0) continue;

                }else {
                    last = fillFirstStack(index, item);
                }

                fillFirstStack(index, item);

                PositionItemStack first = getFirstEmptyStack(index);

                //物品输入的格子的位置
                setIndexVec(index + (size - 1) * size, Tmp.v1, false);

                //如果

                //如果排最后的非空物品堆是无 或者 最后的非空物品堆已满
                //Log.info(index + " " + (index + maxSize[index] * size));
                //直接跳过的情况
                if(last != null && (last.item != item && last.amount > 0)){
                    continue;
                }

                tileOn();

                //需要填充第一个空载荷的情况
                if(first == null){
                    fillFirstStack(index, item);
                }
                if (last == null || last.amount >= tileCapacity) {
                    if(maxSize[index] >= size){
                        continue;
                    }
                    fillFirstStack(index, item);
                } else if (!Tmp.v2.set(Tmp.v1).sub(Geometry.d4x(rotation) * 4, Geometry.d4y(rotation) * 4).within(last.currentPosition, 4)) {
                    Vec2 input = Tmp.v2.set(Tmp.v1).sub(Geometry.d4x(rotation) * 4, Geometry.d4y(rotation) * 4);
                    setIndexVec(index + (size - 1) * size, Tmp.v3, false);

                    if(input.within(last.currentPosition, 4)){
                        continue;
                    }else if(input.within(Tmp.v3, 4)){
                        fillFirstStack(index, item);
                    }

                }


                if (last.item == item && last.amount < tileCapacity) {
                    int min = Math.min(amount, tileCapacity - last.amount);
                    last.amount += min;
                    amount -= min;
                }

            }

            Log.info("add " +( prv - amount));
            items.add(item, prv - amount);

            return packData(lx, width, (initialAccum + accum) % width, rotation);
        }

        public @Nullable PositionItemStack fillFirstStack(int index, Item item){
            PositionItemStack first = getFirstEmptyStack(index);
            if(first == null)return null;

            if (first.amount == 0) {
                first.item = item;
                first.currentPosition.set(index, -0.5f).sub(radius(), radius()).scl(tilesize).rotate(90 * (rotation - 1));
                maxSize[index]++;
                setIndexVec(index + maxSize[index] * size, first.targetPosition, false);
            }

            return first;
        }

        public @Nullable PositionItemStack getFirstEmptyStack(int index){
            return maxSize[index] == size ? null : itemStacks[index + maxSize[index] * size];
        }

        public @Nullable PositionItemStack getLastStack(int index){
            return maxSize[index] == 0 ? null : itemStacks[index + (maxSize[index] - 1) * size];
        }


        @Override
        public void handleItem(Building source, Item item) {
            //Vars.world.build(113,170).handleItem(Vars.world.build(106,160), Items.metaglass)
            buildHandle(source, item, 1);
        }

        @Override
        public void handleStack(Item item, int amount, Teamc source) {
            if(source instanceof Building b){
                buildHandle(b, item, amount);
            }

            stepAccepted = curStep();
        }

        @Override
        public int removeStack(Item item, int amount) {
            return super.removeStack(item, amount);
        }

        public boolean availableItem() {
            return item == null;
        }

        public boolean availablePayload() {
            for (PositionItemStack stack : itemStacks) {
                if (stack.amount > 0) return false;
            }
            return true;
        }

        @Override
        public void onDeconstructed(Unit builder) {
            super.onDeconstructed(builder);
            Pools.freeAll(Seq.with(itemStacks));
        }

        @Override
        public void onDestroyed() {
            super.onDestroyed();
            Pools.freeAll(Seq.with(itemStacks));
        }

        @Override
        public void created() {
            for (int i = 0; i < itemStacks.length; i++) {
                PositionItemStack stack = Pools.obtain(PositionItemStack.class, PositionItemStack::new);

                stack.setStack(Items.copper, 0);

                setIndexVec(i % size, Tmp.v1, false);
                stack.targetPosition.set(Tmp.v1);
                stack.currentPosition.set(Tmp.v1);

                itemStacks[i] = stack;
            }
        }
    }

    public static int packData(int left, int width, int accum, int rotation){
        return left + (width << 4) + (accum << 8) + (rotation << 12);
    }

    public static InputData unpackData(int data){
        InputData pd = Pools.obtain(InputData.class, InputData::new);
        pd.set(data & 15, (data >> 4) & 15, (data >> 8) & 15, data >> 12);
        return pd;
    }

    public static class InputData {
        int left;
        int width;
        int accum;
        int rotation;

        public InputData(){
        }

        public void set(int left, int width, int accum, int rotation) {
            this.left = left;
            this.width = width;
            this.accum = accum;
            this.rotation = rotation;
        }


        public void free(){
            Pools.free(this);
        }
    }
}

     */
