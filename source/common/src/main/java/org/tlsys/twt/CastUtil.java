package org.tlsys.twt;

import org.tlsys.twt.members.VClass;

public final class CastUtil {

    private static int getCastLevel_Primitive(VClass from, VClass to) {
        if (from == to)
            return 0;

        if (from.getRealTimeName().equals(byte.class) || from.getRealTimeName().equals(Byte.class)) {
            if (to.getRealTimeName().equals(double.class) || to.getRealTimeName().equals(Double.class))
                return 5;
            if (to.getRealTimeName().equals(float.class) || to.getRealTimeName().equals(Float.class))
                return 4;
            if (to.getRealTimeName().equals(long.class) || to.getRealTimeName().equals(Long.class))
                return 3;
            if (to.getRealTimeName().equals(int.class) || to.getRealTimeName().equals(Integer.class))
                return 2;
            if (to.getRealTimeName().equals(short.class) || to.getRealTimeName().equals(Short.class))
                return 1;

            if (to.getRealTimeName().equals(byte.class) || to.getRealTimeName().equals(Byte.class))
                return 0;
        }

        if (from.getRealTimeName().equals(short.class) || from.getRealTimeName().equals(Short.class)) {
            if (to.getRealTimeName().equals(double.class) || to.getRealTimeName().equals(Double.class))
                return 4;
            if (to.getRealTimeName().equals(float.class) || to.getRealTimeName().equals(Float.class))
                return 3;
            if (to.getRealTimeName().equals(long.class) || to.getRealTimeName().equals(Long.class))
                return 2;
            if (to.getRealTimeName().equals(int.class) || to.getRealTimeName().equals(Integer.class))
                return 1;

            if (to.getRealTimeName().equals(short.class) || to.getRealTimeName().equals(Short.class))
                return 0;
        }

        if (from.getRealTimeName().equals(int.class) || from.getRealTimeName().equals(Integer.class)) {
            if (to.getRealTimeName().equals(double.class) || to.getRealTimeName().equals(Double.class))
                return 3;
            if (to.getRealTimeName().equals(float.class) || to.getRealTimeName().equals(Float.class))
                return 2;
            if (to.getRealTimeName().equals(long.class) || to.getRealTimeName().equals(Long.class))
                return 1;

            if (to.getRealTimeName().equals(int.class) || to.getRealTimeName().equals(Integer.class))
                return 0;
        }

        if (from.getRealTimeName().equals(long.class) || from.getRealTimeName().equals(Long.class)) {
            if (to.getRealTimeName().equals(double.class) || to.getRealTimeName().equals(Double.class))
                return 2;
            if (to.getRealTimeName().equals(float.class) || to.getRealTimeName().equals(Float.class))
                return 1;

            if (to.getRealTimeName().equals(long.class) || to.getRealTimeName().equals(Long.class))
                return 0;
        }

        if (from.getRealTimeName().equals(float.class) || from.getRealTimeName().equals(Float.class)) {
            if (to.getRealTimeName().equals(double.class) || to.getRealTimeName().equals(Double.class))
                return 1;

            if (to.getRealTimeName().equals(float.class) || to.getRealTimeName().equals(Float.class))
                return 0;
        }

        if (from.getRealTimeName().equals(char.class) || from.getRealTimeName().equals(Character.class)) {
            if (to.getRealTimeName().equals(char.class) || to.getRealTimeName().equals(Character.class))
                return 0;
        }

        VClass cl = from;
        int r = 0;
        while (cl != null) {
            if (cl == to)
                return r;
            r++;
            cl = cl.getSuperClass();
        }

        return -1;
    }
}
