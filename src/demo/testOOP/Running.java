package demo.testOOP;

public class Running {
    public static void main(String[] args) {
        A a = new A();
        B b = new B();
        C c = new C();

        long startA = System.currentTimeMillis();
        a.run();
        long endA = System.currentTimeMillis();
        System.out.println("Time for A: " + (endA - startA) + " ms");

//        long startB = System.currentTimeMillis();
//        b.run();
//        long endB = System.currentTimeMillis();
//        System.out.println("Time for B: " + (endB - startB) + " ms");
//
//        long startC = System.currentTimeMillis();
//        c.run();
//        long endC = System.currentTimeMillis();
//        System.out.println("Time for C: " + (endC - startC) + " ms");
    }

}
