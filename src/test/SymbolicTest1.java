package test;

public class SymbolicTest1 implements BinaryTest {
    public void foo(int a, int b) {

        a = a - b;
        if (a != 0) {
            System.out.println("Then");
        } else {
            System.out.println("Else");
        }
        int c = a - b;
        if (c >= 0) {
            System.out.println("Then");
        } else {
            System.out.println("Else");
        }
    }

    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        SymbolicTest2 t = new SymbolicTest2();
        t.foo(a, b);
    }
}
