package test;

public class SymbolicTest3 implements TernaryTest {

    @Override
    public void foo(int a, int b, int c) {
        int d = a + b;
        int e = b + c;
        if (d >= e) {
            System.out.println("Then");
        } else {
            System.out.println("Else");
        }
        a = a + c;
        if (a == b) {
            System.out.println("Then");
        } else {
            System.out.println("Else");
        }

    }

    public static void main(String[] args) {
        int a = Integer.parseInt(args[0]);
        int b = Integer.parseInt(args[1]);
        int c = Integer.parseInt(args[2]);
        SymbolicTest3 t = new SymbolicTest3();
        t.foo(a, b, c);
    }
}
