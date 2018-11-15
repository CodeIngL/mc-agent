### 非spring集成 ###

	http://192.168.110.114/laihj/mc-api-style

该项目实例提供了servlet3下的简单示例。

1. 添加相应模块

        <dependency>
            <groupId>com.codeL</groupId>
            <artifactId>mc-agent-reporter</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
	该模块提供了向外暴露度量信息的功能，比如jmx


        <dependency>
            <groupId>com.codeL</groupId>
    		<artifactId>mc-agent-core-support</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
	该模块提供了web下从HttpServletRequest获取参数的能力

        <dependency>
            <groupId>com.codeL</groupId>
    		<artifactId>mc-agent-struts</artifactId>
            <version>1.0-SNAPSHOT</version>
        </dependency>
	该模块提供了struts从actionForm获取参数的能力(当然该模块显然是支持mc-agent-core-support的)



2. 在resource下创建文件
	- mc.properties
	- mcSystem.properties

3. 配置mc.properties文件

		##应用名
		mc.user.config.appName=xxx
		##心跳地址
		mc.user.config.serverUrl=xxx
		##是否开启mc
		mc.user.config.enable=false
		##抓取的异常类型
		mc.user.config.exceptionIncludes=Throwable,Exception
		##如果你选择了<artifactId>mc-agent-reporter</artifactId>，并且想在控制台展示，且要自定义时间
		mc.user.metrics.console.reporter.period=30

4. 配置mcSystem.properties文件

		##如果你选择了<artifactId>mc-agent-core-support</artifactId>
		mcSystem.class.internalAspectHelper=com.codeL.mc.agent.fetch.helper.web.WebAspectHelper
		##如果你选择了<artifactId>mc-agent-agent-struts</artifactId>
		mcSystem.class.internalAspectHelper=com.codeL.mc.agent.fetch.helper.struts.StrutsWebAspectHelper

		##如果你选择了<artifactId>mc-agent-reporter</artifactId>，并且想在控制台展示
		mcSystem.class.internalConsoleMetricsReporter=com.codeL.mc.agent.reporter.ConsoleMetricsReporter

		##如果你选择了<artifactId>mc-agent-reporter</artifactId>，并且想通过JMX暴露
		mcSystem.class.internalJmxMetricsReporter=com.codeL.mc.agent.reporter.JmxMetricsReporter

5. 配置aop.xml文件

		<?xml version="1.0" encoding="UTF-8"?>
		<aspectj>
		    <!--监控非spring框架下的统计项指标-->
			<aspects>
		        <!-- 监控器配置,这里无需改动 -->
		        <concrete-aspect name="McMonitor" extends="com.codeL.mc.agent.fetch.weaver.aspect.MonitorByAnnotationAspectWeaver">
		            <pointcut name="timerPoint" expression="@annotation(com.codeL.mc.agent.fetch.annotation.define.MCTimer)" />
		            <pointcut name="meterPoint" expression="@annotation(com.codeL.mc.agent.fetch.annotation.define.MCMeter)" />
		            <pointcut name="counterPoint" expression="@annotation(com.codeL.mc.agent.fetch.annotation.define.MCCounter)" />
		            <pointcut name="histogramPoint" expression="@annotation(com.codeL.mc.agent.fetch.annotation.define.MCHistogram)" />
		        </concrete-aspect>

			</aspects>
		    <!--需要监测的包,可以多个include元素来指定多个包.使用时,请替代为实际项目里的包名-->
		    <!--<weaver options="-XnoInline -Xlint:-cantFindType"> 这种写法可以忽略掉报错信息-->
			<weaver options="-verbose">
		        <!--该写法下,监控器将会监控com.codeL.${yourpackage}这个包下的所有类,不包括子包-->
				<include within="com.codeL.${yourpackage}.*" />
		        <!--该写法下,监控器将会监控com.codeL.${yourpackage}这个包以及子包下的所有类-->
				<include within="com.codeL.${yourpackage}..*" />
				<exclude within="cn.com..*CGLIB*" />
			</weaver>
		</aspectj>


6. 配置日志文件

		log4j.logger.com.codeL.mc.agent=debug,mcRollingFile #如果需要mc的日志输出,将mc包的日志级别保持为debug
		log4j.appender.mcRollingFile=org.apache.log4j.RollingFileAppender
		log4j.appender.mcRollingFile.File=/Users/linj/Documents/logs/mc.log #文件存放路径，请更改为实际存放路径
		log4j.appender.mcRollingFile.MaxFileSize=20MB
		log4j.appender.mcRollingFile.MaxBackupIndex=3
		log4j.appender.mcRollingFile.Encoding=UTF-8
		log4j.appender.mcRollingFile.Append=true
		log4j.appender.mcRollingFile.layout=org.apache.log4j.PatternLayout
		log4j.appender.mcRollingFile.layout.ConversionPattern=%d [%-5p] [%t] %c - %m(traceId=%X{traceId})%n
		log4j.additivity.com.codeL.mc.agent=false

7. 配置VM参数

下载[findAspectjAgent.sh](http://192.168.2.107/laihj/findagent/blob/master/findAspectjAgent.sh)该文件放置到tomcat的bin目录下

catalina.sh中添加

		. findPassthroughAgent.sh "$CATALINA_BASE"/conf/server.xml "$CATALINA_HOME"/webapps
		JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote \
		-Dcom.sun.management.jmxremote.port=18090 \
		-Dcom.sun.management.jmxremote.ssl=false \
		-Dcom.sun.management.jmxremote.authenticate=false \
		-Djava.rmi.server.hostname=${本机ip}"

*注1：port为本应用的JMX远程连接端口，一般是18090，非此端口需要和基础架构组说明*
*注2：hostname为本机对外暴露的IP*

findAspectjAgent.sh用于找到相应的agent。下面是脚本测试示例:

        #!/bin/bash

        CATALINA_BASE="/d/progammer/apache-tomcat-7.0.52"
        CATALINA_HOME="/d/progammer/apache-tomcat-7.0.52"

        JAVA_OPTS="";

        . findPassthroughAgent.sh "$CATALINA_BASE"/conf/server.xml "$CATALINA_HOME"/webapps

        echo $JAVA_OPTS

        JAVA_OPTS="$JAVA_OPTS -Dcom.sun.management.jmxremote \
        	-Dcom.sun.management.jmxremote.port=18090 \
        	-Dcom.sun.management.jmxremote.ssl=false \
        	-Dcom.sun.management.jmxremote.authenticate=false \
        	-Djava.rmi.server.hostname=192.168.100.110"

        ehco $JAVA_OPTS
