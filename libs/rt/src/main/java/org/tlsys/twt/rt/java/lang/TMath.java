package org.tlsys.twt.rt.java.lang;

import org.tlsys.twt.CastUtil;
import org.tlsys.twt.Script;
import org.tlsys.twt.annotations.ClassName;
import org.tlsys.twt.annotations.JSClass;
import org.tlsys.twt.annotations.ReplaceClass;

@JSClass
@ReplaceClass(Math.class)
public final class TMath {

    public static final double E = 2.7182818284590452354;
    public static final double PI = 3.14159265358979323846;

    private TMath() {
    }

    public static double sin(double a) {
        return CastUtil.toDouble(Script.code("Math.sin(",a,")"));
    }

    public static double cos(double a) {
        return CastUtil.toDouble(Script.code("Math.cos(",a,")"));
    }

    public static double tan(double a) {
        return CastUtil.toDouble(Script.code("Math.tan(",a,")"));
    }

    public static double asin(double a) {
        return CastUtil.toDouble(Script.code("Math.asin(",a,")"));
    }

    public static double acos(double a) {
        return CastUtil.toDouble(Script.code("Math.acos(", CastUtil.toObject(a),")"));
    }

    public static double atan(double a) {
        return CastUtil.toDouble(Script.code("Math.atan(",CastUtil.toObject(a),")"));
    }

    public static double toRadians(double angdeg) {
        return angdeg / 180.0 * PI;
    }

    public static double toDegrees(double angrad) {
        return angrad * 180.0 / PI;
    }

    public static double exp(double a) {
        return CastUtil.toDouble(Script.code("Math.exp(",CastUtil.toObject(a),")"));
    }

    public static double log(double a) {
        return CastUtil.toDouble(Script.code("Math.log(",CastUtil.toObject(a),")"));
    }

    public static double log10(double a) {
        return CastUtil.toDouble(Script.code("Math.log10(",CastUtil.toObject(a),")"));
    }

    public static double sqrt(double a) {
        return CastUtil.toDouble(Script.code("Math.sqrt(",CastUtil.toObject(a),")"));
    }

    public static double cbrt(double a) {
        return CastUtil.toDouble(Script.code("Math.cbrt(",CastUtil.toObject(a),")"));
    }

    public static double ceil(double a) {
        return CastUtil.toDouble(Script.code("Math.ceil(",CastUtil.toObject(a),")"));
    }

    public static double floor(double a) {
        return CastUtil.toDouble(Script.code("Math.floor(",CastUtil.toObject(a),")"));
    }

    public static double atan2(double y, double x) {
        return CastUtil.toDouble(Script.code("Math.atan2(",CastUtil.toObject(y), ",", CastUtil.toObject(x),")"));
    }

    public static double pow(double a, double b) {
        return CastUtil.toDouble(Script.code("Math.pow(",CastUtil.toObject(a), ",", CastUtil.toObject(b),")"));
    }

    public static int round(float a) {
        return CastUtil.toInt(Script.code("Math.round(",a,")"));
    }

    public static long round(double a) {
        return CastUtil.toLong(Script.code("Math.round(",CastUtil.toObject(a),")"));
    }

    public static double random() {
        return CastUtil.toDouble(Script.code("Math.random()"));
    }

    public static int addExact(int x, int y) {
        int r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    public static long addExact(long x, long y) {
        long r = x + y;
        // HD 2-12 Overflow iff both arguments have the opposite sign of the result
        if (((x ^ r) & (y ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    public static int subtractExact(int x, int y) {
        int r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("integer overflow");
        }
        return r;
    }

    public static long subtractExact(long x, long y) {
        long r = x - y;
        // HD 2-12 Overflow iff the arguments have different signs and
        // the sign of the result is different than the sign of x
        if (((x ^ y) & (x ^ r)) < 0) {
            throw new ArithmeticException("long overflow");
        }
        return r;
    }

    public static int multiplyExact(int x, int y) {
        long r = (long)x * (long)y;
        if ((int)r != r) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)r;
    }

    public static long multiplyExact(long x, long y) {
        long r = x * y;

        long ax = Math.abs(x);
        long ay = Math.abs(y);
        if (((ax | ay) >>> 31 != 0)) {

            // Some bits greater than 2^31 that might cause overflow
            // Check the result using the divide operator
            // and check for the special case of Long.MIN_VALUE * -1
            if (((y != 0) && (r / y != x)) ||
                    (x == Long.MIN_VALUE && y == -1)) {
                throw new ArithmeticException("long overflow");
            }

        }

        return r;
    }

    public static int incrementExact(int a) {
        if (a == Integer.MAX_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a + 1;
    }

    public static long incrementExact(long a) {
        if (a == Long.MAX_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a + 1L;
    }

    public static int decrementExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return a - 1;
    }

    public static long decrementExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return a - 1L;
    }

    public static int negateExact(int a) {
        if (a == Integer.MIN_VALUE) {
            throw new ArithmeticException("integer overflow");
        }

        return -a;
    }

    public static long negateExact(long a) {
        if (a == Long.MIN_VALUE) {
            throw new ArithmeticException("long overflow");
        }

        return -a;
    }

    public static int toIntExact(long value) {
        if ((int)value != value) {
            throw new ArithmeticException("integer overflow");
        }
        return (int)value;
    }

    public static int floorDiv(int x, int y) {
        int r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    public static long floorDiv(long x, long y) {
        long r = x / y;
        // if the signs are different and modulo not zero, round down
        if ((x ^ y) < 0 && (r * y != x)) {
            r--;
        }
        return r;
    }

    public static int floorMod(int x, int y) {
        int r = x - floorDiv(x, y) * y;
        return r;
    }

    public static long floorMod(long x, long y) {
        return x - floorDiv(x, y) * y;
    }

    public static int abs(int a) {
        return (a < 0) ? -a : a;
    }

    public static long abs(long a) {
        return (a < 0) ? -a : a;
    }

    public static float abs(float a) {
        return (a <= 0.0F) ? 0.0F - a : a;
    }

    public static double abs(double a) {
        return (a <= 0.0D) ? 0.0D - a : a;
    }

    public static int min(int a, int b) {
        return (a <= b) ? a : b;
    }

    public static int max(int a, int b) {
        return (a >= b) ? a : b;
    }

    public static double sinh(double a) {
        return CastUtil.toDouble(Script.code("Math.sinh(",CastUtil.toObject(a),")"));
    }

    public static double cosh(double x) {
        return CastUtil.toDouble(Script.code("Math.cosh(",CastUtil.toObject(x),")"));
    }

    public static double tanh(double x) {
        return CastUtil.toDouble(Script.code("Math.tanh(",CastUtil.toObject(x),")"));
    }
}
