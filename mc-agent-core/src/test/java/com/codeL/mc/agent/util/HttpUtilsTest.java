package com.codeL.mc.agent.util;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 * 2019/1/11
 */
public class HttpUtilsTest {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtilsTest.class);

    @Test
    public void get() throws InterruptedException {
        int i = 0;
        for (;;){
            try {
                HttpUtils.get("https://www.hao123.com","utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Thread.sleep(1000L);
            i++;
            if (i>10){
                break;
            }
        }
    }
}