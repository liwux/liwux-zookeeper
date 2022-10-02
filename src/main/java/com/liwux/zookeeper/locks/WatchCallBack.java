package com.liwux.zookeeper.locks;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class WatchCallBack implements Watcher, AsyncCallback.StringCallback, AsyncCallback.Children2Callback,AsyncCallback.StatCallback,AsyncCallback.DataCallback {

    ZooKeeper zooKeeper;
    String threadName;
    CountDownLatch countDownLatch=new CountDownLatch(1);


    String pathName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }


    public ZooKeeper getZooKeeper() {
        return zooKeeper;
    }

    public void setZooKeeper(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void tryLock(){
        try {
            System.out.println(threadName + "  create....");
            zooKeeper.create("/lock",threadName.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL,this,"abc");
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void unLock(){
        try {
            zooKeeper.delete("/",-1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        }

    }

    public void process(WatchedEvent watchedEvent) {
        switch (watchedEvent.getType()) {
            case None:
                break;
            case NodeCreated:
                break;
            case NodeDeleted:
                zooKeeper.getChildren("/",false,this ,"sdf");
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

    public void processResult(int i, String s, Object o, String s1) {
        if (s1!=null){
            System.out.println(threadName+"create node:"+ s);
            pathName = s1;
            zooKeeper.getChildren("/",false,this,"adb");
        }

    }

    //getChildren call back
    public void processResult(int i, String s, Object o, List<String> list, Stat stat) {
        //一定能看到自己前面的
        System.out.println(threadName+"look locks....");
        Collections.sort(list);
        int j = list.indexOf(pathName.substring(1));


        //是不是第一个
        if(j == 0){
            //yes
            System.out.println(threadName +" i am first....");
            try {
                zooKeeper.setData("/",threadName.getBytes(),-1);
                countDownLatch.countDown();

            } catch (KeeperException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }else{
            //no
            zooKeeper.exists("/"+list.get(i-1),this, this,"sdf");
        }

    }

    public void processResult(int i, String s, Object o, Stat stat) {

    }

    public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {

    }
}
