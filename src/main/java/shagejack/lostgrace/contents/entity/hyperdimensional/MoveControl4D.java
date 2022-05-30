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
        return this.operation4D == Operation.MOVE_TO || this.operation4D == Operation.MOVE_TO_4D;
    }

    @Override
    public void setWantedPosition(double pX, double pY, double pZ, double pSpeed) {
        this.wantedX = pX;
        this.wantedY = pY;
        this.wantedZ = pZ;
        this.speedModifier = pSpeed;
        this.operation4D = Operation.MOVE_TO;
    }

    public void setWantedPosition(double pX, double pY, double pZ, double pW, double pSpeed) {
        this.wantedX = pX;
        this.wantedY = pY;
        this.wantedZ = pZ;
        this.wantedW = pW;
        this.speedModifier = pSpeed;
        this.operation4D = Operation.MOVE_TO_4D;
    }

    @Override
    public void tick() {
        if (this.operation4D == Operation.MOVE_TO) {
            this.operation4D = Operation.WAIT;
            if (hasStraightLinePathIn3D()) {
                double d0 = this.wantedX - this.mob.getX();
                double d1 = this.wantedZ - this.mob.getZ();
                double d2 = this.wantedY - this.mob.getY();
                double d3 = d0 * d0 + d2 * d2 + d1 * d1;
                if (d3 < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float f9 = (float)(Mth.atan2(d1, d0) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), f9, 90.0F));
                this.mob.setSpeed((float)(this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED)));
            } else {
                this.mob4D.setW(this.mob4D.getW() + this.speedModifier * this.mob.getAttributeValue(Attributes.MOVEMENT_SPEED) * 0.05);
            }
        } else if (this.operation4D == Operation.MOVE_TO_4D) {
            this.operation4D = Operation.WAIT;
            Optional<Vec3> intersectionPoint3D = getPointOfIntersection();
            if (intersectionPoint3D.isEmpty() || this.mob.getLevel().noCollision(this.mob4D.getLargestBoundingBox().move(intersectionPoint3D.get()))) {
                // move straightly towards destination
                Vector4 between = getWantedVector().subtract(getPosVector());
                if (between.length() < (double)2.5000003E-7F) {
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
                if (this.mob.getLevel().noCollision(this.mob4D.getBoundingBox(this.mob4D.getW() + nW).move(this.mob.position()))) {
                    this.mob4D.setW(nW);
                }

                Vector4 between = target.subtract(getPosVector());
                if (between.length() < (double)2.5000003E-7F) {
                    this.mob.setZza(0.0F);
                    return;
                }

                float rot = (float)(Mth.atan2(between.z(), between.x()) * (double)(180F / (float)Math.PI)) - 90.0F;
                this.mob.setYRot(this.rotlerp(this.mob.getYRot(), rot, 90.0F));
                this.mob.setSpeed((float) speed3D);
            }
        } else {
            this.mob.setZza(0);
        }

    }

    protected boolean hasStraightLinePathIn3D() {
        AABB bounding = this.mob4D.getLargestBoundingBox();

        if (bounding.equals(Entity4D.EMPTY_BOUNDING))
            return true;

        bounding = bounding.move(this.mob.position());

        Vec3 betweenVector = new Vec3(this.wantedX - this.mob.getX(), this.wantedY - this.mob.getY(), this.wantedY - this.mob.getY());
        double length = betweenVector.length();
        Vec3 normalized = betweenVector.normalize();

        for (int i = 0; i < length; i++) {
            bounding = bounding.move(normalized);
            if (!this.mob.getLevel().noCollision(bounding)) {
                return false;
            }
        }

        return true;
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
            return Optional.of(new Vec3(x2 - x1, y2 - y1, z2 - z1).scale(scale));
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
        MOVE_TO,
        MOVE_TO_4D
    }


}
