package book.chapter05.p5_3_1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import book.chapter05.CommonUtil;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;

//Chapter: 5.3.1 Java API -> 创建连接 -> 创建一个最基本的ZooKeeper对象实例，复用sessionId和
public class ZooKeeper_Constructor_Usage_With_SID_PASSWD implements Watcher {
    private static CountDownLatch connectedSemaphore = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zookeeper = new ZooKeeper(CommonUtil.LOCAL_HOST,
                5000, //
                new ZooKeeper_Constructor_Usage_With_SID_PASSWD());
        connectedSemaphore.await();
        long sessionId = zookeeper.getSessionId();
        byte[] passwd = zookeeper.getSessionPasswd();

        //Use illegal sessionId and sessionPassWd
        zookeeper = new ZooKeeper(CommonUtil.LOCAL_HOST,
                5000, //
                new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
                1l,//
                "test".getBytes());
        //Use correct sessionId and sessionPassWd
        zookeeper = new ZooKeeper(CommonUtil.LOCAL_HOST,
                5000, //
                new ZooKeeper_Constructor_Usage_With_SID_PASSWD(),//
                sessionId,//
                passwd);
        Thread.sleep(Integer.MAX_VALUE);
    }

    /**
     * 每两秒就会有一个事件，SyncConnected or Disconnected
     * @param event
     */
    public void process(WatchedEvent event) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        System.out.println(format.format(new Date()) + "Receive watched event：" + event);
        if (KeeperState.SyncConnected == event.getState()) {
            connectedSemaphore.countDown();
        }
    }
}