package xmu.lgp.lly.integration.zk;

import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.AsyncCallback.StatCallback;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.data.Stat;

public class ZKWatcher implements Watcher, StatCallback {

    CountDownLatch latch;
    
    public ZKWatcher() {
        latch = new CountDownLatch(1);
    }
    
    @Override
    public void process(WatchedEvent event) {
        System.out.println("Watcher fired on path: " + event.getPath() + " state: " + event.getState() + " type " + event.getType());
        latch.countDown();
    }

    @Override
    public void processResult(int arg0, String arg1, Object arg2, Stat arg3) { }
    
    public void await() throws InterruptedException {
        latch.await();
    }
    
    
}
