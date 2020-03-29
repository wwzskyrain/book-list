package book.list.zookeeper.curatorframework.recipes;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.curator.framework.recipes.leader.LeaderLatch;
import org.apache.curator.framework.recipes.leader.LeaderLatchListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author erik.wang
 * @date 2020-02-23 18:40
 * @description
 */
public class LeaderLatchTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(LeaderLatchTest.class);
    String leaderLatchPath = "/zk_book/recipe/leaderLatch";

    @Test
    public void test() throws InterruptedException {
        client.start();
        client.getZookeeperClient().blockUntilConnectedOrTimedOut();

        ExecutorService threadPool = Executors.newCachedThreadPool(threadFactory);
        for (int i = 0; i < 5; i++) {
            String participateId = "P_" + i;
            threadPool.submit(() -> {
                LeaderLatch leaderLatch = new LeaderLatch(client, leaderLatchPath, participateId);
                leaderLatch.addListener(new LeaderLatchListener() {
                    @Override
                    public void isLeader() {
                        logger.info("happy, I am leader , said participate={}", leaderLatch.getId());
                        waitSomeSecond(3);
                        try {
                            leaderLatch.close(LeaderLatch.CloseMode.SILENT);
                            leaderLatch.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void notLeader() {
                        logger.info("sad, I am not leader , said thread={}", Thread.currentThread().getName());
                    }
                }, threadPool);

                try {
                    leaderLatch.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
        waitSomeSecond(60 * 10);
    }

}
