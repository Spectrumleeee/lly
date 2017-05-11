package xmu.lgp.lly.integration.zk;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

public class ZKConnection {
    
    private ZooKeeper zoo;
    
    public ZooKeeper getZookeeper(String connectUrl) throws IOException, InterruptedException, TimeoutException {
        final CountDownLatch connectionLatch = new CountDownLatch(1);
        zoo = new ZooKeeper(connectUrl, 2000, new Watcher(){
            @Override
            public void process(WatchedEvent event) {
                if (event.getState() == Watcher.Event.KeeperState.SyncConnected) {
                    connectionLatch.countDown();
                }
            }
        });
        if (!connectionLatch.await(20L, TimeUnit.SECONDS)) {
            zoo.close();
            throw new TimeoutException("等待zookeeper连接超时");
        }
        return zoo;
    }
    
    public void close() throws InterruptedException {
        zoo.close();
    }
    
}
