/**
 * Author:  liguangpu <spectrumleeee@gmail.com>
 * Created: 2017-4-4
 */

package lly.framework.core.bootstrap;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import lly.framework.core.util.PrintUtil;

import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Bootstrap {

    public static AbstractApplicationContext context;

    public static void main(String args[]) throws Exception {
        start();
        stop();
    }

    protected static void start() throws Exception {
        PrintUtil.printClassBootstrapMsg(Bootstrap.class, "开始启动");
        
        String confDirString = System.getProperty("conf.dir", "src/main/resources");
        File confDir = new File(confDirString);
        if (!confDir.exists()) {
            throw new RuntimeException("Conf directory " + confDir.getAbsolutePath() + " does not exist.");
        }
        ClassLoader loader = new URLClassLoader(new URL[] { confDir.toURI().toURL() });
        Thread.currentThread().setContextClassLoader(loader);
        context = new ClassPathXmlApplicationContext("applicationContext.xml");

        PrintUtil.printClassBootstrapMsg(Bootstrap.class, "启动成功");
    }

    protected static void stop(Integer... sleepTimes) throws Exception {
        int times = sleepTimes.length != 0 ? sleepTimes[0].intValue() : 1;
        for (int i = 0; i < times; i++)
            Thread.sleep(3000L);

        context.close();
        
        PrintUtil.printClassBootstrapMsg(Bootstrap.class, "关闭成功");
    }

}
