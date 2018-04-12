## 简介

opmc-agent是运营平台监控中心的客户端插件，通过简单的配置，ie:在监控的方法上增加对应的注解即可实现业务系统的具体监控。

当前具有基本信息统计（基于Metrics）、心跳以及异常抓取功能，其中异常抓取包括Controller层的异常抓取、Dubbo客户端、服务端的异常抓取以及日志输出的异常抓取。

opmc-agent原版由**林菁**开发，新版由**laihj**开发，与之前旧版相比,具有以下特点

1. 模块化插件,不再依赖spring
2. 缓存切面操作，提高统计性能
3. 代码设计重构

关于细节,请参阅源码以及API

### opmc-agent功能特征 ###

opmc-agent主要有以下功能

1. 信息统计

2. 异常抓取

3. 心跳监测

---

## 功能之信息统计

OPMC使用Metrics作为统计插件，继承了Metrics的5种度量类型，系统中使用对应的5种注解进行标注使用。

#### Gauge

仪表盘，记录瞬时值，使用`@MCGauge`注解激活，使用该注解的方法必须有返回值，不可以是`void`返回类型，不可有入参，推荐使用静态方法。

推荐用于记录仪表性指标，如在线人数、变量长度、变量数量等信息。

#### Counter

计数器，使用`@MCCounter`注解激活，方法被激活时计数器+1，方法激活完毕之后计数器-1，用于统计方法的当前请求数。

推荐作用于接口，可统计接口当前的请求数。

#### Meter

自增计数器，使用`@MCMeter`激活，用于统计时间基准上的速率，即TPS。

推荐作用于http接口和dubbo接口，可统计接口的访问量。

#### Histogram

直方图，统计数据分布，使用`@MCHistogram`激活，目前用于统计方法耗时，即方法执行消耗时间的数据分布。

推荐作用于http接口和dubbo接口，可统计接口的执行时间。

#### Timer

是Meter和Histogram的组合，使用`@MCTimer`激活，用于统计被注解方法的执行耗时和TPS。

推荐作用于http接口和dubbo接口。或是其他有需要统计此类型数据的方法。

*注1：在OPMC的统计项名称，会取注解的value值，如果注解没有value值，那么就取methodName。例`@MCTimer("timer1")`会以`timer1`作为统计项名称，否则以被注解的methodName作为名称。*

*注2：每个被注解的方法，opmc-agent都会生成该方法的统计项。目前还没有总的统计项的相关功能和配置。*

### 明细信息统计

此功能基于上述信息统计，为基本信息统计的扩展。

在4类注解下（不包括`@MCGauge`），插件可以根据入参的名称和实际值进行分别统计。

例：方法`getName()`会根据`type`来进行业务方法的重定向，现需要统计它各类`type`的明细值，只需在注解里增加对应的配置即可。

插件会统计方法整体的统计值，以及根据`type`参数区分之后的统计值，如`type`在实际运行过程中接收到了`2`、`3`、`4`的实际入参，那么插件会根据实际入参的值动态生成`getName.type.2`、`getName.type.3`、`getName.type.4`的明细统计项以及整体`getName`的统计项。

*注1：该功能目前支持Struts1框架，其他框架未做测试和适配*
*注2：该功能只支持针对一个入参进行切分统计*

---

## 功能之异常抓取

插件支持以下异常抓取,异常的处理都是统一方式处理

1. spring的Controller层异常抓取(spring模块)

2. Dubbo调用的异常抓取(dubbo模块)

3. 日志输出的异常抓取(基本模块)
	- 使用依赖aspectj
	- 支持`slf4j`,`apache log4j`以及`apache common logging`

### Spring下的Controller层

该功能要求项目中有一个标注了`@ControllerAdvice`注解的统一异常处理器，插件拦截标注`@ExceptionHandler`注解的处理方法，进行异常的捕获。

方法样例可见插件源码test包下`cn.com.servyou.yypt.opmc.agent.exceptionadvice.HandlerAdvice`类。

*注：考虑到目前用的都是这类异常拦截器，所以目前只有此类的适配，使用`HandlerExceptionResolver`接口的实现类进行异常处理的情况下，插件不会进行异常拦截。*

### Dubbo调用

通过Dubbo自带SPI方式实现，名字为`OPMCExceptionFilter`。

### 日志输出的异常抓取

拦截以下形式的方法

	log.error(...,Throwable)

---

### 功能之心跳处理

不需要关心


--- 

## 使用方法

spring下pom.xml依赖


        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-spring</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
        <dependency>
            <groupId>com.ryantenney.metrics</groupId>
            <artifactId>metrics-spring</artifactId>
            <version>3.1.3</version>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework</groupId>
                    <artifactId>spring-context-support</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
        <dependency>
            <groupId>io.dropwizard.metrics</groupId>
            <artifactId>metrics-core</artifactId>
            <version>3.2.5</version>
        </dependency>


dubbo模块pom.xml依赖


        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-dubbo</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>


*注意版本号*


### 插件配置文件


**spring配置**
	
	<!--如果项目中已有以下配置,可以去掉或者注释此项-->
	<aop:aspectj-autoproxy proxy-target-class="true"/>
	<!--如果项目中已有此配置,去掉或者注释此配置项-->
	<mvc:annotation-driven/>
	<context:component-scan base-package="cn.com.servyou.yypt.opmc.agent.spring"/>
	<bean id="opmcConfiguration" class="cn.com.servyou.yypt.opmc.agent.config.Configuration">
	   <!--必须参数:插件是否启用,true启用,false为不启用-->
		<property name="enabled" value="true"/>
	    <!--必须参数:opmc服务端的地址-->
	    <!--opmc生产地址为: http://opmc.dc.servyou-it.com:8001/opmc-web  -->
	    <property name="serverUrl" value="http://opmc.sit.91lyd.com/opmc-web"/>
	    <!--可选参数:应用名,更改为实际应用名-->
	    <property name="${appName}" value="theTest"></property>
	    <!--可选参数:需要监测的异常,该配置项优先级为高,以SimpleName作为判断条件,例:DataException-->
	    <!-- <property name="exceptionInclude" value=""/> -->
	    <!--可选参数:不需要监测的异常,该配置项优先级为低,以SimpleName作为判断条件,例:DataException-->
	    <!-- <property name="exceptionExclude" value=""/> -->
	</bean>

	//Metrics配置项：
    <metrics:metric-registry name="metricRegistry" id="metricRegistry"/>
    <metrics:health-check-registry id="health"/>
    <metrics:annotation-driven metric-registry="metricRegistry"/>
    <metrics:reporter type="jmx" id="metricJmxReporter" metric-registry="metricRegistry"/>
    <!-- <metrics:reporter metric-registry="metricRegistry" id="metricConsoleReporter" type="console" period="1m"/> -->
	

*注1：`exceptionInclude`和`exceptionExclude`的优先级解释：插件抓取到异常，判断是否需要发送时，只要异常名在`exceptionInclude`中配置了，就会进行发送，即使是在`exceptionExclude`中配置了一样的名字。*

*注2：如果项目Spring的配置和SpringMVC的配置是分开的，即有一个`application.xml`和一个`springmvc-servlet.xml`，那么两个文件处，分别需要声明`<aop:aspectj-autoproxy />`，这是由于Spring的上下文和SpringMVC的上下文不同，单一配置无法两者都生效。*

*注3：重复的`<mvc:annotation-driven/>`配置可能会造成异常，包括但不限于前后台数据不一致等*


**非spring配置**

针对非spring模块的统计，除了要配置插件配置文件和spring配置文件之外，还需要再配置aop配置文件以及服务启动参数。

*注：如有非spring模块的统计需求，可按此配置，基于spring框架的新项目不需做此配置。*

**aop配置文件**

aop配置文件的样例位于`classpath:nonspring/opmc-aop.example`，复制该文件到你的项目中，转换为xml格式，按照说明修改配置，并且最终加载它。

该文件的`<aspects>`节点不允许修改。

`<weaver>`节点用于配置需要进行统计的包名，不支持逗号分割包名。如需要指定多个包，可使用多个`include`元素来指定
```
<!--需要统计的包,可以多个include元素来指定多个包-->
	<weaver options="-verbose">
		<include within="cn.com.servyou.yypt.opmc.agent.aspectj.*" />
		<include within="cn.com.servyou.yypt.opmc.agent.a..*" />
	</weaver>
```
*注1：`include`中配置的包中不可含有spring方式加载并且需要统计的代码，会影响插件运行。*

*注2：`include`中，如果需要包含某个包以及它的子包，请使用`cn.com.servyou.yypt.opmc.agent.a..*`这种写法*


##### 服务启动参数

非spring的统计项配置需要在JAVA启动参数里，增加如下参数，才能进行原生aspectj的织入：

```
-javaagent:/home/aspectj/aspectjweaver/1.8.9/aspectjweaver-1.8.9.jar
-Dorg.aspectj.weaver.loadtime.configuration=nonspring/aop.xml
-Dorg.aspectj.weaver.loadtime.configuration.lightxmlparser=true
```

### 日志文件配置(重要)

log4j.properties配置

	#opmc日志输出部分,将opmc日志保存到独立的文件中
	#如果需要opmc的日志输出,将opmc包的日志级别保持为debug
	log4j.logger.cn.com.servyou.yypt.opmc.agent=debug,opmcRollingFile
	#opmc RollingFile Appender
	log4j.appender.opmcRollingFile=org.apache.log4j.RollingFileAppender
	#文件存放路径，请更改为实际存放路径
	log4j.appender.opmcRollingFile.File=/Users/linj/Documents/logs/opmc.log
	#文件最大尺寸
	log4j.appender.opmcRollingFile.MaxFileSize=20MB
	#文件最大保留个数
	log4j.appender.opmcRollingFile.MaxBackupIndex=3
	#日志文件不输出到root里
	log4j.additivity.cn.com.servyou.yypt.opmc.agent=false
	
	#日志文件编码
	log4j.appender.opmcRollingFile.Encoding=UTF-8
	log4j.appender.opmcRollingFile.Append=true
	log4j.appender.opmcRollingFile.layout=org.apache.log4j.PatternLayout
	log4j.appender.opmcRollingFile.layout.ConversionPattern=[%p] %-d{yyyy-MM-dd HH:mm:ss} %m %l%n



### 代码内配置

#### 基本信息统计

5类注解（如`@MCGauge`），在方法前加上注解即可进行对应类型的统计。5种度量类型的用处可参考文档插件说明处的描述。

统计项的指标名（在opmc-web中显示的指标名称）的命名规则为统计类型.类名.方法别名（如`MCTimer.TestController.showList`）。

假设方法名为`show`，如果注解设置了value（如`@MCTimer("value1")`)，那么指标名将会是`MCTimer.TestController.value1`，如果未设置value,那么指标名会取方法名，变成`MCTimer.TestController.show`。

#### 按入参明细信息统计

明细信息统计支持除了`@MCGauge`之外的4种注解，这4种注解提供了3个参数来进行明细信息统计的配置。

* `divideParamName` ：明细统计用于切分的参数名
* `divideParamGetType` ：参数获取方式，有三种，分别是`URL`、`FORM`以及`PARSE`
* `divideParamParserClass` ：使用`PARSE`类型参数时，实现了`DivideParamParser`接口的转置类的class


参数三种获取方式的配置说明如下：

##### URL

插件会获取方法的`HttpServletRequest`类型参数里对应名称为`divideName`的值，因此此方式需要方法入参里有`HttpServletRequest`类型参数

*此方式适用于参数写在URL里的情况*

##### FORM

插件会通过反射，使用`get`方法，获取方法的`ActionForm`类型参数里对应名称为`divideName`的值，因此此方式需要方法入参里有`ActionForm`类型参数，并且实际的`ActionForm`类里的参数具有`get`方法

*此方式适用于参数写在Form里的情况*

##### PARSE

此方式需要用户新建一个参数转置类，继承`cn.com.servyou.yypt.opmc.agent.fetch.jdls.divide.interfaces.DivideParamParser`接口，实现接口的`parse(Object... params)`方法，插件会取该方法的返回值作为实际统计值。

插件会将被统计方法获取到的实际参数==以数组形式全部传入==`parse(Object... params)`中，因此在实现该方法时，获取被统计方法实际参数的时候需要自己转置一把类型。

例如，被统计方法的参数声明是这样的：

```

public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)

```

那么在实现`parse(Object... params)`方法的时候，如果要获取第三个`HttpServletRequest`参数，需要这样转置一下（例）：


```

public String parse(Object... params) {
     if (params[2] != null) {
             if (params[2] instanceof HttpServletRequest) {
                 HttpServletRequest request = (HttpServletRequest) params[2];
             }
         }
         return "others";
     }

```

*此方式适用于传入的参数不适合直接作为统计项，需要进行一些额外处理的情况*


#### 异常抓取

对于SpringMVC下的Controller层，只需项目里拥有一个标注了`@ControllerAdvice`注解的异常拦截器即可。

Dubbo的异常抓取不需任何代码内的配置项，对业务代码无入侵。

日志输出的异常抓取也不需要任何代码内的配置项，对业务代码无入侵。

### 应用服务器配置

由于插件是将数据发送至JMX以供服务端进行读取的，所以应用服务器在启动时，需要在JAVA启动参数里增加JMX的配置，配置如下：


```
-Dcom.sun.management.jmxremote
-Dcom.sun.management.jmxremote.port=18090
-Dcom.sun.management.jmxremote.ssl=false
-Dcom.sun.management.jmxremote.authenticate=false
-Djava.rmi.server.hostname=10.200.100.3

```

*注1：port为本应用的JMX远程连接端口，一般是18090，非此端口需要和基础架构组说明*
*注2：hostname为本机对外暴露的IP*

## 常见问题

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

