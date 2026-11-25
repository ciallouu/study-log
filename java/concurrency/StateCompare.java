public class StateCompare {
    public static void main(String[] args) throws Exception {
        // A. sleep → TIMED_WAITING
        Thread a = new Thread(() -> { try { Thread.sleep(3000); } catch (Exception ignored) {} });
        a.start(); Thread.sleep(100);
        System.out.println("sleep(3000)      → " + a.getState());

        // B. 两个线程抢同一把锁 → 后到的进 BLOCKED
        Object lock = new Object();
        Runnable job = () -> { synchronized (lock) { try { Thread.sleep(3000); } catch (Exception ignored) {} } };
        Thread b1 = new Thread(job); Thread b2 = new Thread(job);
        b1.start(); b2.start(); Thread.sleep(200);
        System.out.println("抢锁失败的那个    → " + b2.getState());

        // C. t.join() 无参无限等 → WAITING
        Thread c = new Thread(() -> { try { Thread.sleep(3000); } catch (Exception ignored) {} });
        c.start(); Thread.sleep(100);
        System.out.println("c 睡着的状态      → " + c.getState());
        new Thread(() -> { try { c.join(); } catch (Exception ignored) {} }).start();
        Thread.sleep(100);
        System.out.println("（上面这个 join 的线程在 WAITING，但它在 lambda 里拿不到引用）");
    }
}