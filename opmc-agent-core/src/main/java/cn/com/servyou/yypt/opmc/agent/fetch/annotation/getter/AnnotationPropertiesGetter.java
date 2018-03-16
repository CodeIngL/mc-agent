package cn.com.servyou.yypt.opmc.agent.fetch.annotation.getter;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCCounter;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCHistogram;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCMeter;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCGauge;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCTimer;

/**
 * <p>Function: [功能模块：注解属性的获取器]</p>
 * <p>Description: [功能描述：获取注解的对应属性.]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public interface AnnotationPropertiesGetter<T> {
    /**
     * 获取注解的value
     * 用于组装方法对应的监控项的key值
     *
     * @param t 注解实例
     * @return String
     */
    String getValue(T t);

    /**
     * 获取按参数明细统计的配置项
     * 用于组装明细统计的明细key
     * 配置项格式为DivideConfigInfo实例
     *
     * @param t 注解实例
     * @return DivideConfigInfo
     */
    DivideConfigInfo getDivideConfig(T t);

    /**
     * @return 返回注解类型
     */
    Class<T> getAnnotation();

    /**
     * MCTimer类型
     */
    AnnotationPropertiesGetter MCTIMER = new AnnotationPropertiesGetter<MCTimer>() {
        @Override
        public String getValue(MCTimer mcTimer) {
            return mcTimer.value();
        }

        @Override
        public DivideConfigInfo getDivideConfig(MCTimer mcTimer) {
            return new DivideConfigInfo(mcTimer.divideParamName(), mcTimer.divideParamGetType(), mcTimer.divideParamParserClass());
        }

        @Override
        public Class<MCTimer> getAnnotation() {
            return MCTimer.class;
        }
    };
    /**
     * MCMETER类型
     */
    AnnotationPropertiesGetter MCMETER = new AnnotationPropertiesGetter<MCMeter>() {
        @Override
        public String getValue(MCMeter mcMeter) {
            return mcMeter.value();
        }

        @Override
        public DivideConfigInfo getDivideConfig(MCMeter mcMeter) {
            return new DivideConfigInfo(mcMeter.divideParamName(), mcMeter.divideParamGetType(), mcMeter.divideParamParserClass());
        }

        @Override
        public Class<MCMeter> getAnnotation() {
            return MCMeter.class;
        }
    };
    /**
     * MCHISTOGRAM类型
     */
    AnnotationPropertiesGetter MCHISTOGRAM = new AnnotationPropertiesGetter<MCHistogram>() {
        @Override
        public String getValue(MCHistogram mcHistogram) {
            return mcHistogram.value();
        }

        @Override
        public DivideConfigInfo getDivideConfig(MCHistogram mcHistogram) {
            return new DivideConfigInfo(mcHistogram.divideParamName(), mcHistogram.divideParamGetType(), mcHistogram.divideParamParserClass());
        }

        @Override
        public Class<MCHistogram> getAnnotation() {
            return MCHistogram.class;
        }
    };
    /**
     * MCCOUNTER类型
     */
    AnnotationPropertiesGetter MCCOUNTER = new AnnotationPropertiesGetter<MCCounter>() {
        @Override
        public String getValue(MCCounter mcCounter) {
            return mcCounter.value();
        }

        @Override
        public DivideConfigInfo getDivideConfig(MCCounter mcCounter) {
            return new DivideConfigInfo(mcCounter.divideParamName(), mcCounter.divideParamGetType(), mcCounter.divideParamParserClass());
        }

        @Override
        public Class<MCCounter> getAnnotation() {
            return MCCounter.class;
        }
    };
    /**
     * MCGAUGE类型
     */
    AnnotationPropertiesGetter MCGAUGE = new AnnotationPropertiesGetter<MCGauge>() {
        @Override
        public String getValue(MCGauge mcGauge) {
            return mcGauge.value();
        }

        @Override
        public DivideConfigInfo getDivideConfig(MCGauge mcGauge) {
            return null;
        }

        @Override
        public Class<MCGauge> getAnnotation() {
            return MCGauge.class;
        }
    };
}
