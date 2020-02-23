package book.list.zookeeper.curatorframework.create;

import book.list.zookeeper.curatorframework.BaseTest;
import com.alibaba.fastjson.JSON;
import com.sun.tools.javac.util.Assert;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

/**
 * @author erik.wang
 * @date 2020-02-22 14:17
 * @description
 */
public class CreateNodeTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(CreateNodeTest.class);

    static public String PERSISTENT_PATH = "/zk_book/create/persistent_path";

    /**
     *
     */
    @Test
    public void test_syn_create_persistent_node() {

        client.start();
        try {
            client.create()
                    .creatingParentsIfNeeded()
                    .withMode(CreateMode.PERSISTENT)
                    .forPath(PERSISTENT_PATH, PERSISTENT_PATH.getBytes());
        } catch (Exception e) {
            // 当结点已经存在时，是会报错的:NodeExistsException
            logger.info("create zk node error with e = ", e);
            Assert.error();
        }
    }

    @Test
    public void test_async_create_persistent_node() {
        client.start();
        try {
            client.create().withMode(CreateMode.PERSISTENT)
                    .inBackground(new BackgroundCallback() {
                        @Override
                        public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
                            logger.info("event = {}", event);
                        }
                    }).forPath(PERSISTENT_PATH);
        } catch (Exception e) {
            logger.info("for path error with e = {}", e);
            Assert.error();
        }
        // 让程序运营一会；如果函数结束了，那么回调就看不到效果了。
        try {
            TimeUnit.SECONDS.sleep(5);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        logger.info("test over");
    }

    /**
     * 这个test写的不错吧，可惜没啥大的价值
     */
    @Test
    public void test_check_exist_node() {
        client.start();
        try {
            Stat stat = client.checkExists().forPath(PERSISTENT_PATH);
            Assert.checkNonNull(stat);
            logger.info("stat = {}", JSON.toJSONString(stat));
            stat = client.checkExists().forPath(PERSISTENT_PATH + "/not_exist");
            Assert.checkNull(stat);
            logger.info("stat = {}", JSON.toJSONString(stat));
        } catch (Exception e) {
            logger.info("check exist error with e = ");
            Assert.error();
        }
    }


}
