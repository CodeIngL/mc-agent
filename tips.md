## 开发难点 || 耗时点 || 注意点

### zabbix数据采集压力问题

我们模拟估计过数据量，如果信息部项目都上了opmc，并且监控项都按照调研的数据量来的话，zabbix取数的压力是有点大的。

>总mbean数为5400个。
按每个mbean需要获取的key数为5个计算 count meanrate min max mean
每次zabbix需要同时获取29000个key值。
按一分钟获取一次数据，数据保存一周计算，一周的数据量为2.9亿

>运维中心：可以接受，数据获取间隔保持在三分钟以上即可。

具体数据量的估算可以参考keys.md

现在运维中心是给我们搭建了一个取数的zabbix-proxy，但是这个代理的实际取数性能也没有测试过，不保证在系统全部上线以后能靠谱。

*如果后期要上根据执行结果来统计的功能的话，必须更加的考虑到zabbix数据压力的问题，因为数据量是成倍的上涨*

### 包扫描

在包扫描类`ComponentScan`里，可以看到不止是抓了`ClassNotFoundException`，还抓了`Error`。在实际扫描的过程中，如果配置的命名空间比较大，那么就会扫描这些命名空间下的所有类。如果有类找不到，那这个类就应该不处理，所以抓取`ClassNotFoundException`异常，只输出提示信息。`Error`的出现情况在于，某些包的某些类所引用的类不存在，即在编译阶段就应该报错的。在实际运行时如果没有使用到，便不会报错，而在扫包的过程中，则是因为扫包时会初始化class，所以会报`Error`级别的错误，会阻断插件启动，所以这种情况也应该抓取错误，只输出提示信息。

### Metrics监控项初始化

`MetricsMonitorInitListener`为基于`spring`的启动器，在spring加载完毕时启动，绑定的是`ContextRefreshedEvent`事件，但是此事件有可能刷新多次，因此需要一个`initialized`标志位来控制初始化次数为一次。

接口`MetricsBuilder`是为了适配统计指标初始化而设立的，否则按照原来的方法，是使用反射来进行适配获取的。

*尽可能的减少反射使用，可以减少运行资源消耗*

`MetricsHelper`类的`register`方法捕捉了`IllegalArgumentException`异常而不处理，原因已在注释里写明。

Metrics的监控项名称是无法被zabbix自动全部获取 的，需要注册在一个约定好名字的监控项中，该监控项提供一个json格式的字符串，将所监控系统内的所有监控项包装为K:V的形式注册在jmx里。需要注意的是，这里的json格式字符串包装是用的java原生方法。位于`MetricsKeyCache`类的`convertKeysMapToJsonString`方法。逗号的增加和去除需要特别注意。

### Metrics动态监控项

组装动态监控项的一个前提，就是找到运行时增加监控项的方法，在`MetricRegistry`类中有对应的方法，而`MetricRegistry`类的实例则是依靠spring去获取。

### Metrics按参数明细统计

此功能下的转置器，使用了`DivideParamParserCache`作为转置器缓存，未使用线程安全机制，为了节省开销。

### Metrics 注解属性的获取

使用了`AnnotationPropertiesGetter`类作为注解属性的获取器，如果不采取这种写法的话，就需要一堆if嵌套，或者是反射执行，都不太友好，所以采取接口内部实现的方法来进行适配。

### 异常抓取和发送

异常的发送采取异步模式，使用异常抓取缓存`ExceptionCaughtCache`，现在不允许名称相同的异常重复放入,防止出现连续不断的相同类型异常报警，对opmc服务端和数据库造成过大的压力。

### AOP织入

spring的aop和原生的aop织入实现不大一样，所以会有一个`cn.com.servyou.yypt.opmc.agent.fetch.weaver.aspect`包，里头放的是原生的织入类。


