package shagejack.lostgrace.contents.entity.hyperdimensional;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import shagejack.lostgrace.foundation.utility.Vector3;
import shagejack.lostgrace.foundation.utility.Vector4;

import java.util.Optional;

public class MoveControl4D extends MoveControl {

    protected double wantedW;
    protected Entity4D mob4D;
    protected Operation operation4D;

    public MoveControl4D(Mob mob) {
        super(mob);
        this.mob4D = (Entity4D) mob;
    }

    @Override
    public boolean hasWanted() {
        return this.operation4D == Operation.MOVE_TO_3D || this.operation4D == Operation.MOVE_TO_4D;
    }

    @Override
    public void setWantedPosition(double pX, double pY, double pZ, double pSpeed) {
        this.wantedX = pX;
        this.wantedY = pY;
        this.wantedZ = pZ;
        this.wantedW = 0.0D;
        this.speedModifier = pSpeed;
        this.operation4D = Operation.MOVE_TO_3D;
    }

    public void setWantedPosition(double pX, double pY, double pZ, double pW, double pSpeed) {
        if (Double.compare(pW, 0.0D) == 0) {
            setWantedPosition(pX, pY, pZ, pSpeed);
            return;
        }

        this.wantedX = pX;
        this.wantedY = pY;
        this.wantedZ = pZ;
        this.wantedW = pW;
        this.speedModifier = pSpeed;
        this.operation4D = Operation.MOVE_TO_4D;
    }

    @Override
    public void tick() {
        if (this.operation4D == Operation.MOVE_TO_3D) {
            this.operation4D = Operation.WAIT;
            if (canMove()) {
                double speed = this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                double speed3D = getSpeed3D(speed);
                double speedW = getSpeedW(speed);
                double nW = this.mob4D.getW() > 0 ? speedW * 0.05 : speedW;

                Vector4 between = getWantedVector().subtract(getPosVector());
                if (between.length() < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                if (this.mob.getLevel().noCollision(this.mob4D.getBoundingBox(this.mob4D.getW() + nW).move(this.mob.position()))) {
                    this.mob4D.setW(nW);
                }

                float rot = (float)(Mth.atan2(between.z(), between.x()) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), rot, 90.0F));
                this.mob.setSpeed((float) speed3D);

            } else {
                this.mob4D.moveAwayFrom3D(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.05);
            }
        } else if (this.operation4D == Operation.MOVE_TO_4D) {
            this.operation4D = Operation.WAIT;

            Optional<Vec3> intersectionPoint3D = getPointOfIntersection();
            if (intersectionPoint3D.isEmpty() || this.mob.getLevel().noCollision(this.mob4D.getLargestBoundingBox().move(intersectionPoint3D.get()))) {
                // move straightly towards destination
                Vector4 between = getWantedVector().subtract(getPosVector());
                if (between.length() < 2.5E-7D) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float rot = (float)(Mth.atan2(between.z(), between.x()) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), rot, 90.0F));

                double speed = this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                double speed3D = getSpeed3D(speed);
                double speedW = between.w() > 0 ? getSpeedW(speed) : -getSpeedW(speed);
                this.mob.setSpeed((float) speed3D);
                this.mob4D.setW(this.mob4D.getW() + speedW * 0.05);
            } else {
                // finding the nearest empty spot in 3d space and move towards it
                Vector4 target = findEmptySpace();

                double speed = this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED);
                double speed3D = getSpeed3D(speed, target);
                double speedW = getSpeedW(speed, target);
                double nW = this.mob4D.getW() > 0 ? speedW * 0.05 : speedW;

                Vector4 between = target.subtract(getPosVector());
                if (between.length() < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                if (this.mob.getLevel().noCollision(this.mob4D.getBoundingBox(this.mob4D.getW() + nW).move(this.mob.position()))) {
                    this.mob4D.setW(nW);
                }

                float rot = (float)(Mth.atan2(between.z(), between.x()) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), rot, 90.0F));
                this.mob.setSpeed((float) speed3D);
            }
        } else {
            this.mob.setZza(0);
        }

    }

    protected boolean canMove() {
        return canMove(getWantedVector());
    }

    protected boolean canMove(Vector4 vec) {
        Vector4 stepVector = vec.subtract(getPosVector()).normalize().divide(5);
        AABB bounding = this.mob4D.getBoundingBox(this.mob4D.getW() + stepVector.w());
        bounding = bounding.move(this.mob.position());

        if (bounding.equals(Entity4D.EMPTY_BOUNDING))
            return true;

        return !this.mob.getLevel().noCollision(bounding.move(stepVector.get3DPart().toVec3()));
    }

    protected Vector4 findEmptySpace() {
        Vector3 pos = getPosVector().get3DPart();
        Vector3 targetPos = pos;

        double radius = 2;

        while (radius <= 32) {
            for (double theta = 0; theta <= 2 * Math.PI; theta += Math.PI / 9) {
                for (double phi = 0; phi <= 2 * Math.PI; phi += Math.PI / 9) {
                    targetPos = pos.add(radius * Math.cos(phi), radius * Math.cos(theta), radius * Math.sin(theta) * Math.sin(phi));

                    if (this.mob.getLevel().noCollision(this.mob4D.getLargestBoundingBox().move(targetPos.toVec3())))
                        break;
                }
            }
            radius += 2;
        }

        return Vector4.of(targetPos, 0);
    }

    /**
     * get 3d point of intersection of the 4d vector from entity position to destination and the 3d minecraft world space
      */
    protected Optional<Vec3> getPointOfIntersection() {
        final double x1 = this.mob.getX();
        final double y1 = this.mob.getY();
        final double z1 = this.mob.getZ();
        final double w1 = this.mob4D.getW();
        final double x2 = this.wantedX;
        final double y2 = this.wantedY;
        final double z2 = this.wantedZ;
        final double w2 = this.wantedW;

        if (w1 * w2 < 0) {
            double scale = w1 / (w1 - w2);
            return Optional.of(new Vec3(x1, y1, z1).add(new Vec3(x2 - x1, y2 - y1, z2 - z1).scale(scale)));
        }

        return Optional.empty();
    }

    protected double getSpeedW(double speed) {
        return getSpeedW(speed, getWantedVector());
    }

    protected double getSpeed3D(double speed) {
        return getSpeed3D(speed, getWantedVector());
    }

    protected double getSpeedW(double speed, Vector4 target) {
        Vector4 between = target.subtract(getPosVector());

        return speed * between.w() / between.length();
    }

    protected double getSpeed3D(double speed, Vector4 target) {
        Vector4 between = target.subtract(getPosVector());

        return speed * between.get3DPart().length() / between.length();
    }

    protected Vector4 getWantedVector() {
        return new Vector4(this.wantedX, this.wantedY, this.wantedZ, this.wantedW);
    }

    protected Vector4 getPosVector() {
        return new Vector4(this.mob.getX(), this.mob.getY(), this.mob.getZ(), this.mob4D.getW());
    }

    protected enum Operation {
        WAIT,
        MOVE_TO_3D,
        MOVE_TO_4D
    }


}
