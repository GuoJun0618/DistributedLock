# DistributedLock
分布式锁的实现

1.0 基于zookeeper实现分布式锁，Test.class里面有测试demo

设计思路：
    通过zookeeper新建临时有序的节点，拥有节点序号最小的客户端线程认为获取到锁。
