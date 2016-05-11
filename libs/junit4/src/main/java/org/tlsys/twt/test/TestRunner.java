package org.tlsys.twt.test;

import org.tlsys.twt.annotations.JSClass;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

@JSClass
public class TestRunner {
    private final ArrayList<Class> classes = new ArrayList<>();

    private static void testMethod(Object o, Method method) throws InvocationTargetException, IllegalAccessException {
        for (Method m : o.getClass().getMethods()) {
            if (m.getAnnotation(TWTBefore.class) != null)
                m.invoke(o);
        }

        method.invoke(o);

        for (Method m : o.getClass().getMethods()) {
            if (m.getAnnotation(TWTAfter.class) != null)
                m.invoke(o);
        }
    }

    private static void testClass(Class clazz) throws InvocationTargetException, IllegalAccessException, InstantiationException {

        for (Method m : clazz.getMethods()) {
            if (m.getAnnotation(TWTBeforeClass.class) != null)
                m.invoke(null);
        }

        Object o = clazz.newInstance();


        for (Method m : clazz.getMethods()) {
            if (m.getAnnotation(TWTTest.class) != null)
                testMethod(o, m);
        }

        for (Method m : clazz.getMethods()) {
            if (m.getAnnotation(TWTAfterClass.class) != null)
                m.invoke(null);
        }

    }

    public TestRunner addClass(Class clazz) {
        classes.add(clazz);
        return this;
    }

    public TestRunner run() throws InvocationTargetException, IllegalAccessException, InstantiationException {
        for (Class cl : classes) {
            testClass(cl);
        }

        return this;
    }
}
