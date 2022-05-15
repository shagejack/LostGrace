package shagejack.lostgrace.foundation.render;

import shagejack.lostgrace.foundation.utility.Vector3;

import java.util.ArrayList;
import java.util.List;

public class SphereBuilder {

    public List<TriangleFace> build(double radius, int stacks) {
        return build(Vector3.Y_AXIS.multiply(radius),  stacks, true);
    }

    public List<TriangleFace> build(double radius, int stacks, boolean clockwise) {
        return build(Vector3.Y_AXIS.multiply(radius), stacks, clockwise);
    }

    public List<TriangleFace> build(Vector3 axis, int stacks) {
        return build(axis, 2 * stacks, stacks, true);
    }

    public List<TriangleFace> build(Vector3 axis, int stacks, boolean clockwise) {
        return build(axis, 2 * stacks, stacks, clockwise);
    }

    public List<TriangleFace> build(Vector3 axis, int sectors, int stacks, boolean clockwise) {
        List<TriangleFace> sphereFaces = new ArrayList<>();
        List<Vector3> vertices = new ArrayList<>();

        double r = axis.length();
        Vector3 rotation = axis.subtract(Vector3.Y_AXIS.multiply(r));

        // generate vertices
        for (int j = 0; j <= stacks; j++) {
            double v = (double) j / stacks;
            double phi = Math.PI * v;

            for (int i = 0; i <= sectors; i++) {
                double u = (double) i / sectors;
                double theta = 2 * Math.PI * u;
                vertices.add(new Vector3(r * Math.sin(phi) * Math.cos(theta), r * Math.cos(phi), r * Math.cos(phi) * Math.sin(theta)).add(rotation));
            }
        }

        // generate triangles
        for (int j = 0; j < stacks; j++) {
            int k1 = j * (sectors + 1);
            int k2 = k1 + sectors + 1;

            for (int i = 0; i < sectors; i++, k1++, k2++) {
                if (j != 0) {
                    sphereFaces.add(clockwise ?
                            new TriangleFace(vertices.get(k1), vertices.get(k1 + 1), vertices.get(k2)) :
                            new TriangleFace(vertices.get(k1), vertices.get(k2), vertices.get(k1 + 1))
                    );
                }

                // generate two triangles when it's not polar sectors
                if (j != stacks - 1) {
                    sphereFaces.add(clockwise ?
                            new TriangleFace(vertices.get(k2), vertices.get(k1 + 1), vertices.get(k2 + 1)) :
                            new TriangleFace(vertices.get(k2), vertices.get(k2 + 1), vertices.get(k1 + 1))
                    );
                }
            }
        }



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
