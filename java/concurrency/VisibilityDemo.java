//可见性实验
public class VisibilityDemo {
    static volatile boolean flag = false;          // 故意不加 volatile

    public static void main(String[] args) throws Exception {
        Thread worker = new Thread(() -> {
            long i = 0;
            while (!flag) { i++; }        // 循环体非空，更接近真实场景
            System.out.println("worker 退出，循环了 " + i + " 次");
        });
        worker.start();

        Thread.sleep(1000);
        flag = true;
        System.out.println("主线程已把 flag 改成 true");

        worker.join(3000);                // 最多等 3 秒，避免程序卡死
        if (worker.isAlive()) {
            System.out.println(">>> 3 秒还没退出：worker 看不到 flag 的新值 ← 可见性问题");
        } else {
            System.out.println(">>> worker 正常退出了（本次 JIT 没做提升优化）");
        }
    }
}