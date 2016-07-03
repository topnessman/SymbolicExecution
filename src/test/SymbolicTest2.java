package test;

//With imfeasible path
public class SymbolicTest2 implements BinaryTest {
    public void foo(int a, int b) {
        int e = b + 10;
        int d = e + a;
        d = e + b;
        a = a + d;
        a = a - b;
        if (a > d) {
            System.out.println("Then");
        } else {
            System.out.println("Else");
        }
        int c = a - b;
        c = d - c;
        if (c < e) {
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

