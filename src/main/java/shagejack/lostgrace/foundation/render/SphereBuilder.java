package shagejack.lostgrace.foundation.render;

import shagejack.lostgrace.foundation.utility.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SphereBuilder {

    // TODO: render sphere

    public List<TriangleFace> build(Vector3 axis, int fractionsSplit, int fractionsCircle) {
        List<TriangleFace> sphereFaces = new ArrayList<>();


        return sphereFaces;
    }

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
    }
}
