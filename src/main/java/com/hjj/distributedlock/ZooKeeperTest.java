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
        zooKeeper = new ZooKeeper("192.168.247.130:2181", 5000, watchedEvent -> {
            try {
                List<String> children = zooKeeper.getChildren("/", true);
                System.out.println("---------start--------------");
                System.out.println(children);
                System.out.println("----------end--------------");
            } catch (KeeperException | InterruptedException e) {
                e.printStackTrace();
            }

        });
        String s = zooKeeper.create("/hello", "world".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
        System.out.println(s);
    }
}
