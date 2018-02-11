package me.andreaiacono.racinglearning.misc;

public class Vector3D {

    private final double x;
    private final double y;
    private final double z;

    public Vector3D(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3D rotateZ(double angle) {
        double nx = x * Math.cos(angle) - y * Math.sin(angle);
        double ny = x * Math.sin(angle) + y * Math.cos(angle);
        double nz = z;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D rotateY(double angle) {
        double nz = z * Math.cos(angle) - x * Math.sin(angle);
        double nx = z * Math.sin(angle) + x * Math.cos(angle);
        double ny = y;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D rotateX(double angle) {
        double ny = y * Math.cos(angle) - z * Math.sin(angle);
        double nz = y * Math.sin(angle) + z * Math.cos(angle);
        double nx = x;
        return new Vector3D(nx, ny, nz);
    }

    public Vector3D translate(double x, double y, double z) {
        return new Vector3D(this.x + x, this.y + y, this.z + z);
    }

    public double getSize() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3D normalize() {
        double size = getSize();
        return new Vector3D(x / size, y / size, z / size);
    }

    public Vector3D scale(double f) {
        return new Vector3D(x * f, y * f, z * f);
    }

    public Vector3D add(Vector3D v) {
        return new Vector3D(x + v.x, y + v.y, z + v.z);
    }

    public Vector3D subtract(Vector3D v) {
        return new Vector3D(x - v.x, y - v.y, z - v.z);
    }

    public double multiply(Vector3D v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public static double angleBetween(Vector3D a, Vector3D b) {
        double am = a.getSize();
        double bm = b.getSize();
        return Math.acos(a.multiply(b) / (am * bm));
    }

    public static Vector3D subtract(Vector3D a, Vector3D b) {
        return new Vector3D(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3D cross(Vector3D res, Vector3D left, Vector3D right) {
        double x = left.y * right.z - left.z * right.y;
        double y = right.x * left.z - right.z * left.x;
        double z = left.x * right.y - left.y * right.x;
        return new Vector3D(x, y, z);
    }

    public double getRelativeAngleBetween(Vector3D v) {
        return getSign(v) * Math.acos(multiply(v) / (getSize() * v.getSize()));
    }

    // http://www.oocities.org/pcgpe/math2d.html
    // http://gamedev.stackexchange.com/questions/45412/understanding-math-used-to-determine-if-vector-is-clockwise-counterclockwise-f
    public int getSign(Vector3D v) {
        return (y * v.x > x * v.y) ? -1 : 1;
    }


}
