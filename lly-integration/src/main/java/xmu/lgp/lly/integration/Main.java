package xmu.lgp.lly.integration;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import xmu.lgp.lly.framework.spring.SpringContextHolder;
import xmu.lgp.lly.integration.dubbo.container.LlyLogbackContainer;
import xmu.lgp.lly.integration.dubbo.container.LlyReportStatusContainer;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.ExtensionLoader;
import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.container.Container;

/**
 * Hello world!
 * 
 */
public class Main {
    
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    
    private static final ExtensionLoader<Container> loader = ExtensionLoader.getExtensionLoader(Container.class);
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("[yyyy-MM-dd HH:mm:ss]");
    
    private static final List<Container> containers = new ArrayList<>();
    
    public static final String DUBBO_CONTAINER_KEY = "dubbo.container";
    
    static {
        LlyLogbackContainer.getInstance().start();
    }
    
    public static void main(String[] args) {
        logger.info("[" + sdf.format(new Date()) + "] xmu.lgp.lly.integration.Main start...");
        
        String reportFile = System.getProperty("lly.started.reportfile");
        LlyReportStatusContainer.getInstance().setReportFilePath(reportFile);
        
        init(args);
        
        logger.info("[" + sdf.format(new Date()) + "] xmu.lgp.lly.integration.Main stop...");
        LlyLogbackContainer.getInstance().stop();
    }
    
    private static void init(String[] args) {
        startDubbo(args);
        final CountDownLatch latch = new CountDownLatch(1);
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    logger.info("开始关闭SafeStopComponent");
                    safeStopComponent();
                    logger.info("所有SafeStopComponent关闭完成");
                    logger.info("开始关闭dubbo容器");
                    stopDubbo();
                    logger.info("关闭dubbo容器完成");
                    logger.info("开始调用ProtocolConfig.destroyAll方法");
                    ProtocolConfig.destroyAll();
                    logger.info("调用ProtocolConfig.destroyAll方法结束");
                } finally {
                    latch.countDown();
                }
            }
        });
        try {
            latch.await();
            logger.info("所有容器和组件关闭完成");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("等待优雅关闭被中断");
        }
    }
    
    private static void startDubbo(String[] args) {
        if (args == null || args.length == 0) {
            String config = ConfigUtils.getProperty("dubbo.container", loader.getDefaultExtensionName());
            args = Constants.COMMA_SPLIT_PATTERN.split(config);
        }
        
        for (int i=0; i < args.length; i++) {
            containers.add(loader.getExtension(args[i]));
        }
        logger.info("Use container type(" + Arrays.toString(args) + ") to run dubbo service.");
        
        for (Container container: containers) {
            container.start();
            logger.info("Dubbo " + container.getClass().getSimpleName() + " started!");
        }
        System.out.println(sdf.format(new Date()) + " Dubbo service server started!");
    }
    
    private static void stopDubbo() {
        for (Container container : containers) {
            try {
                container.stop();
                logger.info("Dubbo " + container.getClass().getSimpleName() + " stopped!");
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
    
    private static void safeStopComponent() {
        Map<Integer, List<SafeLifecycle>> orderMap = getOrderSafeLifecycleMap();
        ExecutorService executorService = Executors.newCachedThreadPool();
        while(!orderMap.isEmpty()) {
            int maxOrder = Integer.MIN_VALUE;
            for (Integer key : orderMap.keySet()) {
                if (key > maxOrder) {
                    maxOrder = key;
                }
            }
            
            final CountDownLatch latch = new CountDownLatch(orderMap.get(Integer.valueOf(maxOrder)).size());
            for (final SafeLifecycle safeLifecycle : orderMap.get(Integer.valueOf(maxOrder))) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            safeLifecycle.safelyStop();
                        } finally {
                            latch.countDown();
                        }
                    }
                });
            }
            try {
                latch.await();
                orderMap.remove(Integer.valueOf(maxOrder));
            } catch(InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.error("等待优雅关闭过程被中断!");
            }
        }
        logger.info("所有SafeStopComponent已经停止!");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60L, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("优雅关闭过程被中断!");
        }
    }
    
    private static Map<Integer, List<SafeLifecycle>> getOrderSafeLifecycleMap() {
        Map<String, SafeLifecycle> lifeCycles = SpringContextHolder.getApplicationContext().getBeansOfType(SafeLifecycle.class);
        
        Map<Integer, List<SafeLifecycle>> orderMap = new HashMap<>();
        for (Map.Entry<String, SafeLifecycle> entry: lifeCycles.entrySet()) {
            int order = getOrder((String)entry.getKey()).intValue();
            List<SafeLifecycle> list = (List<SafeLifecycle>)orderMap.get(Integer.valueOf(order));
            if (null == list) {
                list = new LinkedList<>();
                orderMap.put(Integer.valueOf(order), list);
            }
            list.add(entry.getValue());
        }
        return orderMap;
    }
    
    private static Integer getOrder(String beanName) {
        SafelyStopOrder order = (SafelyStopOrder)SpringContextHolder.getApplicationContext().findAnnotationOnBean(beanName, SafelyStopOrder.class);
        if (order != null) {
            return Integer.valueOf(order.order());
        }
        return Integer.valueOf(0);
    }
}
