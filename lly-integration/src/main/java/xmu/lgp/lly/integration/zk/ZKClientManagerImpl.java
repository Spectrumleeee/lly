package xmu.lgp.lly.integration.zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.Transaction;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZKClientManagerImpl implements ZKManager {

    private static volatile ZooKeeper zk;
    private static volatile ZKConnection zkConnection;
    private String host;
    
    public ZKClientManagerImpl(String host) {
        this.host = host;
    }

    @Override
    public void create(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
    }

    @Override
    public Stat getZNodeStats(String path) throws KeeperException, InterruptedException {
        return zk.exists(path, false);
    }

    @Override
    public byte[] getZNodeData(String path, ZKWatcher watcher) throws KeeperException, InterruptedException {
        byte[] bytes = null;
        if (watcher != null) {
            bytes = zk.getData(path, watcher, null);
            watcher.await();
        } else {
            bytes = zk.getData(path, null, null);
        }
        return bytes;
    }

    @Override
    public void update(String path, byte[] data) throws KeeperException, InterruptedException {
        zk.setData(path, data, -1);
    }

    @Override
    public List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException {
        return zk.getChildren(path, false);
    }

    @Override
    public void delete(String path) throws KeeperException, InterruptedException {
        zk.delete(path, -1);
    }

    @Override
    public void initialize() throws IOException, InterruptedException, TimeoutException {
        zkConnection = new ZKConnection();
        zk = zkConnection.getZookeeper(host);
    }

    @Override
    public void closeConnection() throws InterruptedException {
        zkConnection.close();
    }

    @Override
    public ZKTransaction getTransaction() {
        return new ZKTransaction(zk.transaction());
    }
    
    public String getConnStr() {
        return host;
    }
    
    public void setConnStr(String host) {
        this.host = host;
    }
    
    public static class ZKTransaction {
        private Transaction transaction;
        
        public ZKTransaction(Transaction transaction) {
            this.transaction = transaction;
        }
        
        public void create(String path, byte[] data) {
            transaction.create(path, data, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
        
        public void delete(String path) {
            transaction.delete(path, -1);
        }
        
        public void update(String path, byte[] data) {
            transaction.setData(path, data, -1);
        }
        
        public void commit() throws InterruptedException, KeeperException {
            transaction.commit();
        }
        
    }
}
