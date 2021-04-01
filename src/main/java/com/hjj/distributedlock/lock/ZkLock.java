package com.hjj.distributedlock.lock;

import com.hjj.distributedlock.core.AbstractDistributedLock;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
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
    private static final String LOCK_PATH = "/easyLock";
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
                    List<String> children = zooKeeper.getChildren(LOCK_PATH + "/" + lockName, true);
                    LOG.info("监听----" + children.toString() + "---" + curLockPath);
                    validNode(children);
                } catch (KeeperException | InterruptedException e) {
                    LOG.error("zookeeper监听出错", e);
                }
            });
            while (!zooKeeper.getState().isConnected()) {
                LOG.info("连接中...");
            }
            Stat exists = zooKeeper.exists(LOCK_PATH, false);
            if (exists == null) {
                zooKeeper.create(LOCK_PATH, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                zooKeeper.create(LOCK_PATH + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
            }

        } catch (IOException e) {
            LOG.error("zookeeper初始化出錯", e);
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }

    public static ZkLock create(String zkUrl) {
        return create(null, zkUrl);
    }

    @Override
    public void lock() throws KeeperException, InterruptedException {
        if (curLockPath == null || curLockPath.length() == 0) {
            curLockPath = zooKeeper.create(LOCK_PATH + "/" + lockName + "/" + lockName, new byte[0], ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        }
        for (; ; ) {
            if (getLock) {
                LOG.info("拿到锁{}", curLockPath);
                break;
            }
        }
    }

    private void validNode(List<String> children) {
        if (curLockPath == null || curLockPath.length() == 0) {
            return;
        }
        List<String> list = children.stream().sorted().collect(Collectors.toList());
        if (list.size() == 0) {
            return;
        }
        if (curLockPath.endsWith(list.get(0))) {
            getLock = true;
        }
    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long timeout) {
        return false;
    }

    @Override
    public void unlock() throws KeeperException, InterruptedException {
        zooKeeper.delete(curLockPath, -1);
        LOG.info("删除锁{}", curLockPath);
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
