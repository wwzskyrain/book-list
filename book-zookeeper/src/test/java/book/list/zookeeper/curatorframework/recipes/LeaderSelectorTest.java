package book.list.zookeeper.curatorframework.recipes;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListener;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.framework.state.ConnectionState;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static book.chapter05.CommonUtil.LOCAL_HOST;

/**
 * @author erik.wang
 * @date 2020-02-23 15:34
 * @description
 */
public class LeaderSelectorTest {

    private static final Logger logger = LoggerFactory.getLogger(LeaderSelectorTest.class);

    @Test
    public void test_leader_selector() {

        List<CuratorFramework> clients = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            clients.add(CuratorFrameworkFactory.builder()
                    .connectString(LOCAL_HOST)
                    .sessionTimeoutMs(5000)
                    .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                    .build());
        }
        String leaderPath = "/zk_book/recipe/leaderSelector";
        for (CuratorFramework client : clients) {
            client.start();
            LeaderSelector leaderSelector = new LeaderSelector(client, leaderPath, new LeaderSelectorListenerAdapter() {
                @Override
                public void takeLeadership(CuratorFramework client) throws Exception {
                    logger.info("成为了master角色，thread={}", Thread.currentThread().getName());
                    TimeUnit.SECONDS.sleep(3);
                    logger.info("完成了任务，释放master，thread={}", Thread.currentThread().getName());
                }
            });
            leaderSelector.autoRequeue();
            leaderSelector.start();
        }

        waitSomeSecond(60 * 10);
    }


    private void waitSomeSecond(Integer secondName) {
        try {
            TimeUnit.SECONDS.sleep(secondName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
