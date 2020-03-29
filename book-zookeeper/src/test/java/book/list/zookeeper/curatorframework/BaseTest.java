package book.list.zookeeper.curatorframework;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Before;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static book.chapter05.CommonUtil.LOCAL_HOST;

/**
 * @author erik.wang
 * @date 2020-02-22 15:36
 * @description
 */
public class BaseTest {

    protected CuratorFramework client;

    private static AtomicInteger atomicInteger = new AtomicInteger(1);

    public static ThreadFactory threadFactory = new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("erik_thread" + atomicInteger.getAndIncrement());
            return thread;
        }
    };

    @Before
    public void init() {
        client = CuratorFrameworkFactory.builder()
                .connectString(LOCAL_HOST)
                .sessionTimeoutMs(5000)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
    }

    protected void waitSomeSecond(Integer secondNumber) {
        try {
            TimeUnit.SECONDS.sleep(secondNumber);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
