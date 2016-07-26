package test;

import org.tamier.symbolic.SymbolicPlayground;

/**
 * Launcher class to invoke the method we are symbolically executing from
 * SymbolicPlayground with solver's solutions for each method parameters
 *
 * @author tamier
 *
 */
public class TestLauncher {
    public static void launchBinary(int a, int b) {
        BinaryTest test = getBinaryTest();
        // Run the test
        test.foo(a, b);
    }

    private static BinaryTest getBinaryTest() {

        Class<?> claz;
        Object r = null;
        try {
            // Dynamically lookup testcase class name from SymbolicPlayground
            claz = Class.forName(SymbolicPlayground.clazz);
            // Invoke the constructor of that class
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
            // Similar to the above
            claz = Class.forName(SymbolicPlayground.clazz);
            r = claz.getConstructor().newInstance();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return (TernaryTest) r;
    }
}
