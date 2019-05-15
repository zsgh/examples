package concurrent;


import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author： song.zh
 * @Date: 2019/1/22
 */
public class AtomicImpl {
    static int CIRCLE = 10000;

    private int value = 0;

    private AtomicInteger ai = new AtomicInteger(0);

    private Lock lock = new ReentrantLock();


    /**
     * not atomic, not thread safe
     * @param num
     * @return
     */
    public Integer getAndAdd(int num) {
        value = value + num;
        return value;
    }

    /**
     * 使用 Synchronized 关键字，独占，性能最差
     * @param num
     * @return
     */
    public Integer getAndAddBySync(int num) {
        synchronized (AtomicImpl.class) {
            value = value + num;
            return value;
        }
        //4次耗时：
        //169423486
        //164726161
        //180850079
        //175135923
    }

    /**
     * 使用lock，性能中等
     * @param num
     * @return
     */
    public Integer getAndAddByLock(int num) {
        lock.lock();
        value = value + num;
        lock.unlock();
        return value;
        //四次耗时
        //89207337
        //94481597
        //92589836
        //83016036
    }

    /**
     * 乐观锁， 性能最好
     * @param num
     * @return
     */
    public Integer getAndAddByCAS(int num) {
        ai.addAndGet(num);
        return ai.intValue();
        //四次耗时：
        //43006190
        //49061933
        //50866746
        //45281958
    }

    static class Computer implements Runnable {

        private AtomicImpl atomic;
        private CountDownLatch latch;
        Computer(AtomicImpl auto, CountDownLatch latch) {
            this.atomic = auto;
            this.latch = latch;
        }
        @Override
        public void run() {
            int i = 0;
            while (i++ < AtomicImpl.CIRCLE) {
                atomic.getAndAddByCAS(1);
            }
            latch.countDown();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        long start = System.nanoTime();
        //启动的线程数量
        int threadNum = 100;
        CountDownLatch latch = new CountDownLatch(threadNum);
        ExecutorService executor = new ThreadPoolExecutor(threadNum, threadNum, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1));
        AtomicImpl atomic = new AtomicImpl();
        for (int i=0; i< threadNum; i++) {
            executor.execute(new Thread(new AtomicImpl.Computer(atomic, latch)));
        }
        latch.await();
        long end = System.nanoTime();
        System.out.println("time elapse:"+(end-start)+", value="+atomic.value +",ai="+atomic.ai.intValue());
        executor.shutdownNow();
    }
}