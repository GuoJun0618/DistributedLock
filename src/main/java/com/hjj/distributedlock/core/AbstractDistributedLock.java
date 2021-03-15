package com.hjj.distributedlock.core;

/**
 * @author junguo
 * @date 2021-03-15
 * 分布式锁核心类,支持锁可重入
 */
public abstract class AbstractDistributedLock implements DistributedLock {
    /**
     * 锁名称前缀，默认"lock"，初始化时根据实际业务修改
     */
    protected String LOCK_PREFIX = "lock";
    /**
     * 锁状态，0未被枷锁，大于0表示加锁了n次
     */
    protected int state = 0;
    /**
     * 拥有锁的线程
     */
    protected Thread curThread = null;

    /**
     * 枷锁，会阻塞
     */
    public abstract void lock();

    /**
     * 尝试枷锁，非阻塞
     *
     * @return 枷锁成功与否
     */
    public abstract boolean tryLock();

    /**
     * 尝试一定时间段内枷锁，超时返回
     *
     * @param timeout 超时时间,毫秒
     * @return 枷锁成功与否
     */
    public abstract boolean tryLock(long timeout);

    /**
     * 释放锁
     *
     * @return 释放成功与否
     */
    public abstract boolean unlock();
}