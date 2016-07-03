package org.tamier.symbolic;
import org.checkerframework.dataflow.cfg.JavaSource2CFGDOT;

public class SymbolicPlayground {
    /* Configuration: change as appropriate */
    // input file name and path
    static String inputFile = "//home/tamier/jsr308/SymbolicExecution/src/test/SymbolicTest3.java";
    static String outputDir = "/home/tamier"; // output directory
    static String method = "foo"; // name of the method to analyze
    public static String clazz = "test.SymbolicTest3"; // name of the class to
                                                  // consider

    public static void main(String[] args) {

        SymbolicTypeProcessor typeProcessor = new SymbolicTypeProcessor();

        JavaSource2CFGDOT.launch(inputFile, outputDir, method, clazz, true, typeProcessor);
    }
}
