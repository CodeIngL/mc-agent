package com.codeL.mc.agent.fetch.weaver.aspect;

import com.codeL.mc.agent.config.ConfigurationStateHolder;
import com.codeL.mc.agent.data.metrics.MetricsDelegate;
import com.codeL.mc.agent.data.metrics.MetricsKey;
import com.codeL.mc.agent.fetch.annotation.getter.AnnotationPropertiesGetter;
import com.codeL.mc.agent.fetch.helper.AspectHelper;
import com.codeL.mc.agent.util.MethodUtil;
import com.codahale.metrics.Timer;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_ASPECT_HELPER;
import static com.codeL.mc.agent.constant.McConfigConstants.CLASS_INTERNAL_METRICS_DELEGATE;
import static com.codeL.mc.agent.fetch.annotation.getter.AnnotationPropertiesGetter.*;

/**
 * <p>Function: [功能模块：监控有对应注解的方法内容的拦截织入基类]</p>
 * <p>Description: [功能描述：方法加上对应的注解之后,agent会使用aspectj原生aop对此方法进行相应的监控,监控数据放入对应的监控项里]</p>
 * <p>Company: </p>
 *
 * @author linj
 * @version 1.0
 *          2017/8/15
 */
@Aspect
public abstract class MonitorByAnnotationAspectWeaver {

    /**
     * 一秒包含的纳秒数
     */
    private static final int NANOSECONDS_TO_SECOND = 1000000000;

    /**
     * Timer拦截点
     */
    @Pointcut
    public abstract void timerPoint();

    /**
     * Meter拦截点
     */
    @Pointcut
    public abstract void meterPoint();

    /**
     * Counter拦截点
     */
    @Pointcut
    public abstract void counterPoint();

    /**
     * Histogram拦截点
     */
    @Pointcut
    public abstract void histogramPoint();

    /**
     * 监控MCTimer注解的方法
     * 此方法会记录被MCTimer注解的目标方法的执行时间 执行次数和QPS信息
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("timerPoint()")
    public Object monitorTimer(ProceedingJoinPoint pjp) throws Throwable {
        return monitorTimer(pjp, MCTIMER);
    }

    /**
     * 监控MCMeter注解的方法
     * 此方法会记录被执行次数
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("meterPoint()")
    public Object monitorMeter(ProceedingJoinPoint pjp) throws Throwable {
        return monitorMeter(pjp, MCMETER);
    }

    /**
     * 监控MCCounter注解的方法
     * 在进入被注解的方法时,Counter会增加,在被注解的方法执行完毕之后,Counter会减少.
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("counterPoint()")
    public Object monitorCounter(ProceedingJoinPoint pjp) throws Throwable {
        return monitorCounter(pjp, MCCOUNTER);
    }

    /**
     * 监控MCHistogram注解的方法
     * 此方法会记录方法执行耗时
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("histogramPoint()")
    public java.lang.Object monitorHistogram(ProceedingJoinPoint pjp) throws Throwable {
        return monitorHistogram(pjp, MCHISTOGRAM);
    }

    /**
     * 监控MCHistogram注解的方法
     * 此方法会记录方法执行耗时
     *
     * @param pjp 切片
     * @return Object
     * @throws Throwable 业务方法本身的异常
     */
    @Around("gaugePoint()")
    public Object monitorGauge(ProceedingJoinPoint pjp) throws Throwable {
        return monitorGauge(pjp, MCGAUGE);
    }

    /**
     * timer类型的监控
     *
     * @param pjp
     * @param getter
     * @throws Throwable
     */
    public static Object monitorTimer(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws Throwable {
        MetricsKey metricsKey = fetchMetricsKey(pjp, getter);
        if (metricsKey == null) {
            return pjp.proceed();
        }
        MetricsDelegate delegate = fetchMetricsDelegate();
        Timer.Context context = delegate.getTimerContext(metricsKey.getKey());
        Timer.Context dynamicContext = delegate.getTimerContext(metricsKey.getDynamicKey());
        try {
            return pjp.proceed();
        } finally {
            delegate.stopTimeContext(context);
            delegate.stopTimeContext(dynamicContext);
        }
    }

    /**
     * meter类型的监控
     *
     * @param pjp
     * @param getter
     * @throws Throwable
     */
    public static Object monitorMeter(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws Throwable {
        MetricsKey metricsKey = fetchMetricsKey(pjp, getter);
        if (metricsKey == null) {
            return pjp.proceed();
        }
        MetricsDelegate delegate = fetchMetricsDelegate();
        try {
            return pjp.proceed();
        } finally {
            delegate.markMeter(metricsKey.getKey());
            delegate.markMeter(metricsKey.getDynamicKey());
        }
    }

    /**
     * counter类型的监控
     *
     * @param pjp
     * @param getter
     * @throws Throwable
     */
    public static Object monitorCounter(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws Throwable {
        MetricsKey metricsKey = fetchMetricsKey(pjp, getter);
        if (metricsKey == null) {
            return pjp.proceed();
        }
        MetricsDelegate delegate = fetchMetricsDelegate();
        delegate.incCounter(metricsKey.getKey());
        delegate.incCounter(metricsKey.getDynamicKey());
        try {
            return pjp.proceed();
        } finally {
            delegate.decCounter(metricsKey.getKey());
            delegate.decCounter(metricsKey.getDynamicKey());
        }
    }

    /**
     * @param pjp
     * @param getter
     * @throws Throwable
     */
    public static Object monitorGauge(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws Throwable {
        MetricsKey metricsKey = fetchMetricsKey(pjp, getter);
        if (metricsKey == null) {
            return pjp.proceed();
        }
        MetricsDelegate delegate = fetchMetricsDelegate();
        delegate.registerGauge(metricsKey.getKey(), MethodUtil.getSignatureMethod(pjp));
        return pjp.proceed();
    }

    /**
     * histogram类型的监控
     *
     * @param pjp
     * @param getter
     * @throws Throwable
     */
    public static Object monitorHistogram(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) throws Throwable {
        MetricsKey metricsKey = fetchMetricsKey(pjp, getter);
        if (metricsKey == null) {
            return pjp.proceed();
        }
        MetricsDelegate delegate = fetchMetricsDelegate();
        long start = System.nanoTime();
        try {
            return pjp.proceed();
        } finally {
            long end = System.nanoTime();
            long executeCostMs = (end - start) / NANOSECONDS_TO_SECOND;
            delegate.updateHistogram(metricsKey.getKey(), executeCostMs);
            delegate.updateHistogram(metricsKey.getDynamicKey(), executeCostMs);
        }
    }

    /**
     * 尝试获得一个metricsKey
     *
     * @param pjp
     * @param getter
     * @return
     */
    private static MetricsKey fetchMetricsKey(ProceedingJoinPoint pjp, AnnotationPropertiesGetter getter) {
        try {
            ConfigurationStateHolder stateHolder = ConfigurationStateHolder.getInstance();
            AspectHelper helper = (AspectHelper) stateHolder.getBean(CLASS_INTERNAL_ASPECT_HELPER);
            if (helper == null){
                return null;
            }
            return helper.fetchMetricsKeys(pjp, getter);
        } catch (Throwable throwable) {
            //ignore
        }
        return null;
    }

    /**
     * 获得相关MetricsDelegate
     *
     * @return
     */
    private static MetricsDelegate fetchMetricsDelegate() {
        return (MetricsDelegate) ConfigurationStateHolder
                .getInstance()
                .getBean(CLASS_INTERNAL_METRICS_DELEGATE);
    }

}
