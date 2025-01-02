package Ovulam.world.block.storage;

import arc.graphics.g2d.Draw;
import arc.math.Angles;
import arc.math.geom.Vec2;
import arc.util.Nullable;
import arc.util.Tmp;
import mindustry.gen.Building;
import mindustry.gen.Unit;
import mindustry.graphics.Layer;
import mindustry.world.blocks.payloads.Payload;

import static mindustry.Vars.tilesize;
import static mindustry.world.blocks.payloads.PayloadBlock.pushOutput;

public class PayloadStorageBlock extends BaseStorageBlock{
    public float maxPayloadSize = 9;
    public float payloadSpeed = 0.7f, payloadRotateSpeed = 5f;
    public PayloadStorageBlock(String name) {
        super(name);
        acceptsPayload = true;
    }

    public class PayloadStorageBuild extends BaseStorageBuild {
        public @Nullable Payload payload;
        public float payRotation;
        public Vec2 payVector = new Vec2();
        public boolean carried;


        public boolean acceptUnitPayload(Unit unit){
            return false;
        }

        @Override
        public boolean canControlSelect(Unit unit){
            return !unit.spawnedByCore && unit.type.allowedInPayloads && this.payload == null
                    && acceptUnitPayload(unit) && unit.tileOn() != null && unit.tileOn().build == this;
        }

        @Override
        public void onControlSelect(Unit player){
            float x = player.x, y = player.y;
            handleUnitPayload(player, p -> payload = p);
            this.payVector.set(x, y).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            this.payRotation = player.rotation;
        }

        @Override
        public boolean acceptPayload(Building source, Payload payload){
            return this.payload == null;
        }

        @Override
        public void handlePayload(Building source, Payload payload){
            this.payload = payload;
            this.payVector.set(source).sub(this).clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);
            this.payRotation = payload.rotation();

            updatePayload();
        }

        @Override
        public Payload getPayload(){
            return payload;
        }

        @Override
        public Payload takePayload(){
            return payload;
        }

        @Override
        public void onRemoved(){
            super.onRemoved();
            if(payload != null && !carried) payload.dump();
        }

        @Override
        public void updateTile(){
            super.updateTile();
            if(payload != null){
                payload.update(null, this);
            }
        }

        public void updatePayload(){
            if(payload != null){
                payload.set(x + payVector.x, y + payVector.y, payRotation);
            }
        }

        /** @return true if the payload is indexs position. */
        public boolean moveInPayload(){
            return moveInPayload(true);
        }

        /** @return true if the payload is indexs position. */
        public boolean moveInPayload(boolean rotate){
            if(payload == null) return false;

            updatePayload();

            if(rotate){
                payRotation = Angles.moveToward(payRotation, block.rotate ? rotdeg() : 90f, payloadRotateSpeed * delta());
            }
            payVector.approach(Vec2.ZERO, payloadSpeed * delta());

            return hasArrived();
        }

        public void moveOutPayload(){
            if(payload == null) return;

            updatePayload();

            Vec2 dest = Tmp.v1.trns(rotdeg(), size * tilesize/2f);

            payRotation = Angles.moveToward(payRotation, rotdeg(), payloadRotateSpeed * delta());
            payVector.approach(dest, payloadSpeed * delta());

            Building front = front();
            boolean canDump = front == null || !front.tile().solid();
            boolean canMove = front != null && (front.block.outputsPayload || front.block.acceptsPayload);

            if(canDump && !canMove){
                pushOutput(payload, 1f - (payVector.dst(dest) / (size * tilesize / 2f)));
            }

            if(payVector.within(dest, 0.001f)){
                payVector.clamp(-size * tilesize / 2f, -size * tilesize / 2f, size * tilesize / 2f, size * tilesize / 2f);

                if(canMove){
                    if(movePayload(payload)){
                        payload = null;
                    }
                }else if(canDump){
                    dumpPayload();
                }
            }
        }

        public void dumpPayload(){
            //translate payload forward slightly
            float tx = Angles.trnsx(payload.rotation(), 0.1f), ty = Angles.trnsy(payload.rotation(), 0.1f);
            payload.set(payload.x() + tx, payload.y() + ty, payload.rotation());

            if(payload.dump()){
                payload = null;
            }else{
                payload.set(payload.x() - tx, payload.y() - ty, payload.rotation());
            }
        }

        public boolean hasArrived(){
            return payVector.isZero(0.01f);
        }

        public void drawPayload(){
            if(payload != null){
                updatePayload();

                Draw.z(Layer.blockOver);
                payload.draw();
            }
        }
    }
}
