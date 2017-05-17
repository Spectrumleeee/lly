package xmu.lgp.lly.integration.dubbo.threadpool;

import java.util.concurrent.Executor;

import com.alibaba.dubbo.common.URL;
import com.alibaba.dubbo.common.threadpool.support.cached.CachedThreadPool;
import com.alibaba.dubbo.common.utils.ConfigUtils;

public class LlyCachedThreadPool extends CachedThreadPool {
    
    public Executor getExecutor(URL url) {
        int cores = url.getParameter("corethreads", -1);
        if (cores <= 0) {
            cores = Integer.parseInt(ConfigUtils.getProperty("lly.threadpool.corethreads", "10"));
            url = url.addParameter("corethreads", cores);
        }
        
        int alive = url.getParameter("alive", -1);
        if (alive <= 0) {
            alive = Integer.parseInt(ConfigUtils.getProperty("lly.threadpool.alive", "600000"));
            url = url.addParameter("alive", alive);
        }
        
        String threadname = url.getParameter("threadname");
        if (threadname == null) {
            url = url.addParameter("threadname", "Dubbo-LlyCache");
        }
        
        return super.getExecutor(url);
    }
}
