package cn.com.servyou.yypt.opmc.agent.fetch.getter;

import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.DivideParamGetType;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCTimer;
import cn.com.servyou.yypt.opmc.agent.fetch.divide.DivideParamParser;
import cn.com.servyou.yypt.opmc.agent.fetch.divide.defaultparser.FirstInputParamParser;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.Scanner;

import static cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter.AnnotationPropertiesGetter.MCTIMER;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/5/4
 */
public class AnnotationPropertiesGetterTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        while (true) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int i = 0; i < Integer.parseInt(System.getProperty("count")); i++)
                        MCTIMER.getDivideConfig(new SimpleMCTimer());
                }
            }).start();
            Thread.sleep(3000L);
        }
    }

    static class SimpleMCTimer implements MCTimer{
        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @Override
        public String value() {
            return null;
        }

        @Override
        public boolean monitorByResult() {
            return false;
        }

        @Override
        public String divideParamName() {
            return null;
        }

        @Override
        public DivideParamGetType[] divideParamGetType() {
            return new DivideParamGetType[]{DivideParamGetType.URL};
        }

        @Override
        public Class<? extends DivideParamParser>[] divideParamParserClass() {
            return new Class[]{FirstInputParamParser.class};
        }
    }
}
