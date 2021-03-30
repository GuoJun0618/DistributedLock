package com.hjj.distributedlock;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.util.List;

/**
 * @author junguo
 */
public class ZooKeeperTest {
    static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws InterruptedException, IOException, KeeperException {
        ZooKeeperTest.createClient();
    }

    public static void createClient() throws IOException, KeeperException, InterruptedException {
        zooKeeper = new ZooKeeper("192.168.226.128:2181", 2000, watchedEvent -> {
            try {
                List<String> children = zooKeeper.getChildren("/", true);
                System.out.println("---------start--------------");
                children.forEach(System.out::println);
                System.out.println("----------end--------------");
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

        });
        String s = zooKeeper.create("/hello", "world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
        System.out.println(s);
        String s3 = zooKeeper.create("/guo", "world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(s3);
       /* String s2 = zooKeeper.create("/hello2", "world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        System.out.println(s2);*/
        Stat exists = zooKeeper.exists("/guojun", true);
        System.out.println(exists);
        Thread.sleep(Integer.MAX_VALUE);

    }
}
