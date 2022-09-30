package com.liwux.zookeeper;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException, KeeperException {

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //zk 是有session概念的，没有线程池的概念
        //watch注册值只发生在 读类型调用：get,exist......
        //第一类，new zk时候，传入 watch,这个watch和session有关系，和path、node没有关系
        ZooKeeper connected = new ZooKeeper("192.168.1.11:2181,192.168.1.12:2181,192.168.1.13:2181,192.168.1.14:2181",
                3000, new Watcher() {
            public void process(WatchedEvent event) {
                Event.KeeperState state = event.getState();
                Event.EventType type = event.getType();
                String path = event.getPath();
                switch (state) {
                    case Unknown:
                        break;
                    case Disconnected:
                        break;
                    case NoSyncConnected:
                        break;
                    case SyncConnected:
                        System.out.println("connected");
                        countDownLatch.countDown();
                        break;
                    case AuthFailed:
                        break;
                    case ConnectedReadOnly:
                        break;
                    case SaslAuthenticated:
                        break;
                    case Expired:
                        break;
                    case Closed:
                        break;
                }

                switch (type) {
                    case None:
                        break;
                    case NodeCreated:
                        break;
                    case NodeDeleted:
                        break;
                    case NodeDataChanged:
                        break;
                    case NodeChildrenChanged:
                        break;
                    case DataWatchRemoved:
                        break;
                    case ChildWatchRemoved:
                        break;
                    case PersistentWatchRemoved:
                        break;
                }
            }
        });

        //线性代码阻塞，直到countdown
        countDownLatch.await();
        ZooKeeper.States state= connected.getState();
        switch (state) {
            case CONNECTING:
                System.out.println("ing......");
                break;
            case ASSOCIATING:
                break;
            case CONNECTED:
                System.out.println("ed");
                break;
            case CONNECTEDREADONLY:
                break;
            case CLOSED:
                break;
            case AUTH_FAILED:
                break;
            case NOT_CONNECTED:
                break;
        }

        String pathName = connected.create("/ooxx", "olddata".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);

        Stat stat = new Stat();

        byte[] node = connected.getData("/ooxx", new Watcher() {
            public void process(WatchedEvent event) {
                System.out.println("getData watch:"+event.toString());
            }
        }, stat);

        System.out.println(new String(node));



        // 触发回调
        Stat stat1 = connected.setData("/ooxx", "newdata1".getBytes(), 0);
        Stat stat2 = connected.setData("/ooxx", "newdata2".getBytes(), stat1.getVersion());
    }
}
