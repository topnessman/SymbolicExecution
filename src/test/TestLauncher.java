package test;

import org.tamier.symbolic.SymbolicPlayground;

public class TestLauncher {
    public static void launchBinary(int a, int b) {
        BinaryTest test = getBinaryTest();
        test.foo(a, b);
    }

    private static BinaryTest getBinaryTest() {

        Class<?> claz;
        Object r = null;
        try {
            claz = Class.forName(SymbolicPlayground.clazz);
            r = claz.getConstructor().newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (BinaryTest)r;

    }

    public static void launchTernary(int a, int b, int c) {
        TernaryTest test = getTernaryTesst();
        test.foo(a, b, c);
    }

    private static TernaryTest getTernaryTesst() {
        Class<?> claz;
        Object r = null;
        try {
            claz = Class.forName(SymbolicPlayground.clazz);
            r = claz.getConstructor().newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (TernaryTest) r;
    }
}
