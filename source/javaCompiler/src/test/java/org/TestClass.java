package org;

public class TestClass {

    int hh = 99;

    {
        System.out.println("dd" + hh);
    }

    public TestClass() {
    }

    public TestClass(int hh) {
        this.hh = hh;
    }

    public void doit(final int a) {
        MyClass kot = new MyClass();
        kot.test(11);
        int b = a +8;
        final int cc = 22;
        ComTest.tempMethod();
        TestClass gg = new TestClass();
        ComTest.getLambda((g,v)->{
            return a+b + hh;
        });

    }

    public static void main(String[] args) {

    }
}
