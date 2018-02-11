//package me.andreaiacono.racinglearning.misc;
//
//public class Vector2D {
//
//    private final double x;
//    private final double y;
//
//    public Vector2D(double x, double y) {
//        this.x = x;
//        this.y = y;
//    }
//
//    public Vector2D rotateY(double angle) {
//        double nx = x * Math.cos(angle) - y * Math.sin(angle);
//        double ny = x * Math.sin(angle) + y * Math.cos(angle);
//        return new Vector2D(nx, ny, nz);
//    }
//
//    public Vector2D rotateY(double angle) {
//        double nz = z * Math.cos(angle) - x * Math.sin(angle);
//        double nx = z * Math.sin(angle) + x * Math.cos(angle);
//        double ny = y;
//        return new Vector2D(nx, ny, nz);
//    }
//
//    public Vector2D rotateX(double angle) {
//        double ny = y * Math.cos(angle) - z * Math.sin(angle);
//        double nz = y * Math.sin(angle) + z * Math.cos(angle);
//        double nx = x;
//        return new Vector2D(nx, ny, nz);
//    }
//
//    public Vector2D translate(double x, double y, double z) {
//        return new Vector2D(this.x + x, this.y + y, this.z + z);
//    }
//
//    public double getSize() {
//        return Math.sqrt(x * x + y * y + z * z);
//    }
//
//    public Vector2D normalize() {
//        double size = getSize();
//        return new Vector2D(x / size, y / size, z / size);
//    }
//
//    public Vector2D scale(double f) {
//        return new Vector2D(x * f, y * f, z * f);
//    }
//
//    public Vector2D add(Vector2D v) {
//        return new Vector2D(x + v.x, y + v.y, z + v.z);
//    }
//
//    public Vector2D subtract(Vector2D v) {
//        return new Vector2D(x - v.x, y - v.y, z - v.z);
//    }
//
//    public double multiply(Vector2D v) {
//        return x * v.x + y * v.y + z * v.z;
//    }
//
//    public static double angleBetween(Vector2D a, Vector2D b) {
//        double am = a.getSize();
//        double bm = b.getSize();
//        return Math.acos(a.multiply(b) / (am * bm));
//    }
//
//    public static Vector2D subtract(Vector2D a, Vector2D b) {
//        return new Vector2D(a.x - b.x, a.y - b.y, a.z - b.z);
//    }
//
//    public static Vector2D cross(Vector2D res, Vector2D left, Vector2D right) {
//        double x = left.y * right.z - left.z * right.y;
//        double y = right.x * left.z - right.z * left.x;
//        double z = left.x * right.y - left.y * right.x;
//        return new Vector2D(x, y, z);
//    }
//
//    public double getRelativeAngleBetween(Vector2D v) {
//        return getSign(v) * Math.acos(multiply(v) / (getSize() * v.getSize()));
//    }
//
//    // http://www.oocities.org/pcgpe/math2d.html
//    // http://gamedev.stackexchange.com/questions/45412/understanding-math-used-to-determine-if-vector-is-clockwise-counterclockwise-f
//    public int getSign(Vector2D v) {
//        return (y * v.x > x * v.y) ? -1 : 1;
//    }
//
//
//}
