package shagejack.lostgrace.foundation.utility;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.Iterator;
import java.util.Random;
import java.util.function.IntFunction;

public record Vector3(double x, double y, double z) {

    private static final Random RAND = new Random();
    public static final Vector3 ZERO = new Vector3(0, 0, 0);
    public static final Vector3 X_AXIS = new Vector3(1, 0, 0);
    public static final Vector3 Y_AXIS = new Vector3(0, 1, 0);
    public static final Vector3 Z_AXIS = new Vector3(0, 0, 1);

    public static Vector3 of(double x, double y, double z) {
        return new Vector3(x, y, z);
    }

    public static Vector3 of(Vec3i pos) {
        return new Vector3(pos.getX(), pos.getY(), pos.getZ());
    }

    public static Vector3 of(Vec3 pos) {
        return new Vector3(pos.x(), pos.y(), pos.z());
    }

    public static Vector3 of(Vector3f pos) {
        return new Vector3(pos.x(), pos.y(), pos.z());
    }

    public static Vector3 of(Entity entity) {
        return Vector3.of(entity.position());
    }

    public static Vector3 of(CompoundTag tag) {
        return new Vector3(tag.getDouble("X"), tag.getDouble("Y"), tag.getDouble("Z"));
    }

    public static Vector3 random() {
        return new Vector3(RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1), RAND.nextDouble() * (RAND.nextBoolean() ? 1 : -1));
    }

    public static Vector3 random(Random random) {
        return new Vector3(random.nextDouble() * (random.nextBoolean() ? 1 : -1), random.nextDouble() * (random.nextBoolean() ? 1 : -1), random.nextDouble() * (random.nextBoolean() ? 1 : -1));
    }

    public Vector3 copy() {
        return new Vector3(x, y, z);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        } else if (!(other instanceof Vector3 vec3)) {
            return false;
        } else {
            return Double.compare(this.getX(), vec3.getX()) == 0 && Double.compare(this.getY(), vec3.getY()) == 0 && Double.compare(this.getZ(), vec3.getZ()) == 0;
        }
    }

    @Override
    public int hashCode() {
        long j = Double.doubleToLongBits(this.x);
        int i = (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.y);
        i = 31 * i + (int) (j ^ j >>> 32);
        j = Double.doubleToLongBits(this.z);
        return 31 * i + (int) (j ^ j >>> 32);
    }

    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getZ() {
        return this.z;
    }

    public Vector3 setX(double nX) {
        return new Vector3(nX, this.y, this.z);
    }

    public Vector3 setY(double nY) {
        return new Vector3(this.x, nY, this.z);
    }

    public Vector3 setZ(double nZ) {
        return new Vector3(this.x, this.y, nZ);
    }

    public Vector3 addX(double dX) {
        return new Vector3(this.x + dX, this.y, this.z);
    }

    public Vector3 addY(double dY) {
        return new Vector3(this.x, this.y + dY, this.z);
    }

    public Vector3 addZ(double dZ) {
        return new Vector3(this.x, this.y, this.z + dZ);
    }

    public Vector3 add(Vector3 vec) {
        return new Vector3(x + vec.x, y + vec.y, z + vec.z);
    }

    public Vector3 add(double dX, double dY, double dZ) {
        return new Vector3(x + dX, y + dY, z + dZ);
    }

    public Vector3 subtract(Vector3 vec) {
        return new Vector3(x - vec.x, y - vec.y, z - vec.z);
    }

    public Vector3 multiply(double mul) {
        return new Vector3(x * mul, y * mul, z * mul);
    }

    public Vector3 divide(double mul) {
        return new Vector3(x / mul, y / mul, z / mul);
    }

    public double dot(Vector3 vec) {
        return x * vec.x + y * vec.y + z * vec.z;
    }

    public Vector3 cross(Vector3 vec) {
        return new Vector3(y * vec.z - z * vec.y, z * vec.x - x * vec.z, x * vec.y - y * vec.x);
    }

    public Vector3 opposite() {
        return this.multiply(-1);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public double distance(Vector3 vec) {
        return vec.subtract(this).length();
    }

    public Vector3 normalize() {
        if (!this.equals(ZERO))
            return this.divide(this.length());

        return Y_AXIS;
    }

    public double includedAngle(Vector3 vec) {
        return Math.acos(this.dot(vec) / (this.length() * vec.length()));
    }

    public double includedAngleDegree(Vector3 vec) {
        return this.includedAngle(vec) * 180 / Math.PI;
    }

    public static Iterator<Vector3> iterator(Vector3 from, Vector3 to, int divider) {
        return new VecIterator(from, to, divider);
    }

    public Iterator<Vector3> iterator(Vector3 to, int divider) {
        return iterator(this, to, divider);
    }

    public Iterator<Vector3> iterator(int divider) {
        return iterator(this, divider);
    }

    public static Iterator<Vector3> formulaIterator(Vector3 start, IntFunction<Vector3> addVectorFun, int times) {
        return new VecFormulaIterator(start, addVectorFun, times);
    }

    public Iterator<Vector3> formulaIterator(IntFunction<Vector3> addVectorFun, int times) {
        return formulaIterator(this, addVectorFun, times);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("X", x);
        tag.putDouble("Y", y);
        tag.putDouble("Z", z);
        return tag;
    }

    public VertexConsumer drawPosNormal(Matrix3f normal, VertexConsumer buf) {
        return buf.normal(normal, (float) this.x, (float) this.y, (float) this.z);
    }

    public VertexConsumer drawPosVertex(Matrix4f renderMatrix, VertexConsumer buf) {
        return buf.vertex(renderMatrix, (float) this.x, (float) this.y, (float) this.z);
    }

    public Vector3f toVec3f() {
        return new Vector3f((float) x, (float) y, (float) z);
    }

    public Quaternion asRotateAxis(double angle) {
        return new Quaternion(this.normalize().toVec3f(), (float) angle, false);
    }

    public Quaternion asRotateAxisDegree(double angle) {
        return new Quaternion(this.normalize().toVec3f(), (float) angle, true);
    }

    public Quaternion asToVecRotation(Vector3 vec) {
        return new Quaternion(this.cross(vec).normalize().toVec3f(), (float) this.includedAngle(vec), false);
    }

    public Vector3 rotate(double angle, Vector3 axis) {
        if (!axis.equals(ZERO) && Double.compare(angle, 0) != 0)
            return this.transform(new Quaternion(axis.normalize().toVec3f(), (float) angle, false));

        return this;
    }

    public Vector3 rotateDegree(double angle, Vector3 axis) {
        if (!axis.equals(ZERO) && Double.compare(angle, 0) != 0)
            return this.transform(new Quaternion(axis.normalize().toVec3f(), (float) angle, true));

        return this;
    }

    public Vector3 transform(Matrix3f pMatrix) {
        Vector3f vec = this.toVec3f();
        vec.transform(pMatrix);
        return Vector3.of(vec);
    }

    public Vector3 transform(Quaternion pQuaternion) {
        Quaternion quaternion = new Quaternion(pQuaternion);
        quaternion.mul(new Quaternion((float) this.x(), (float) this.y(), (float) this.z(), 0.0F));
        Quaternion quaternion1 = new Quaternion(pQuaternion);
        quaternion1.conj();
        quaternion.mul(quaternion1);
        return new Vector3(quaternion.i(), quaternion.j(), quaternion.k());
    }

    public static class VecIterator implements Iterator<Vector3> {

        Vector3 from;
        Vector3 to;

        int size;
        int cursor = 0;

        public VecIterator(Vector3 from, Vector3 to, int size) {
            this.from = from;
            this.to = to;
            this.size = size;
        }

        public VecIterator(Vector3 vec, int size) {
            this.from = ZERO;
            this.to = vec;
            this.size = size;
        }

        @Override
        public boolean hasNext() {
            return cursor != size;
        }

        @Override
        public Vector3 next() {
            Vector3 vec = from.add(to.subtract(from).multiply((double) this.cursor / size));
            this.cursor++;
            return vec;
        }
    }

    public static class VecFormulaIterator implements Iterator<Vector3> {

        Vector3 currentPoint;
        IntFunction<Vector3> addVec;

        int times;
        int cursor = 0;

        public VecFormulaIterator(Vector3 startPoint, IntFunction<Vector3> addVec, int times) {
            this.currentPoint = startPoint;
            this.addVec = addVec;
            this.times = times;
        }

        @Override
        public boolean hasNext() {
            return cursor != times;
        }

        @Override
        public Vector3 next() {
            if (cursor == 0) {
                this.cursor++;
                return currentPoint;
            }

            Vector3 vec = currentPoint.add(addVec.apply(cursor));
            this.cursor++;
            return vec;
        }
    }

}
