package xmu.lgp.lly.common.support;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import xmu.lgp.lly.common.context.ContextSlf4jUtil;
import xmu.lgp.lly.common.context.ServiceContext;

@ContextConfiguration(locations={"classpath:application.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class SprintTestCase extends AbstractJUnit4SpringContextTests {
    
    protected Logger logger = LoggerFactory.getLogger(getClass());
    
    @BeforeClass
    public static void beforeClass() {
        ContextSlf4jUtil.addLogKey2MDC(ServiceContext.getContext("UnitTest"));
    }
    
    @AfterClass
    public static void afterClass() {}
}
