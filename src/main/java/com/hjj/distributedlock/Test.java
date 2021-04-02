package com.hjj.distributedlock;

import com.hjj.distributedlock.lock.ZkLock;
import org.apache.zookeeper.KeeperException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author junguo
 */
public class Test {

    /**
     * 在D盘新建一个内容为0的文件代替数据库做测试
     */
    private final static String FILE_PATH = "d:\\1.txt";
    private final static String ZK_URL = "192.168.247.130:2181";

    public static void main(String[] args) {
        Test test = new Test();
        test.test();
    }

    private void test() {

        Thread thread1 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    ZkLock zkLock = ZkLock.create("demo", ZK_URL);
                    zkLock.lock();
                    addFile();
                    zkLock.unlock();
                }
            } catch (KeeperException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                for (int i = 0; i < 100; i++) {
                    ZkLock zkLock = ZkLock.create("demo", ZK_URL);
                    zkLock.lock();
                    addFile();
                    zkLock.unlock();
                }
            } catch (KeeperException | InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        thread1.start();
        thread2.start();
    }

    private void addFile() throws IOException {
        FileReader fr = new FileReader(FILE_PATH);
        BufferedReader br = new BufferedReader(fr);
        String s = br.readLine();
        br.close();
        FileWriter fw = new FileWriter(FILE_PATH);
        int res = Integer.parseInt(s) + 1;
        fw.write(res + "");
        fw.close();

    }
}
