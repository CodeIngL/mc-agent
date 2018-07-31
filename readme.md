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

    - 服务端尚未开发,agent已有相关监测

# 快速开始

## 第一步:选择合适依赖

pom中添加:

        <dependencyManagement>
            <dependencies>
                <dependency>
                    <groupId>cn.com.servyou</groupId>
                    <artifactId>opmc-agent-bom</artifactId>
                    <version>1.5-RELEASE</version>
                    <scope>import</scope>
                    <type>pom</type>
                </dependency>
            </dependencies>
        </dependencyManagement>

dependencies中添加:

        <dependency>
            <groupId>cn.com.servyou</groupId>
            <artifactId>opmc-agent-spring</artifactId>
        </dependency>
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
        </beans>

## 第三步:配置VM参数

**配置VM参数(针对tomcat)**:

下载[findAgent.sh](http://192.168.2.107/laihj/findagent/blob/master/findAgent.sh)该文件放置到tomcat的bin目录下


然后在catalina.sh的位置

        # ----- Execute The Requested Command -----------------------------------------

添加以下代码

    . "$CATALINA_HOME"/bin/findAgent.sh "$CATALINA_BASE"/conf/server.xml "$CATALINA_HOME"/webapps
    JAVA_OPTS="$JAVA_OPTS -Djava.rmi.server.hostname=${本机ip}"

**请把本机ip替换成你的机器IP**

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

* 无法抓取Controller层的异常

请确认是否有标注了`@ControllerAdvice`注解的统一异常处理器，如没有，请参照源码test包下`cn.com.servyou.yypt.opmc.agent.exceptionadvice.HandlerAdvice`类。

* 非spring框架下，统计项没有生效

检查用于非spring配置的`aop.xml`文件里的`<weaver options="-verbose">`选项，看它的`include`元素是否包含了需要统计的类所在包

这段配置的id是否和其他bean有重名。

* 出现无法代理final类的错误

这个大部分是因为aop代理重复了，系统中已经有aop配置，然后又增加了`<aop:aspectj-autoproxy proxy-target-class="true"></aop:aspectj-autoproxy>`配置，便造成了重复代理。cglib去代理原类生成的代理类了，而代理类是final的，便会报错误。

这种情况下，可以将`proxy-target-class="true"`去除。

