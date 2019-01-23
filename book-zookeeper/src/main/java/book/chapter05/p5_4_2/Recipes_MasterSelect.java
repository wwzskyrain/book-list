package book.chapter05.p5_4_2;

import book.chapter05.CommonUtil;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.leader.LeaderSelector;
import org.apache.curator.framework.recipes.leader.LeaderSelectorListenerAdapter;
import org.apache.curator.retry.ExponentialBackoffRetry;

public class Recipes_MasterSelect {

    static String master_path = "/curator_recipes_master_path";

    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(CommonUtil.LOCAL_HOST)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();

    public static void main(String[] args) throws Exception {
        client.start();
        LeaderSelector selector = new LeaderSelector(client,
                master_path,
                new LeaderSelectorListenerAdapter() {
                    public void takeLeadership(CuratorFramework client) throws Exception {

                        String clientString = client.getConfig().toString();
                        System.out.println("Client " + clientString + " become the master.");
                        Thread.sleep(3000);
                        System.out.println("Client " + clientString + " release the master.");
                    }
                });
        selector.autoRequeue();
        selector.start();
        Thread.sleep(Integer.MAX_VALUE);
    }
}