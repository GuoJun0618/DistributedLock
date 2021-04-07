package com.hjj.distributedlock.core;

import org.apache.zookeeper.KeeperException;

/**
 * @author junguo
 * @date 2021-03-15
 * 分布式锁核心接口
 */
public interface DistributedLock {

    /**
     * 枷锁，阻塞
     *
     * @throws InterruptedException 中断异常
     * @throws KeeperException      ZK异常
     */
    void lock() throws InterruptedException, KeeperException;

    /**
     * 尝试枷锁，非阻塞
     *
     * @return 枷锁成功与否
     * @throws InterruptedException 中断异常
     * @throws KeeperException      ZK异常
     */
    boolean tryLock() throws KeeperException, InterruptedException;

    /**
     * 尝试一定时间段内枷锁，超时返回
     *
     * @param timeout 超时时间,毫秒
     * @return 枷锁成功与否
     * @throws InterruptedException 中断异常
     * @throws KeeperException      ZK异常
     */
    boolean tryLock(long timeout) throws KeeperException, InterruptedException;

    /**
     * 释放锁
     *
     * @throws InterruptedException 中断异常
     * @throws KeeperException      ZK异常
     */
    void unlock() throws KeeperException, InterruptedException;
}
