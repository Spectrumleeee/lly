package xmu.lgp.lly.integration.zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.data.Stat;

public interface ZKManager {
    
    public void create(String path, byte[] data) throws KeeperException, InterruptedException;
    
    public Stat getZNodeStats(String path) throws KeeperException, InterruptedException;
    
    public byte[] getZNodeData(String path, ZKWatcher watcher) throws KeeperException, InterruptedException;
    
    public void update(String path, byte[] data)  throws KeeperException, InterruptedException;
    
    public List<String> getZNodeChildren(String path) throws KeeperException, InterruptedException;
    
    public void delete(String path) throws KeeperException, InterruptedException;
    
    public void initialize() throws IOException, InterruptedException, TimeoutException;
    
    public void closeConnection() throws InterruptedException;
    
    public ZKClientManagerImpl.ZKTransaction getTransaction();
}
