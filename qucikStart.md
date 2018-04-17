## 简介

opmc-agent是运营平台监控中心的客户端插件，通过简单的配置，ie:在监控的方法上增加对应的注解即可实现业务系统的具体监控。

原版由**林菁**开发，新版由**laihj**开发，与之前旧版相比,具有以下特点

1. 模块化插件,不再依赖spring

2. 缓存切面操作，提高统计性能

3. 代码设计重构

关于细节,请参阅源码以及API

### opmc-agent功能特征 ###

opmc-agent主要有以下功能

1. 信息统计

2. 异常抓取
	- spring下@ExceptionHandler异常抓取
	- log异常抓取
	- dubbo异常抓取

3. 心跳监测

## 第一步:配置VM参数

**配置VM参数**:

		-javaagent:${aspectjweaver-1.8.9.jar}
		-Dorg.aspectj.weaver.loadtime.configuration=${aop.xml};INNER-INF/aop.xml
		-Dorg.aspectj.weaver.loadtime.configuration.lightxmlparser=true
		-Dcom.sun.management.jmxremote
		-Dcom.sun.management.jmxremote.port=18090
		-Dcom.sun.management.jmxremote.ssl=false
		-Dcom.sun.management.jmxremote.authenticate=false
		-Djava.rmi.server.hostname=10.200.100.3

**`${aspectjweaver-1.8.9.jar}`**
- 该jar的**路径**，以便JVM能够找到该jar。

**`${aop.xml}`**
- 项目中的**AOP配置文件**，以便Aspectj能够进行访问。

**`jmx`**
- 项目通过**jmx**暴露一些统计信息


## 第二步:选择合适依赖

**pom依赖**:

        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-log</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

上述模块提供**log异常**抓取功能.

        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-dubbo</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

上述模块提供**dubbo异常**抓取功能.

	
        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-spring</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>

上述模块提供spring环境下的其他功能.


选择上面的组合实现的你的需求


## 第三步:集成

#### spring集成 ####


**bean配置**

	<!--已有此配置,忽略此项-->
	<aop:aspectj-autoproxy proxy-target-class="true"/>
	<!--已有此配置,忽略此项-->
	<mvc:annotation-driven/>
    <!--扫描opmc-spring模块-->
	<context:component-scan base-package="cn.com.servyou.yypt.opmc.agent.spring"/>
	<!--配置类-->
	<bean id="opmcConfiguration" class="cn.com.servyou.yypt.opmc.agent.config.Configuration">
	    <!--必须参数:是否启用-->
		<property name="enabled" value="true"/>
	    <!--必须参数:服务端的地址-->
	    <!--opmc生产地址为: http://opmc.dc.servyou-it.com:8001/opmc-web  -->
	    <property name="serverUrl" value="http://opmc.sit.91lyd.com/opmc-web"/>
	    <!--可选参数:应用名-->
	    <property name="${appName}" value="theTest"></property>
	    <!--可选参数:需要监测异常,ie:DataException-->
	    <!-- <property name="exceptionInclude" value=""/> -->
	</bean>

	//Metrics配置项:
    <metrics:metric-registry name="metricRegistry" id="metricRegistry"/>
    <metrics:health-check-registry id="health"/>
    <metrics:annotation-driven metric-registry="metricRegistry"/>
    <metrics:reporter type="jmx" id="metricJmxReporter" metric-registry="metricRegistry"/>
    <!-- <metrics:reporter metric-registry="metricRegistry" id="metricConsoleReporter" type="console" period="1m"/> -->


- `exceptionInclude`默认为`Throwable`和`Exception`,有其他需要则请配置

---

**aop.xml配置**

	<?xml version="1.0" encoding="UTF-8"?>
	<aspectj>
	    <!--监控非spring框架下的统计项指标-->
		<aspects>
	        <!-- 监控器配置,这里无需改动 -->
	        <concrete-aspect name="OpmcMonitor" extends="cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect。MonitorByAnnotationAspectWeaver">
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

tip:如果用于统计`@MCXXXX`(下文介绍)注解完全处于spring控制，请忽略上述**aop.xml**配置

## 第四步:配置日志

log4j.properties配置

	log4j.logger.cn.com.servyou.opmc.agent=debug,opmcRollingFile #如果需要opmc的日志输出,将opmc包的日志级别保持为debug
	log4j.appender.opmcRollingFile=org.apache.log4j.RollingFileAppender
	log4j.appender.opmcRollingFile.File=/Users/linj/Documents/logs/opmc.log #文件存放路径，请更改为实际存放路径
	log4j.appender.opmcRollingFile.MaxFileSize=20MB
	log4j.appender.opmcRollingFile.MaxBackupIndex=3
	log4j.appender.opmcRollingFile.Encoding=UTF-8
	log4j.appender.opmcRollingFile.Append=true
	log4j.appender.opmcRollingFile.layout=org.apache.log4j.PatternLayout
	log4j.appender.opmcRollingFile.layout.ConversionPattern=%d [%-5p] [%t] %c - %m(traceId=%X{traceId})%n

	log4j.additivity.cn.com.servyou.opmc.agent=false


-----


### 信息统计介绍 ###

OPMC使用Metrics作为统计插件，系统中使用对应的5种注解来对应Metrics的度量类型。

Metrics用一个键来代表监控项(统计项)，其是形式如:**xxx.xxx.xxx.xxx** 

OPMC中有两种键，一种是**静态**的，一种是**动态**的，都是通过`@MCXXXX`注解实现的。

**静态键**

`@MCXXXX`的value来构建键，如果value不存在则使用方法名来构建。 例如:

	@MCTimer("value1")
	void show(){...}

静态键为**xxx.xxx.value1**

	@MCTimer
	void show(){...}

静态键为**xxx.xxx.show**

**动态键**

`@MCXXXX`使用一下三个属性来构建动态键

- `divideParamName` ：明细统计用于切分的参数名
- `divideParamGetType` ：参数获取方式，有三种，分别是`URL`、`FORM`以及`PARSE`
- `divideParamParserClass` ：使用`PARSE`类型参数时，实现了`DivideParamParser`接口的转置类的class

动态键的形式如下:

	静态键.${divideParamName}.${divideParamValue}

其中**divideParamName**以上说明，**divideParamValue**由divideParamGetType和divideParamParserClass推导。

#### @MCGauge

仪表盘，记录瞬时值，注解的方法必须无参，有返回值，非`void`返回类型，推荐使用静态方法。

推荐用于记录仪表性指标，如在线人数、变量长度、变量数量等信息。

#### @MCCounter

计数器，用于统计方法的当前请求数。

推荐作用于接口，可统计接口当前的请求数。

#### @MCMeter

自增计数器，用于统计时间基准上的速率，即TPS。

推荐作用于http接口和dubbo接口，可统计接口的访问量。

#### @MCHistogram

直方图，统计数据分布，目前用于统计方法耗时，即方法执行消耗时间的数据分布。

推荐作用于http接口和dubbo接口，可统计接口的执行时间。

#### @MCTimer

是MCMeter和MCHistogram的组合，用于统计被注解方法的执行耗时和TPS。

推荐作用于http接口和dubbo接口。或是其他有需要统计此类型数据的方法。

*注1：目前还没有总的统计项的相关功能和配置。*


**动态键介绍**：

#### divideParamGetType ####

`divideParamGetType`三种获取方式的配置说明如下：

##### URL

插件会获取方法的`HttpServletRequest`类型参数里对应名称为`divideName`的值，因此此方式需要方法入参里有`HttpServletRequest`类型参数

*此方式适用于参数写在URL里的情况*

##### FORM(strucs)

插件会通过反射，使用`get`方法，获取方法的`ActionForm`类型参数里对应名称为`divideName`的值，因此此方式需要方法入参里有`ActionForm`类型参数，并且实际的`ActionForm`类里的参数具有`get`方法

*此方式适用于参数写在Form里的情况*

##### PARSE

此方式需要用户新建一个参数转置类，实现下面接口:

	public interface DivideParamParser {

	    String parse(Object... params);
	}

插件会取该方法的返回值作为实际统计值。

params对应注解方法的参数数组，例子见**FirstInputParamParser**实现

*此方式适用于传入的参数不适合直接作为统计项，需要进行一些额外处理的情况*


---

### 常见问题说明 ###

* 插件无效

检查配置文件的`enabled`选项，是否配置为`true`

* 方法上加了注解，监控统计却没有生效

检查插件配置文件的`basePackage`选项，是否有包含方法所在类。

* 无法抓取Controller层的异常

请确认是否有标注了`@ControllerAdvice`注解的统一异常处理器，如没有，请参照源码test包下`cn.com.servyou.yypt.opmc.agent.exceptionadvice.HandlerAdvice`类。

* 非spring框架下，统计项没有生效

检查用于非spring配置的`aop.xml`文件里的`<weaver options="-verbose">`选项，看它的`include`元素是否包含了需要统计的类所在包

* 远程JMX无法连接

检查应用服务器的启动参数配置，查看是否有`jmxremote`相关的配置，详细参数可见【应用服务器配置】处的说明。

其次检查插件配置文件，例如名为`application-opmc.xml`，查看其中

`<metrics:reporter type="jmx" id="theBeta" metric-registry="metricRegistry"/>`

这段配置的id是否和其他bean有重名。

* 出现无法代理final类的错误

这个大部分是因为aop代理重复了，系统中已经有aop配置，然后又增加了`<aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>`配置，便造成了重复代理。cglib去代理原类生成的代理类了，而代理类是final的，便会报错误。

这种情况下，可以将`proxy-target-class="true"`去除。

