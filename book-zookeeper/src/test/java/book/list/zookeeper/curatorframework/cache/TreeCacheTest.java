package book.list.zookeeper.curatorframework.cache;

import book.list.zookeeper.curatorframework.BaseTest;
import com.alibaba.fastjson.JSON;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.cache.ChildData;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author erik.wang
 * @date 2020-02-22 20:10
 * @description
 */
public class TreeCacheTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(TreeCacheTest.class);
    private String path = "/zk_book/treeCache/father";

    /**
     * 三种事件类型，以及触发条件：假设监听的路径为path
     * 1.NODE_ADDED    当前结点的创建、子节点的创建、子节点的子节点的创建；
     * 注意，当TreeNode第一次运行时，如果已经存在path/..，那么也会触发该事件。
     * 2.NODE_UPDATED  上面是那种结点的数据的更新
     * 3.NODE_REMOVED  上面三种结点的删除
     */
    @Test
    public void test_tree_cache_listener() {
        client.start();
        TreeCache treeCache = new TreeCache(client, path);
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                logger.info("event = {}", JSON.toJSONString(event));
                logger.info("childEvent() over.");
            }
        });

        waitSomeSecond(1000);
    }

    /**
     * 获取数据：假设当前监控的是路径是path
     * 1.path的数据如何获取
     * 2.path下子节点的数据如何获取
     * 3.看一下event的结构先
     * {
          "data": {
          "path": "/zk_book/treeCache/watch",
          "stat": {
              "aversion": 0,
              ... : . 省略其他状态
              "version": 0
              }
          },
          "type": "NODE_REMOVED"
     * }
     * 4.答案：基于上一个ut'事件类型和触发条件'，需要做好以下四点就够了
     *      1.能通知的肯定都只监听路径下的变动，所以事件路径一定是path/..
     *      2.所以，先截取"path/"前缀,在判断是否还有"/"，从而过滤调非一级子节点
     *      3.有必要对父节点特殊对待吗？business-monitor的业务逻辑是不需要的。
     *      4.第一次初始化怎么办？简单，程序重新启动时，每个path/..结点都会触发一次add事件，所以，不必在去主动读取一级节点们
     *      5.不得不佩服明哥写代码的老道
     */
    @Test
    public void test_read_data() {
        client.start();
        TreeCache treeCache = new TreeCache(client, path);
        treeCache.getListenable().addListener(new TreeCacheListener() {
            @Override
            public void childEvent(CuratorFramework client, TreeCacheEvent event) throws Exception {
                logger.info("event = {}", JSON.toJSONString(event));

                switch (event.getType()) {
                    case NODE_ADDED:
                        logger.info("node added with path={}", event.getData().getPath());
                        onListener(event);
                        break;
                    case NODE_UPDATED:
                        logger.info("node update with path={}", event.getData().getPath());
                        onListener(event);
                        break;
                    case NODE_REMOVED:
                        //休想在这中case中再去读取data
                        logger.info("node deleted with path={}", event.getData().getPath());
                        break;
                    default: //还有四种情况呢
                        break;
                }
            }
        });
        try {
            treeCache.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        waitSomeSecond(60 * 10);
    }

    private void onListener(TreeCacheEvent event) {
        ChildData childData = event.getData();
        String path = childData.getPath();
        String fatherPath = "/zk_book/treeCache/father";
        String childrenPath = "/zk_book/treeCache/father/";

        if (path.equals(fatherPath)) {
            byte[] fatherNodeData = childData.getData();
            if (fatherNodeData == null) {
                logger.info("the data of father node={} is null", path);
            } else {
                logger.info("the data of father node={} is = {}", path, new String(fatherNodeData));
            }
        } else if (path.startsWith(childrenPath) &&
                !path.substring(childrenPath.length()).contains("/")) {
            logger.info("first level child node with path={}", path);
            byte[] oneChildData = childData.getData();
            if (oneChildData == null) {
                logger.info("the data of path {} is null");
            } else {
                logger.info("the data of path {} is = {}", path, new String(oneChildData));
            }
        } else {
            logger.info("no care with path={}", path);
        }
    }
}
