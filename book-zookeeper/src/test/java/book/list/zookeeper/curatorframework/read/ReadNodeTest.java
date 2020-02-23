package book.list.zookeeper.curatorframework.read;

import book.list.zookeeper.curatorframework.BaseTest;
import org.apache.zookeeper.CreateMode;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author erik.wang
 * @date 2020-02-22 15:35
 * @description
 */
public class ReadNodeTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ReadNodeTest.class);

    private String PATH_FOR_NODE_WITH_DATA = "/zk_book/read/node_with_data";

    @Test
    public void test_read_data_of_specific_node() {

        client.start();
        String nodeData = "node_data";
        try {
            create_a_ephemeral_node_with_data(nodeData);
        } catch (Exception e) {
            logger.info("test over because creating ephemeral node error with e = {}", e);
            return;
        }

        byte[] readData = null;
        try {
            readData = client.getData()
                    .forPath(PATH_FOR_NODE_WITH_DATA);
        } catch (Exception e) {
            logger.info("forPath error with e = {}", e);
        }
        logger.info("readData = {}", new String(readData));
        Assert.assertTrue(nodeData.equals(new String(readData)));
        waitSomeSecond(10);
    }

    /**
     * 功能：测试读取指定结点下的所有子节点数据
     * 方法：先找的指定结点下的所有子目录，然后遍历目录，以找到他们的数据
     */
    @Test
    public void test_read_child_node_() {
        try {
            //运行该程序前，先创建一个持久结点'/zk_book/read/node_with_data'
            client.start();
            List<String> childrenPathNames = client.getChildren().forPath(PATH_FOR_NODE_WITH_DATA);
            for (String childrenPathName : childrenPathNames) {
                String childPath = PATH_FOR_NODE_WITH_DATA + "/" + childrenPathName;
                byte[] childNodeData = client.getData().forPath(childPath);
                logger.info("path = {}, data = {}", childNodeData == null ? "null" : new String(childNodeData));
            }
            logger.info("childrenPathNames={}", childrenPathNames);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void create_a_ephemeral_node_with_data(String data) throws Exception {

        client.create()
                .creatingParentsIfNeeded()
                .withMode(CreateMode.EPHEMERAL)
                .forPath(PATH_FOR_NODE_WITH_DATA, data.getBytes());
    }
}
