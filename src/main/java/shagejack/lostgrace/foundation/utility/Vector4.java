package shagejack.lostgrace.foundation.utility;

public record Vector4(double x, double y, double z, double w) {

    public static Vector4 of(Vector3 vec, double w) {
        return new Vector4(vec.x(), vec.y(), vec.z(), w);
    }

    public Vector3 get3DPart() {
        return new Vector3(x, y, z);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z + w * w);
    }

    public Vector4 add(Vector4 add) {
        return new Vector4(x + add.x, y + add.y, z + add.z, w + add.w);
    }

    public Vector4 subtract(Vector4 sub) {
        return new Vector4(x - sub.x, y - sub.y, z - sub.z, w - sub.w);
    }

    public Vector4 multiply(double mul) {
        return new Vector4(x * mul, y * mul, z * mul, w * mul);
    }

    public Vector4 divide(double div) {
        return this.multiply(1 / div);
    }

    public Vector4 opposite() {
        return this.multiply(-1);
    }

    public Vector4 normalize() {
        return this.divide(this.length());
    }
}
