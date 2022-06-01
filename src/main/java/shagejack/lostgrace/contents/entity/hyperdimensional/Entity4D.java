package shagejack.lostgrace.contents.entity.hyperdimensional;

import net.minecraft.world.phys.AABB;

public interface Entity4D {

    AABB EMPTY_BOUNDING = new AABB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);

    default AABB getLargestBoundingBox() {
        return getBoundingBox(0.0D);
    }

    AABB getBoundingBox(double w);

    double getW();

    void setW(double w);

    default void addW(double w) {
        this.setW(this.getW() + w);
    }

    default void moveTowards3D(double dW) {
        if (Math.abs(getW()) > 0.0000025D) {
            setW(getW() > 0 ? Math.max(getW() - dW, 0) : Math.min(getW() + dW, 0));
        }
    }

    default void moveAwayFrom3D(double dW) {
        setW(getW() > 0 ? getW() + dW : getW() - dW);
    }

}
