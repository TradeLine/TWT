package org;

import org.jetbrains.annotations.NotNull;

public class TestClass {

    static{
        System.out.println("!!!!!!!");
    }

    int hh = 99;

    {
        System.out.println("dd" + hh);
    }

    public TestClass() {
    }

    public TestClass(int hh, long k) {
        this.hh = hh;
    }

    @NotNull
    public void doit(final int a, final long j) {
        TestClass.class.getClass();
        MyClass kot = new MyClass();
        kot.test(11);
        GGG o = TestClass::aa;
        int b = a +8;
        final int cc = 22;
        ComTest.tempMethod();
        TestClass gg = new TestClass();
        ComTest.getLambda((g,v)->{
            return a+b + hh + (int)j;
        });

    }

    public static void aa() {
    }

    public static void aa(int b) {
    }

    public static void main(String[] args) {

    }

    public interface GGG {
        public void doit();
    }
}
