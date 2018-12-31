package book.chapter05.p5_4_1;

import book.chapter05.CommonUtil;
import org.I0Itec.zkclient.ZkClient;

//ZkClient检测节点是否存在
public class Exist_Node_Sample {
    public static void main(String[] args) throws Exception {
        String path = "/zk-book";
        ZkClient zkClient = new ZkClient(CommonUtil.LOCAL_HOST, 2000);
        System.out.println("Node " + path + " exists " + zkClient.exists(path));
    }
}