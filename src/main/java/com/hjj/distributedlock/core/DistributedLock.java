package com.hjj.distributedlock.core;

/**
 * @author junguo
 * @date 2021-03-15
 * 分布式锁核心接口
 */
public interface DistributedLock {

    /**
     * 枷锁，会阻塞
     */
    void lock();

    /**
     * 尝试枷锁，非阻塞
     *
     * @return 枷锁成功与否
     */
    boolean tryLock();

    /**
     * 尝试一定时间段内枷锁，超时返回
     *
     * @param timeout 超时时间,毫秒
     * @return 枷锁成功与否
     */
    boolean tryLock(long timeout);

    /**
     * 释放锁
     *
     * @return 释放成功与否
     */
    boolean unlock();
}
