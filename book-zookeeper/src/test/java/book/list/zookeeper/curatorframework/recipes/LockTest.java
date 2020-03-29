package book.list.zookeeper.curatorframework.recipes;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author erik.wang
 * @date 2020-02-23 15:00
 * @description
 */
public class LockTest extends BaseTest {

    private String lock_path = "/zk_book/recipe/lock";
    private static final Logger logger = LoggerFactory.getLogger(LockTest.class);

    /**
     * 分布式锁，这个很简单。
     * 1.注意，这里有一个漏洞：即使用了锁，如果性能很好的话，还是有可能产生相同订单好的。
     * 2.所以，订单号的生成不能用时间序列；要用atomicLong这种逻辑。
     */
    @Test
    public void test_lock() {
        client.start();
        final InterProcessMutex lock = new InterProcessMutex(client, lock_path);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 30; i++) {
            threadPool.submit(new Runnable() {
                public void run() {
                    try {
                        countDownLatch.await();
                        lock.acquire();
                    } catch (Exception e) {
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss|SSS");
                    String orderNo = sdf.format(new Date());
                    System.out.println("the generated order No is  : " + orderNo);
                    try {
                        lock.release();
                    } catch (Exception e) {
                    }
                }
            });
        }
        countDownLatch.countDown();
        waitSomeSecond(60 * 10);
    }
}
