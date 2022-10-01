package com.liwux.zookeeper.config;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StatCallback, AsyncCallback.DataCallback {

    ZooKeeper zooKeeper;
    MyConf myConf;

    CountDownLatch countDownLatch=new CountDownLatch(1);

    public MyConf getMyConf() {
        return myConf;
    }

    public void setMyConf(MyConf myConf) {
        this.myConf = myConf;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
        if (bytes!=null){
            String s1 = new String(bytes);
            myConf.setConf(s1);
            countDownLatch.countDown();
        }
    }

    public void processResult(int i, String s, Object o, Stat stat) {
        if (stat!=null){
            zooKeeper.getData("/AppConf",this,this,"qwe");
        }
    }

    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                zooKeeper.getData("/AppConf",this,this,"qwe");
                break;
            case NodeDeleted:
                //容忍性
                myConf.setConf("");
                countDownLatch = new CountDownLatch(1);
                break;
            case NodeDataChanged:
                zooKeeper.getData("/AppConf",this,this,"qwe");
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

    public void await(){
        zooKeeper.exists("/AppConf",this,this,"abc");
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
