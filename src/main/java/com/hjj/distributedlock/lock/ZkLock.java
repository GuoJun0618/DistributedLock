package com.hjj.distributedlock.lock;

import com.hjj.distributedlock.core.AbstractDistributedLock;
import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author junguo
 */
public class ZkLock extends AbstractDistributedLock {
    private ZooKeeper zooKeeper;
    private static final Logger LOG = LoggerFactory.getLogger(ZkLock.class);
    private static final String LOCK_ROOT = "/easyLock";
    private String curLockPath;
    private volatile boolean getLock = false;

    private ZkLock() {
    }

    /**
     * 创建ZkLock锁对象，lockName为空时使用默认名称为前缀
     *
     * @param lockName 名称
     * @return 锁对象
     */
    public static ZkLock create(String lockName, String zkUrl) {
        ZkLock zkLock = new ZkLock();
        if (lockName != null && lockName.length() > 0) {
            zkLock.lockName = lockName;
        }
        zkLock.init(zkUrl);
        return zkLock;
    }

    private void init(String zkUrl) {
        try {
            zooKeeper = new ZooKeeper(zkUrl, 5000, watchedEvent -> {
                try {
                    if (zooKeeper.getState().isAlive() &&
                            watchedEvent.getType() == Watcher.Event.EventType.NodeChildrenChanged) {
                        findMinNode();
                    }
                } catch (KeeperException | InterruptedException e) {
                    LOG.error("zookeeper监听出错", e);
                }
            });
            initNode();
        } catch (IOException e) {
            LOG.error("zookeeper初始化出錯", e);
        }
    }

    private void initNode() {
        try {
            Stat existsRoot = zooKeeper.exists(LOCK_ROOT, false);
            if (existsRoot == null) {
                zooKeeper.create(LOCK_ROOT, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
            Stat existsCur = zooKeeper.exists(LOCK_ROOT + "/" + lockName, false);
            if (existsCur == null) {
                zooKeeper.create(LOCK_ROOT + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }
        } catch (KeeperException.NodeExistsException e1) {
            LOG.info("initNode-已经创建过该节点");
        } catch (KeeperException | InterruptedException e) {
            LOG.error("initNode-error", e);
        }
    }

    public static ZkLock create(String zkUrl) {
        return create(null, zkUrl);
    }

    @Override
    public void lock() throws KeeperException, InterruptedException {
        if (getLock && curThread == Thread.currentThread()) {
            return;
        }
        curLockPath = zooKeeper.create(LOCK_ROOT + "/" + lockName + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        findMinNode();
        for (; ; ) {
            if (getLock) {
                LOG.debug("拿到锁{}", curLockPath);
                break;
            }
        }
    }

    private void findMinNode() throws KeeperException, InterruptedException {
        List<String> children = zooKeeper.getChildren(LOCK_ROOT + "/" + lockName, true);
        List<String> list = children.stream().sorted().collect(Collectors.toList());
        if (list.size() == 0) {
            return;
        }
        if (curLockPath != null && curLockPath.endsWith(list.get(0))) {
            getLock = true;
            curThread = Thread.currentThread();
        }
    }

    @Override
    public boolean tryLock() {
        if (getLock && curThread != Thread.currentThread()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean tryLock(long timeout) {
        return false;
    }

    @Override
    public void unlock() throws KeeperException, InterruptedException {
        zooKeeper.delete(curLockPath, -1);
        LOG.debug("删除锁{}", curLockPath);
        curLockPath = null;
        getLock = false;
        zooKeeper.close();
    }

    @Override
    public String toString() {
        return "ZkLock{" +
                "zooKeeper=" + zooKeeper +
                ", curLockPath='" + curLockPath + '\'' +
                ", getLock=" + getLock +
                '}';
    }
}
