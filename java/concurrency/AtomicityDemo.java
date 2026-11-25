public class AtomicityDemo {
//    static int count=0;
//    static synchronized void inc() { count++; }
// 循环体改成 inc();
static java.util.concurrent.atomic.AtomicInteger count =
        new java.util.concurrent.atomic.AtomicInteger();
// 循环体改成 count.incrementAndGet();
// 打印改成 count.get()

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        Thread[] ts = new Thread[10];
        for (int i = 0; i < 10; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) count.incrementAndGet();
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();
        System.out.println("期望 100000，实际 " + count.get()
                + "，耗时 " + (System.currentTimeMillis() - start) + "ms");
    }
}