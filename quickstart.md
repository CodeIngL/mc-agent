## 简介

yypt-opmc-agent是运营平台监控中心的客户端插件，通过包导入之后进行简单的配置，在需要监控的方法上增加对应的注解即可实现业务系统的具体监控。

当前具有基本信息统计（基于Metrics）、心跳以及异常抓取功能，其中异常抓取包括Controller层的异常抓取、Dubbo客户端、服务端的异常抓取以及日志输出的异常抓取。


相比旧版的的配置，我们大大简化了配项


    <?xml version="1.0" encoding="UTF-8"?>
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:metrics="http://www.ryantenney.com/schema/metrics"
           xmlns:context="http://www.springframework.org/schema/context"
           xmlns:aop="http://www.springframework.org/schema/aop" xmlns:mvc="http://www.springframework.org/schema/mvc"
           xsi:schemaLocation="http://www.springframework.org/schema/beans
               http://www.springframework.org/schema/beans/spring-beans.xsd
               http://www.ryantenney.com/schema/metrics
               http://www.ryantenney.com/schema/metrics/metrics.xsd http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context.xsd http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd http://www.springframework.org/schema/mvc http://www.springframework.org/schema/mvc/spring-mvc.xsd">
        <!--如果已有spring的aop配置,去掉或者注释此配置项,此项详细配置和常见问题可查看使用说明-->
        <aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>
        <!--如果已有此配置,去掉或者注释此配置项,此项详细配置和常见问题可查看使用说明-->
        <mvc:annotation-driven/>
        <!--包扫描配置，不要更改-->
        <context:component-scan base-package="cn.com.servyou.yypt.opmc"/>
        <!--OPMC配置项 bean的id和class禁止修改-->
        <bean id="opmcConfiguration" class="cn.com.servyou.yypt.opmc.agent.config.Configuration">
            <!--必须参数:插件是否启用,true启用,false为不启用-->
            <property name="enabled" value="true"/>
            <!--必须参数:opmc服务端的地址,示例为测试地址-->
            <!--opmc生产地址为: http://opmc.dc.servyou-it.com:8001/opmc-web -->
            <property name="serverUrl" value="http://opmc-develop.sit.91lyd.com/opmc-web"></property>
            <!--可选参数:应用名,更改为实际应用名-->
            <property name="appName" value="${appName}"></property>
            <!--可选参数:需要监测的异常,该配置项优先级为高,以SimpleName作为判断条件,例:DataException-->
            <property name="exceptionInclude" value=""></property>
            <!--可选参数:不需要监测的异常,该配置项优先级为低,以SimpleName作为判断条件,例:DataException-->
            <property name="exceptionExclude" value=""></property>
        </bean>

        <!--Metrics数据统计插件配置项 此处禁止修改-->
        <metrics:metric-registry id="metricRegistry" />
        <!--Metrics健康检查项 此处禁止修改-->
        <metrics:health-check-registry id="health" />
        <!--Metrics注解配置 此处禁止修改-->
        <metrics:annotation-driven metric-registry="metricRegistry" />
        <!--Metrics报告类 不可与其他在spring中声明的bean重名-->
        <metrics:reporter type="jmx" id="metricJmxReporter" metric-registry="metricRegistry" />

    </beans>