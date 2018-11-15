package com.codeL.mc.agent.config

import org.junit.Assert
import org.junit.Before
import org.junit.Test

/**
 cif
 *
 */
class McClientInitManagerTest {

    McClientInitManager manager;

    @Before
    void init() {
        manager = new McClientInitManager();
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
        Map conf = ["mcSystem.class.internalKeyCacheReporterStarter": "com.codeL.mc.agent.task.KeyCacheReportStarter",
                    "mcSystem.class.internalKeyCacheReporter"       : "com.codeL.mc.agent.task.reporter.KeyCacheReporter"];
        manager.registerClass(conf)
        int clsKvSize = manager.confManager.innerObject.size();
        println("all class kv count is " + clsKvSize);
        Assert.assertTrue("successful", (clsKvSize == 2));
        clsKvSize = manager.confManager.innerObject.size();
        Assert.assertTrue("successful", (clsKvSize == 2));
    }

}
