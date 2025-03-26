package Ovulam.world.block.distribution;

import Ovulam.gen.Stackerc;
import arc.func.Cons;
import arc.func.Cons2;
import arc.func.Cons3;
import arc.math.Mathf;
import arc.math.geom.Geometry;
import arc.math.geom.Vec2;
import arc.struct.Seq;
import arc.util.Log;
import arc.util.Tmp;
import arc.util.pooling.Pools;
import mindustry.Vars;
import mindustry.core.World;
import mindustry.type.Item;

//块堆接收器, 默认朝正上
public interface StackManager<T extends Stackerc> {
    Seq<T>[] stackSeqs();

    T[][] stackMap();

    int size();

    int rotation();

    int tileCapacity();

    float sdelta();

    float wave();
    
    float stackSpeed();

    default int radius() {
        return (size() - 1) * 8 / 2;
    }

    default void eachStack(Cons3<Integer, Integer, T> cons){
        for (int i = 0; i < stackSeqs().length; i++) {
            int j = 0;
            for (T t : stackSeqs()[i]) {
                cons.get(i, j, t);
                j++;
            }
        }
    }

    default void eachTile(Cons3<Integer, Integer, T> cons){
        for (int i = 0; i < size(); i++) {
            for (int j = 0; j < size(); j++) {
                cons.get(i, j, stackMap()[i][j]);
            }
        }
    }

    default int accept(int data, Item item){
        InputData inputData = InputData.unpackData(data);
        int from = inputData.left;
        int width = inputData.width;
        int rotation = inputData.rotation;
        inputData.free();

        return accept(from, width, rotation, item);
    }

    default int accept(int from, int width, int rotSource, Item item){
        int accepted = 0;

        if((rotSource & 1) == 0){//背后输入的情况
            for (int i = 0; i < width; i++) {
                T lastTile = stackMap()[from + i][size() - 1];

                if(lastTile == null){
                    accepted += tileCapacity();
                }else if(item == lastTile.item() && lastTile.y() < -radius() + 4){
                    accepted += tileCapacity() - lastTile.stack().amount;
                }
            }
        }else {//侧面输入的情况
            T[] stacks = stackMap()[(rotSource & 2) == 0 ? 0 : (size() - 1)];

            for (int i = 0; i < width; i++) {
                T tile = stacks[from + i];
                if(!tile.hasItem()){
                    accepted += tile.itemCapacity();
                } else if (item == tile.item()){
                    accepted += tile.maxAccepted(item);
                }

            }
        }

        Log.info(accepted);
        return accepted;
    }

    default int handle(int data, int amount, Item item){
        InputData inputData = InputData.unpackData(data);
        int from = inputData.left;
        int width = inputData.width;
        int rotation = inputData.rotation;
        int accum = inputData.accum;
        inputData.free();

        return handle(from, width, accum, rotation, amount, item);
    }

    default int handle(int from, int width, int accum, int rotSource, int amount, Item item){
        int counter = 0;

        for (int i = 0; i < width; i++) {
            accum++;
            int index = from + (i + accum) % width;

            int x = (rotSource & 1) == 1 ? ((rotSource & 2) == 0 ? 0 : (size() - 1)) : index;
            int y = (rotSource & 1) == 1 ? index : ((rotSource & 2) == 0 ? 0 : (size() - 1));

            T tile = stackMap()[x][y];
            Vec2 inputPos = tilePosition(x, y).add(Geometry.d4x(rotSource) * 4, Geometry.d4y(rotSource) * 4);

            if(tile == null){
                tile = stackMap()[x][y] = obtain();
                tile.stack().item = item;
                tile.set(inputPos);

                if((rotSource & 1) == 1) {
                    int d4y = Geometry.d4y(rotSource);
                    int maxWidth;

                    for (maxWidth = 0; maxWidth < width; maxWidth++) {
                        final int w = x + maxWidth * d4y;
                        if(y < stackSeqs()[w].size){
                            break;
                        }
                    }

                    stackSeqs()[x + maxWidth * d4y].add(tile);
                }else {
                    stackSeqs()[x].add(tile);
                }
            }

            if (tile.item() == item && tile.within(inputPos, 4.2f)) {
                int maxAccept = Math.min(tile.maxAccepted(item), amount);

                tile.addItem(item, maxAccept);
                amount -= maxAccept;

                if (amount <= 0) {
                    if (amount < 0) accum--;
                    break;
                }
            }
        }

        Log.info(InputData.packData(from, width, (accum + counter) % width, rotSource));
        return InputData.packData(from, width, (accum + counter) % width, rotSource);
    }

    default Vec2 tilePosition(int x, int y){
        return Tmp.v1.set(tileToPosition(x), tileToPosition(y));
    }

    default float tileToPosition(float coord){
        return (coord + 0.5f) * 8 - radius();
    }

    default int posToTile(float coord){
        return (int) ((coord + radius() - 4) / 8);
    }

    default void updateStacks(){
        eachStack((seqX, seqY, stack) -> {
            float previousX = stack.x(), previousY = stack.y();
            float targetX = tileToPosition(seqX);
            float targetY = tileToPosition(seqY) + wave();

            //优先运输X轴
            boolean xMoving = !Mathf.equal(targetX, previousX);

            float from = xMoving ? previousX : previousY;
            float to = xMoving ? targetX : targetY;
            float appr = Mathf.approach(from, to, sdelta() * stackSpeed());

            if(xMoving){
                stack.x(appr);

                int prevX = posToTile(from), apprX = posToTile(appr);

                if(prevX != apprX){
                    int tileY = posToTile(previousY);
                    exchange(prevX, tileY, apprX, tileY);
                }
            }else {
                stack.y(appr);

                int tileX = posToTile(previousX);
                if(Mathf.equal(previousY, targetY) && (seqY == 0 || stackMap()[tileX][seqY - 1] == null)){
                    exchange(tileX, seqY, tileX, seqY - 1);
                }
            }
        });
    }

    default void exchange(int fromX, int fromY, int toX, int toY){
        stackMap()[toX][toY] = stackMap()[fromX][fromY];
        stackMap()[fromX][fromY] = null;
    }

    T obtain();

    default void free(T stack){
        Pools.free(stack);
    }

    //以接收者的中心为零点
    default Vec2 stackTarget(int x, int y, Vec2 vec2) {
        return vec2.set(x, y).sub(radius(), radius());
    }

    class InputData {
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

        public static int packData(int left, int width, int accum, int rotation){
            return left + (width << 4) + (accum << 8) + (rotation << 12);
        }

        public static InputData unpackData(int data){
            InputData pd = Pools.obtain(InputData.class, InputData::new);
            pd.set(data & 15, (data >> 4) & 15, (data >> 8) & 15, data >> 12);
            return pd;
        }
    }

    class Positionc<T extends Stackerc>{
        int x, y;
        T t;

    }

}
