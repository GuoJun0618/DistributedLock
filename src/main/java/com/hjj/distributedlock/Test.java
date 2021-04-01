package com.hjj.distributedlock;

import com.hjj.distributedlock.lock.ZkLock;
import org.apache.zookeeper.KeeperException;

/**
 * @author junguo
 */
public class Test {
    private int data = 0;

    public static void main(String[] args) {
        Test test = new Test();
        test.test();
        System.out.println(test.data);
    }

    private void test() {
        Thread thread1 = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 100; i++) {
                        ZkLock zkLock = ZkLock.create("192.168.247.130:2181");
                        zkLock.lock();
                        System.out.println("线程11拿到锁" + data);
                        data++;
                        zkLock.unlock();
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        Thread thread2 = new Thread() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < 1000; i++) {
                        ZkLock zkLock = ZkLock.create("192.168.247.130:2181");
                        zkLock.lock();
                        System.out.println("线程22拿到锁");
                        data++;
                        zkLock.unlock();
                    }
                } catch (KeeperException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        thread1.start();
        // thread2.start();
    }

}
