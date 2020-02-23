package book.chapter05.p5_4_2;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

import java.util.concurrent.TimeUnit;

import static book.chapter05.CommonUtil.LOCAL_HOST;

//使用Curator创建节点
public class Create_Node_Sample {
    static String path = "/zk-book/c1";
    static CuratorFramework client = CuratorFrameworkFactory.builder()
            .connectString(LOCAL_HOST)
            .sessionTimeoutMs(5000)
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
    public static void main(String[] args) throws Exception {
        client.start();
        client.create()
              .creatingParentsIfNeeded()
              .withMode(CreateMode.EPHEMERAL)
              .forPath(path, "init".getBytes());

        TimeUnit.SECONDS.sleep(20);
        System.out.println("over");
    }
}