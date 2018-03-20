package cn.com.servyou.yypt.opmc.agent.data.metrics;

import cn.com.servyou.opmc.agent.conf.context.ApplicationEvent;
import cn.com.servyou.opmc.agent.conf.context.ApplicationListener;
import cn.com.servyou.yypt.opmc.agent.event.AvailableEvent;
import cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.*;
import cn.com.servyou.yypt.opmc.agent.log.Log;
import cn.com.servyou.yypt.opmc.agent.log.LogFactory;
import cn.com.servyou.yypt.opmc.agent.util.MethodUtil;
import com.codahale.metrics.*;

import java.lang.reflect.Method;

import static cn.com.servyou.yypt.opmc.agent.constant.Constants.METRICS_DYNAMIC_KEY;

/**
 * <p>Function: [功能模块：Metrics工具类]</p>
 * <p>Description: [功能描述：进行metrics监控项键值的初始化和获取、计数等操作]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 *          17/11/10
 */
public class MetricsDelegate implements ApplicationListener {

    private static final Log LOGGER = LogFactory.getLog(MetricsDelegate.class);

    /**
     * 命名空间
     */
    private static final String MBEAN_NAMESPACE = "servyou";

    /**
     * 注册表
     */
    private MetricRegistry registry = new MetricRegistry();

    /**
     * 注册表
     */
    private MetricsKeyRegistry keyRegistry = new MetricsKeyRegistry();

    /**
     * 进行监控项的注册，监控项名字不得重复
     *
     * @param name   监控项名
     * @param metric metric实例
     * @param <T>    metric类型
     * @return null 不进行返回
     */
    private <T extends Metric> T register(String name, T metric) {
        try {
            return registry.register(MetricRegistry.name(MBEAN_NAMESPACE, name), metric);
        } catch (IllegalArgumentException e) {
            LOGGER.warn("", e);
            return null;
        }
    }

    /**
     * @param keyType 键类型
     */
    public void registerKeyType(String keyType) {
        keyRegistry.putKeyType(keyType);
    }

    /**
     * 注册gauge监控项
     *
     * @param key    监控项名
     * @param method 被@MCGauge注解的方法,推荐使用静态的无入参方法.
     * @throws Exception
     */
    public void registerGauge(final String key, final Method method) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ready to bind method to MCGauge with key :[" + key + "] , and method :[" + method.getName() + "]");
        }
        if (!keyRegistry.putKeyToCacheIfNotExist(MCGauge.class.getSimpleName(), key)) {
            return;
        }
        //注册gauge监控项
        register(key, new Gauge() {
            @Override
            public Object getValue() {
                try {
                    //判断method是否为静态,静态方法和普通方法的激活方法不同
                    if (MethodUtil.isStaticMethod(method)) {
                        //如果是静态方法,则是直接激活调用,入参为空即可.
                        return method.invoke(null);
                    } else {
                        //如果是非静态方法(不推荐),将会初始化一个method的父类的新实例,进行调用.
                        return method.invoke(method.getDeclaringClass().newInstance());
                    }
                } catch (Exception e) {
                    LOGGER.warn("Binding method to MCGauge failed on key :[" + key + "]", e);
                }
                return null;
            }
        });
    }

    /**
     * 注册gauge监控项
     *
     * @param key   监控项名
     * @param gauge 已经初始化好的gauge
     */
    public void registerGauge(final String key, Gauge gauge) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ready to set MCGauge with key :" + key + ".");
        }
        register(key, gauge);
    }

    /**
     * 标记Meter
     *
     * @param key 监控项名
     */
    public void markMeter(String key) {
        try {
            if (keyRegistry.putKeyToCacheIfNotExist(MCMeter.class.getSimpleName(), key)) {
                registry.meter(MetricRegistry.name(MBEAN_NAMESPACE, key)).mark();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on mark a Meter", e);
        }
    }

    /**
     * 获取Timer的监控实例,并且开始统计
     *
     * @param key 监控项名
     * @return Context
     */
    public Timer.Context getTimerContext(String key) {
        try {
            if (keyRegistry.putKeyToCacheIfNotExist(MCTimer.class.getSimpleName(), key)) {
                return registry.timer(MBEAN_NAMESPACE + key).time();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on time a Timer", e);
        }
        return null;
    }

    /**
     * 停止
     *
     * @param context
     */
    public void stopTimeContext(Timer.Context context) {
        try {
            if (context != null) {
                context.stop();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on stop a context", e);
        }
    }

    /**
     * 更新Histogram值
     *
     * @param key   监控项名
     * @param value 监控项值
     */
    public void updateHistogram(String key, long value) {
        try {
            if (keyRegistry.putKeyToCacheIfNotExist(MCHistogram.class.getSimpleName(), key)) {
                registry.histogram(MBEAN_NAMESPACE + key, new MetricRegistry.MetricSupplier<Histogram>() {
                    @Override
                    public Histogram newMetric() {
                        return new Histogram(new ExponentiallyDecayingReservoir());
                    }
                }).update(value);
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on update a Histogram", e);
        }
    }

    /**
     * Counter增加
     *
     * @param key 监控项名
     */
    public void incCounter(String key) {
        try {
            if (keyRegistry.putKeyToCacheIfNotExist(MCCounter.class.getSimpleName(), key)) {
                registry.counter(MBEAN_NAMESPACE + key).inc();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on inc a Counter", e);
        }
    }

    /**
     * Counter减少
     *
     * @param key 监控项名
     */
    public void decCounter(String key) {
        try {
            if (keyRegistry.putKeyToCacheIfNotExist(MCCounter.class.getSimpleName(), key)) {
                registry.counter(MBEAN_NAMESPACE + key).dec();
            }
        } catch (Exception e) {
            LOGGER.warn("Exception occurred on dec a Counter", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        if (event instanceof AvailableEvent) {
            registerGauge(METRICS_DYNAMIC_KEY, new Gauge() {
                @Override
                public Object getValue() {
                    return keyRegistry.getDynamicKeyJson();
                }
            });
            registerKeyType(MCTimer.class.getSimpleName());
            registerKeyType(MCCounter.class.getSimpleName());
            registerKeyType(MCMeter.class.getSimpleName());
            registerKeyType(MCHistogram.class.getSimpleName());
        }
    }

    public MetricRegistry getRegistry() {
        return registry;
    }

    public void setRegistry(MetricRegistry registry) {
        this.registry = registry;
    }
}
