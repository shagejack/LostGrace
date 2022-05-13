package shagejack.lostgrace.foundation.render;

import shagejack.lostgrace.foundation.utility.Vector3;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SphereBuilder {

    public List<TriangleFace> build(Vector3 axis, int fractionsSplit, int fractionsCircle) {
        List<TriangleFace> sphereFaces = new ArrayList<>();
        Vector3 centerPerp = axis.perpendicular();
        double degSplit =       180D / ((double) fractionsSplit);
        double degCircleSplit = 360D / ((double) fractionsCircle);
        double degCircleOffsetShifted = degCircleSplit / 2D;
        boolean shift = false;

        Vector3[] prevArray = new Vector3[fractionsCircle];
        Vector3 prev = axis.copy();
        Arrays.fill(prevArray, prev);
        for (int i = 1; i <= fractionsSplit; i++) {
            Vector3 splitVec = axis.rotate(Math.toRadians(degSplit * i), centerPerp);

            Vector3[] circlePositions = new Vector3[fractionsCircle];
            for (int j = 0; j < fractionsCircle; j++) {
                double deg = shift ? degCircleOffsetShifted : 0;
                deg += degCircleSplit * j;
                circlePositions[j] = splitVec.rotate(Math.toRadians(deg), axis);
            }

            for (int k = 0; k < fractionsCircle; k++) {
                int prevIndex = shift ? k : k - 1;
                if (prevIndex < 0) {
                    prevIndex = fractionsCircle - 1;
                }
                int nextIndex = shift ? k + 1 : k;
                if (nextIndex >= fractionsCircle) {
                    nextIndex = 0;
                }
                sphereFaces.add(new TriangleFace(prevArray[prevIndex], prevArray[nextIndex], circlePositions[k]));
                int nextCircle = k + 1;
                if (nextCircle >= fractionsCircle) {
                    nextCircle = 0;
                }
                sphereFaces.add(new TriangleFace(circlePositions[k], prevArray[nextIndex], circlePositions[nextCircle]));
            }

            prevArray = circlePositions;
            shift = !shift;
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
