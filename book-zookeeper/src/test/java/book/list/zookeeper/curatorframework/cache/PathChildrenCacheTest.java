package book.list.zookeeper.curatorframework.cache;

import book.list.zookeeper.curatorframework.BaseTest;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-02-23 09:48
 * @description
 */
public class PathChildrenCacheTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PathChildrenCacheTest.class);

    private static final String path = "/zk_book/pathChildrenCache";

    /**
     * 测试监听消息:以下三种，都只是监听子节点的变动
     * 1.CHILD_ADDED
     * 2.CHILD_UPDATED
     * 3.CHILD_REMOVED
     * 注意：
     *  1.对父节点和二级子节点的一切crud都是不会触发事件的
     *  2.第一次运行时，对已经存在的子节点，是会触发add事件的
     *  3.相比于TreeCache，PathChildrenCache有时候更适合某些场景，比如business-monitor
     */
    @Test
    public void test_trigger_case_for_listener() {
        client.start();
        PathChildrenCache pathChildrenCache = new PathChildrenCache(client, path, true);
        try {
            pathChildrenCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        pathChildrenCache.getListenable().addListener(new PathChildrenCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {

                if (event.getData() == null) {
                    // 从此可见，除了switch的三种常见事件类型外，还有其他类型，比如CONNECTION_RECONNECTED
                    logger.info("event = {}", JSON.toJSONString(event));
                    return;
                }
                String eventPath = event.getData().getPath();
                byte[] eventData = event.getData().getData();
                switch (event.getType()) {
                    case CHILD_ADDED:
                        logger.info("node add, path={}, data={}", eventPath, eventData == null ? "null" : new String(eventData));
                        break;
                    case CHILD_UPDATED:
                        logger.info("node update, path={}, data={}", eventPath, eventData == null ? "null" : new String(eventData));
                        break;
                    case CHILD_REMOVED:
                        logger.info("node remove, path={}, data={}", eventPath, eventData == null ? "null" : new String(eventData));
                        break;
                    default:
                        logger.info("event = {}", JSON.toJSONString(event));

                }
            }
        });

        waitSomeSecond(60 * 10);
    }


}
