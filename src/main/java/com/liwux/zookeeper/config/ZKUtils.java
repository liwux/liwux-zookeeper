package com.liwux.zookeeper.config;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZKUtils {

    public static ZooKeeper zooKeeper;

    private static final String address= "192.168.1.11:2181,192.168.1.12:2181,192.168.1.13:2181,192.168.1.14:2181/testLock";

    private static final DefaultWatch watch = new DefaultWatch();

    private static final CountDownLatch countDownLatch = new CountDownLatch(1);

    public static ZooKeeper getZooKeeper() throws InterruptedException, IOException {

        zooKeeper = new ZooKeeper(address,1000,watch);
        watch.setCountDownLatch(countDownLatch);
        countDownLatch.await();

        return zooKeeper;
    }

}
