package cn.com.servyou.yypt.opmc.agent.config

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 cif
 *
 */
class OpmcClientInitManagerTest {

    OpmcClientInitManager manager;

    @Before
    void init() {
        manager = new OpmcClientInitManager();
    }

    @Test
    void testStart() {
        manager.start();
        int kvSize = manager.confManager.innerConf.size();
        int clsKvSize = manager.confManager.innerObject.size();
        println("all kv count is " + kvSize);
        println("all class kv count is " + clsKvSize);
        Assert.assertTrue("successful", (kvSize == 23) && (clsKvSize == 15))
    }

    @Test
    void testRegisterClass() {
        Map conf = ["opmcSystem.class.internalKeyCacheReporterStarter": "cn.com.servyou.yypt.opmc.agent.task.KeyCacheReportStarter",
                    "opmcSystem.class.internalKeyCacheReporter"       : "cn.com.servyou.yypt.opmc.agent.task.reporter.KeyCacheReporter"];
        manager.registerClass(conf)
        int clsKvSize = manager.confManager.innerObject.size();
        println("all class kv count is " + clsKvSize);
        Assert.assertTrue("successful", (clsKvSize == 2));
        clsKvSize = manager.confManager.innerObject.size();
        Assert.assertTrue("successful", (clsKvSize == 2));
    }

}
