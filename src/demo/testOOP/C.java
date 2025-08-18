package demo.testOOP;

class C {
    void run() {
        try {
            Thread.sleep(150); // giả lập công việc mất 150ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("C is running");
    }
}
