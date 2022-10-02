package com.liwux.zookeeper.locks;

import com.liwux.zookeeper.config.ZKUtils;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class TestLock {

    ZooKeeper zooKeeper;

    @Before
    public void conn() {
        try {
            zooKeeper = ZKUtils.getZooKeeper();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @After
    public void close(){
        try {
            zooKeeper.close();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void lock(){
        for (int i = 0; i < 10; i++) {
            new Thread(new Runnable() {
                public void run() {
                    WatchCallBack watchCallBack = new WatchCallBack();
                    watchCallBack.setZooKeeper(zooKeeper);
                    String threadName = Thread.currentThread().getName();
                    watchCallBack.setThreadName(threadName);
                    //每一个线程
                    //抢锁
                    watchCallBack.tryLock();
                    //干活
                    System.out.println(threadName+" at work");

                    //释放锁
                    watchCallBack.unLock();
                }
            }).start();
        }
    }
}
