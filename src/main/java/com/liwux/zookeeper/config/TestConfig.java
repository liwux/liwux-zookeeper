package com.liwux.zookeeper.config;
import org.apache.zookeeper.ZooKeeper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

public class TestConfig {

    ZooKeeper zooKeeper;

    @Before
    public void conn() {
        try {
            zooKeeper = ZKUtils.getZooKeeper();
        } catch (InterruptedException e) {
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
    public void getConf(){
        WatchCallBack watchCallBack = new WatchCallBack();
        watchCallBack.setZooKeeper(zooKeeper);
        MyConf conf = new MyConf();
        watchCallBack.setMyConf(conf);


        watchCallBack.await();

        //1.节点不存在
        //2.节点存在
        while (true){
            if (conf.getConf().equals("")){
                System.out.println("配置清空");
                watchCallBack.await();
            }else {
                System.out.println(conf.getConf());
            }
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
