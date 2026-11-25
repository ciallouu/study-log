public class ThreadStateDemo {
    public static void main(String[] args) {
        Thread t =new Thread(() -> {
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {} });
        System.out.println("创建后 "+t.getState());//预测NEW
        t.start();
        System.out.println("start后 "+t.getState());//预测RUNNABLE
        try { Thread.sleep(100); } catch (InterruptedException ignored) { }
        System.out.println("sleep "+t.getState());//TIMED_WAITING
        try { t.join(); } catch (InterruptedException ignored) { }
        System.out.println("结束 "+t.getState());//TERMINATED
    }
}
