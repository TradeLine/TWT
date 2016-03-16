package org.tlsys.twt;

import static org.junit.Assert.*;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

public class RefUtilsTest {

    @FunctionalInterface
    public interface T0 {
        public void doit(int a, float b);
    }

    @Test
    public void testGetArgumentDescription() throws NoSuchMethodException {
        Class v1 = new T0() {

            @Override
            public void doit(int a, float b) {

            }
        }.getClass();
        RefUtils.ConstructorArgumentDescription ca = RefUtils.getArgumentDescription(v1.getDeclaredConstructors()[0]);

        Field[] fields = v1.getFields();
        Constructor[] constructors = v1.getDeclaredConstructors();
        Parameter[] params = v1.getDeclaredConstructors()[0].getParameters();
        Class dec = v1.getDeclaringClass();
        Class enc = v1.getEnclosingClass();
        boolean annon = v1.isAnonymousClass();

        System.out.println("123" + ca + v1 + constructors + params);
    }

    @Test
    public void testGetDependencyClass() {
        assertNull(RefUtils.getDependencyClass(RefUtilsTest.class));
        assertNull(RefUtils.getDependencyClass(T2.class));
        assertNull(RefUtils.getDependencyClass(I1.class));
        assertNull(RefUtils.getDependencyClass(I2.class));

        assertEquals(RefUtils.getDependencyClass(T1.class), RefUtilsTest.class);
    }

    public class T1 {

    }

    public static class T2 {

    }

    public interface I1 {

    }

    public static interface I2 {

    }
}
