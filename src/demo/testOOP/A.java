package demo.testOOP;

class A extends B {
    void run() {
        try {
            Thread.sleep(100); // giả lập công việc mất 100ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("A is running");
    }
}

