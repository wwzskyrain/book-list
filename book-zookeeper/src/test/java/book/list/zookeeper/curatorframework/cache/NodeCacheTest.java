package book.list.zookeeper.curatorframework.cache;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.zookeeper.CreateMode;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-02-22 18:08
 * @description
 */
public class NodeCacheTest extends BaseTest {

    private String path = "/zk_book/nodeCache/water";
    private static final Logger logger = LoggerFactory.getLogger(NodeCacheTest.class);

    /**
     * 测试被通知的三种情况：
     * 1.结点不在了
     * 2.结点在没数据
     * 3.结点在数据更新
     * 4.注意：子节点任何的变动都于此无关
     * @throws Exception
     */
    @Test
    public void test_node_cache_listener() throws Exception {
        client.start();
        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(path, "init".getBytes());
        final NodeCache cache = new NodeCache(client, path, false);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {

                ChildData currentData = cache.getCurrentData();

                if (currentData != null) { // case1：结点还在
                    logger.info("node is exist.");
                    byte[] dataByte = currentData.getData();
                    if (dataByte != null) {  // case2：结点没有数据
                        logger.info("found data={}", new String(dataByte));
                    } else {
                        logger.info("not found data, maybe node without data");
                    }
                } else { // case3：结点被删除了
                    logger.info("maybe node is deleted");
                }
                logger.info("listener over ---------");
            }
        });

        client.setData().forPath(path, "u".getBytes());
        Thread.sleep(1000);
        System.out.println("to delete node");
        client.delete().deletingChildrenIfNeeded().forPath(path);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     *  不需要首先create node的；可以监听一个不保存在的结点
     */
    @Test
    public void test_who_is_first_creation_node_or_cache_start() {
        client.start();
        NodeCache nodeCache = new NodeCache(client, path, false);
        try {
            nodeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        nodeCache.getListenable().addListener(new NodeCacheListener() {
            @Override
            public void nodeChanged() throws Exception {
                // 事件处理就简单过了，可以参考上一个例子
                logger.info("nodeChanged ----");
            }
        });
        pause();
    }

    private void pause() {
        try {
            TimeUnit.SECONDS.sleep(60 * 5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
