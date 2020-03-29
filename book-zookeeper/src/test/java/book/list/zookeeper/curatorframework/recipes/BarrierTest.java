package book.list.zookeeper.curatorframework.recipes;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author erik.wang
 * @date 2020-02-23 11:14
 * @description
 */
public class BarrierTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(BarrierTest.class);


    /**
     *
     */
    @Test
    public void test_jdk_count_down_latch() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
//        countDownLatch.countDown();  //不注释掉该句，就会自费latch
        ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 5; i++) {
            threadPool.submit(() -> {
                try {
                    logger.info("I am ready to run, said thread={}", Thread.currentThread().getName());
                    countDownLatch.await();
                    logger.info("I am running, said thread={}", Thread.currentThread().getName());
                } catch (InterruptedException e) {
                    logger.info("");
                    e.printStackTrace();
                }
            });
        }

        waitSomeSecond(10);
        countDownLatch.countDown();
    }

    /**
     * 用distribute_barrier来模拟count_down_latch。
     */
    @Test
    public void test_distribute_barrier_seem_to_count_down() {
        client.start();
        String barrierPath = "/zk_book/recipe/barrier";
        DistributedBarrier distributedBarrier = new DistributedBarrier(client, barrierPath);
        ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 5; i++) {
            threadPool.submit(() -> {
                try {
                    logger.info("I am ready to run, said thread={}", Thread.currentThread().getName());
                    distributedBarrier.setBarrier();
                    String threadName = Thread.currentThread().getName();
                    if (!threadName.endsWith("4")) {
                        distributedBarrier.waitOnBarrier();  //如果不主动waitOnBarrier的话，那么前一句setBarrier就没有意思了
                    }
                    logger.info("I am running, said thread={}", Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        waitSomeSecond(5);
        try {
            distributedBarrier.removeBarrier();
            logger.info("remove barrier, said thread={}", Thread.currentThread().getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        waitSomeSecond(10);
    }

    /**
     * 1.如果有一个client，他讲memberQty和大家写的不一致怎么办？那就没得玩了，虽然大家是分布式，但是大家还是一个系统。
     * 2.double barrier就只能是double吗，jdk的barrier可是无限次的
     */
    @Test
    public void test_distribute_double_barrier() {
        client.start();
        String barrierPath = "/zk_book/recipe/double/barrier";

        ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 5; i++) {
            threadPool.submit(() -> {
                try {
                    DistributedDoubleBarrier doubleBarrier = new DistributedDoubleBarrier(client, barrierPath, 5);
                    logger.info("I will enter to run, said thread={}", Thread.currentThread().getName());
                    waitSomeSecond(2);
                    doubleBarrier.enter();

                    logger.info("I am in section, said thread={}", Thread.currentThread().getName());
                    waitSomeSecond(2);

                    doubleBarrier.leave();
                    logger.info("I am leave, said thread={}", Thread.currentThread().getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        waitSomeSecond(60 * 10);
    }
}