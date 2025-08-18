package demo.testOOP;

class B {
    void run() {
        try {
            Thread.sleep(200); // giả lập công việc mất 200ms
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("B is running");
    }
}

