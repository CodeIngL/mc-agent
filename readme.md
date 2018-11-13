## 简介

opmc-agent是运营平台监控中心的客户端插件，配置简单。

功能如下

1. **信息统计**
	- 耗时
	- 调用次数
	- 分布图区间
2. **异常抓取**
	- spring下@ExceptionHandler异常抓取
	- log异常抓取
	- dubbo异常抓取
3. **心跳监测**
	- 应用存活检测
4. **GC(FullGC)探测**
    - 应用GC探测

### 里程功能

1.1-RELEASE中注解RequestMapping等价于注解MCTimer

    - Spring的properties的键值对opmc.requestMappingEnabled=true为开启等价的开关,默认为关闭。
    - 我们希望能够手动的在你想要监控的方法上添加@MCTimer注解,而不是一键开启。

1.2-RELEASE支持spring更加简化的配置

    - 支持Schema,Spring自定命名空间

1.3-RELEASE增加了异常开关,默认所有的异常进行抓取

    - Configuration类的catchAll=true抓取所有异常，和exceptionExcludes搭配。所用异常中将排除exceptionExcludes指定的异常
    - Configuration类的catchAll=false不抓取所有异常，和exceptionIncludes搭配。仅抓取exceptionIncludes指定的异常

1.4-RELEASE修复了对logback的支持

    - 支持logback日志框架

1.5-RELEASE引入了FULLGC的支持

1.7-RELEASE为最新的版本

# 快速开始

## 第一步:选择合适依赖

pom中添加:

        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>cn.com.servyou</groupId>
                    <artifactId>opmc-agent-bom</artifactId>
                    <version>1.7-RELEASE</version>
                    <scope>import</scope>
                    <type>pom</type>
                </dependency>
            </dependencies>
        </dependencyManagement>

dependencies中添加:

        <!--spring框架支持-->
        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-spring</artifactId>
        </dependency>
        <!--jvm异常告警(可选)-->
        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-jvm</artifactId>
        </dependency>
        <!--支持dubbo异常抓取功能(可选)-->
        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-dubbo</artifactId>
        </dependency>


## 第二步:spring集成

1. 配置bean.xml

		<?xml version="1.0" encoding="UTF-8"?>
        <beans xmlns="http://www.springframework.org/schema/beans"
               xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
               xmlns:opmc="http://www.servyou.cn/schema/opmc"
               xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
              http://www.servyou.cn/schema/opmc http://www.servyou.cn/schema/opmc/opmc.xsd">

            <bean id="opmcConfiguration" class="cn.com.servyou.yypt.opmc.agent.config.Configuration">
                <property name="enable" value="true"/>
                <!--opmc生产地址为: http://opmc.dc.servyou-it.com:8001/opmc-web  -->
                <property name="serverUrl" value="http://opmc.sit.91lyd.com/opmc-web"/>
                <!--可选参数:应用名-->
                <property name="appName" value="${xxxx}"/>
                <property name="catchAll" value="true"/>
                <!--可选参数:程序全部抓取模式下,用户指定需要排除的异常,ie:BusinessException-->
                <property name="exceptionExcludes" value="BusinessException"/>
                <!--可选参数:程序全部不抓取模式下,用户指定需要抓取的异常,ie:BusinessException-->
                <property name="exceptionIncludes" value="BusinessException"/>
            </bean>
            <opmc:driven/>
            <bean class="cn.com.servyou.yypt.opmc.agent.spring.GcRepoterFactoryBean">
                <property name="url" value="http://opmc.dc.servyou-it.com:8001/opmc-web/gc/warn"/>
            </bean>
        </beans>
        
## 第三步:非spring集成(可选)

如果你的应用除了spring之外还有其他方式,你可以通过非spring支持来进行对应用的支持

1. 类资源路径下创建aop.xml,模板如下:

        <?xml version="1.0" encoding="UTF-8"?>
		<aspectj>
		    <!--监控非spring框架下的统计项指标-->
			<aspects>
		        <!-- 监控器配置,这里无需改动 -->
		        <concrete-aspect name="OpmcMonitor" extends="cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect.MonitorByAnnotationAspectWeaver">
		            <pointcut name="timerPoint" expression="@annotation(cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCTimer)" />
		            <pointcut name="meterPoint" expression="@annotation(cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCMeter)" />
		            <pointcut name="counterPoint" expression="@annotation(cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCCounter)" />
		            <pointcut name="histogramPoint" expression="@annotation(cn.com.servyou.yypt.opmc.agent.fetch.annotation.define.MCHistogram)" />
		        </concrete-aspect>

			</aspects>
		    <!--需要监测的包,可以多个include元素来指定多个包.使用时,请替代为实际项目里的包名-->
		    <!--<weaver options="-XnoInline -Xlint:-cantFindType"> 这种写法可以忽略掉报错信息-->
			<weaver options="-verbose">
		        <!--该写法下,监控器将会监控cn.com.servyou.${yourpackage}这个包下的所有类,不包括子包-->
				<include within="cn.com.servyou.${yourpackage}.*" />
		        <!--该写法下,监控器将会监控cn.com.servyou.${yourpackage}这个包以及子包下的所有类-->
				<include within="cn.com.servyou.${yourpackage}..*" />
				<exclude within="cn.com..*CGLIB*" />
			</weaver>
		</aspectj>



## 第三步:配置VM参数（本地时不需要）

**配置VM参数(针对tomcat)**:

下载[findAgent.sh](http://192.168.2.107/laihj/findagent/blob/master/findAgent.sh)到tomcat的bin目录下


然后在catalina.sh的位置

        # ----- Execute The Requested Command -----------------------------------------

添加以下代码

    . "$CATALINA_HOME"/bin/findAgent.sh "$CATALINA_BASE"/conf/server.xml "$CATALINA_HOME"/webapps
    JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=${本机ip}"

**请把本机ip替换成你的机器IP**

**配置VM参数（针对webLogic）**

下载[findAgentWebLogic.sh](http://192.168.2.107/laihj/findagent/blob/master/findAgentWebLogic.sh)到domain的bin下

然后在startWebLogic.sh的位置

        # START WEBLOGIC

添加以下代码

    . "$DOMAIN_HOME"/bin/findAgentWebLogic.sh "$DOMAIN_HOME"/config/config.xml
    JAVA_OPTIONS="$JAVA_OPTS -Djava.rmi.server.hostname=${本机ip}"
    
-----


### 信息统计介绍 ###

OPMC使用Metrics作为统计插件，系统中使用对应的5种注解来对应Metrics的度量类型。

Metrics用一个键来代表监控项(统计项)，其是形式如:**xxx.xxx.xxx.xxx**

OPMC中有两种键，一种是**静态**的，一种是**动态**（请忽略）的，都是通过`@MCXXXX`注解实现的。

**静态键**

`@MCXXXX`的value来构建键，如果value不存在则使用方法名来构建。 例如:

	@MCTimer("value1")
	void show(){...}

静态键为**xxx.xxx.value1**

	@MCTimer
	void show(){...}

静态键为**xxx.xxx.show**

#### @MCCounter

计数器，用于统计方法的当前请求数。

推荐作用于接口，可统计接口当前的请求数。

#### @MCTimer

是MCMeter和MCHistogram的组合，用于统计被注解方法的执行耗时和TPS。

推荐作用于http接口和dubbo接口。或是其他有需要统计此类型数据的方法。

*注1：目前还没有总的统计项的相关功能和配置。*



---

### 常见问题说明 ###

* 无法抓取Controller层的异常

请确认是否有标注了`@ControllerAdvice`注解的统一异常处理器，如没有，请参照源码test包下`cn.com.servyou.yypt.opmc.agent.exceptionadvice.HandlerAdvice`类。

* 非spring框架下，统计项没有生效

检查用于非spring配置的`aop.xml`文件里的`<weaver options="-verbose">`选项，看它的`include`元素是否包含了需要统计的类所在包

这段配置的id是否和其他bean有重名。

* 出现无法代理final类的错误

这个大部分是因为aop代理重复了，系统中已经有aop配置，然后又增加了`<aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>`配置，便造成了重复代理。cglib去代理原类生成的代理类了，而代理类是final的，便会报错误。

这种情况下，可以将`proxy-target-class="true"`去除。

