package shagejack.lostgrace.foundation.render;

import shagejack.lostgrace.foundation.utility.Vector3;

public record TriangleFace(Vector3 v1, Vector3 v2, Vector3 v3) {
    public Vector3 getV1() {
        return v1;
    }

    public Vector3 getV2() {
        return v2;
    }

    public Vector3 getV3() {
        return v3;
    }

    public TriangleFace scale(double scale) {
        return new TriangleFace(v1.multiply(scale), v2.multiply(scale), v3.multiply(scale));
    }
}
