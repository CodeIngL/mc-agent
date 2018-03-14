package cn.com.servyou.yypt.opmc.agent.config;

import cn.com.servyou.yypt.opmc.agent.config.OpmcClientInitManager;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @version 
 * @date 2018/2/7
 * @see
 * @since 
 */
public class OpmcClientInitManagerTest {

    OpmcClientInitManager manager;

    @Before
    public void init(){
        manager = new OpmcClientInitManager();
    }

    @Test
    public void testStart(){
        manager.start();
        System.out.println(manager.toString());
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
