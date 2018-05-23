package cn.com.servyou.opmc.agent.conf.manager;

import cn.com.servyou.opmc.agent.conf.annotation.ConfigAnnotation;
import cn.com.servyou.opmc.agent.conf.core.Config;
import cn.com.servyou.opmc.agent.conf.core.ObjectWrapper;
import cn.com.servyou.opmc.agent.conf.core.PropConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
@Slf4j
public class ConfManager {

    /**
     * bean集合
     */
    private Map<String, ObjectWrapper> innerObject = new ConcurrentHashMap<String, ObjectWrapper>(16);

    /**
     * 循环引用标记
     */
    private Set<Object> cycleObject = new HashSet<Object>(16);

    /**
     * 配置集合
     */
    private Config conf = new PropConfig();

    /**
     * @return 获得内部配置
     */
    public Map<String, String> getInnerConf() {
        return conf.getProperties();
    }

    /**
     * 获得内部创建的bean
     *
     * @param name bean 名称
     * @return bean
     */
    public Object getBean(String name) {
        return innerObject.containsKey(name) ? innerObject.get(name).getObject() : null;
    }

    public Map<String, ObjectWrapper> getAll() {
        return innerObject;
    }

    /**
     * 放入bean
     *
     * @param key bean名称
     * @param cls 类名
     */
    public void putBean(String key, Class cls) {
        if (key == null || "".equals(key)) {
            return;
        }
        try {
            innerObject.put(key, new ObjectWrapper(cls.newInstance()));
        } catch (InstantiationException e) {
            //ignore
        } catch (IllegalAccessException e) {
            throw new RuntimeException("can't access" + cls.getName());
        }
    }

    /**
     * 加载配置
     *
     * @param fileName 配置文件名
     * @throws RuntimeException 异常
     */
    public void loadFrameworkConf(String fileName) throws RuntimeException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL localURL = null;
        //假定其他jar包中不存在相同配置文件，
        //即使存在相同配置文件，不存在相同的配置项键
        //file协议会是最高的优先级
        try {
            Enumeration<URL> urls = loader.getResources(fileName);
            while (urls.hasMoreElements()) {
                URL url = urls.nextElement();
                if ("file".equals(url.getProtocol())) {
                    localURL = url;
                } else if ("jar".equals(url.getProtocol())) {
                    loadConfFromURL(url);
                } else if ("zip".equals(url.getProtocol())) {
                    loadConfFromURL(url);
                }
                //other ignore
            }
            if (localURL != null) {
                loadConfFromURL(localURL);
            }
        } catch (IOException e) {
            throw new RuntimeException("it isn't a legally URL:" + fileName);
        }
    }

    /**
     * 以URL的方式加载
     *
     * @param url
     */
    private void loadConfFromURL(URL url) {
        InputStream in = null;
        try {
            in = url.openStream();
            if (in == null) {
                return;
            }
            Properties prop = new Properties();
            prop.load(in);
            for (Map.Entry<Object, Object> entry : prop.entrySet()) {
                String key = (String) entry.getKey();
                String value = (String) entry.getValue();
                conf.setProperty(key, value);
            }
        } catch (IOException e) {
            //ignore
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error("did nothing ", e);
                    throw new RuntimeException(url.toString() + "is a mistake for config");
                }
            }
        }
    }

    /**
     * 内部信息构建对象依赖
     *
     * @throws IllegalAccessException 异常
     */
    public void buildSimpleDependency() throws IllegalAccessException {
        for (Map.Entry<String, ObjectWrapper> wrapper : innerObject.entrySet()) {
            buildSimpleDependency(wrapper.getValue(), getInnerConf());
        }
    }

    /**
     * 构建内部依赖
     * tip;不支持循环引用，一旦循环引用导致栈溢出
     *
     * @param wrapper
     * @param conf
     */
    private void buildSimpleDependency(ObjectWrapper wrapper, Map<String, String> conf) throws IllegalAccessException {
        Object obj = wrapper.getObject();
        if (cycleObject.contains(obj)) {
            throw new RuntimeException("circular reference" + obj.getClass());
        }
        if (obj == null) {
            return;
        }
        setFiled(obj.getClass(), obj, conf);
        wrapper.setFinishInit(true);
        cycleObject.remove(obj);
    }

    private void setFiled(Class cls, Object obj, Map<String, String> conf) throws IllegalAccessException {
        if ("java.lang.Object".equals(cls.getName())) {
            return;
        }
        if (cls.getName().startsWith("[")) {
            return;
        }
        Field[] fields = cls.getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(ConfigAnnotation.class)) {
                if (Modifier.isStatic(field.getModifiers())) {
                    continue;
                }
                ConfigAnnotation config = field.getAnnotation(ConfigAnnotation.class);
                String name = config.name();
                if (innerObject.containsKey(name) && !innerObject.get(name).isFinishInit()) {
                    buildSimpleDependency(innerObject.get(name), conf);
                }
                field.setAccessible(true);
                if (innerObject.containsKey(name) && innerObject.get(name).isFinishInit()) {
                    field.set(obj, innerObject.get(name).getObject());
                    continue;
                }
                String value = conf.get(name);
                //assume list is ArrayList<String>
                Object valueObj = convertString(value, field.getType());
                if (valueObj == null) {
                    throw new RuntimeException("config file is not correct." + "key:" + name + "\tvalue:" + value + "\tclass:" + obj.toString() + "\tproperties:" + field.getName());
                }
                field.set(obj, valueObj);
            }
        }
        setFiled(cls.getSuperclass(), obj, conf);
    }

    /**
     * 简易的转换String到各种类型
     *
     * @param value
     * @param cls
     * @return
     */
    private Object convertString(String value, Class cls) {
        if (cls.isAssignableFrom(String.class)) {
            return value;
        }
        if (cls.isAssignableFrom(List.class)) {
            List<String> list = new ArrayList<String>();
            Collections.addAll(list, value.split(","));
            return list;
        }
        if (cls.isAssignableFrom(Set.class)) {
            Set<String> set = new HashSet<String>();
            Collections.addAll(set, value.split(","));
            return set;
        }
        if (cls.isAssignableFrom(Integer.class) || cls.isAssignableFrom(int.class)) {
            return Integer.valueOf(value);
        } else if (cls.isAssignableFrom(Double.class) || cls.isAssignableFrom(double.class)) {
            return Double.valueOf(value);
        } else if (cls.isAssignableFrom(Float.class) || cls.isAssignableFrom(float.class)) {
            return Float.valueOf(value);
        } else if (cls.isAssignableFrom(Long.class) || cls.isAssignableFrom(long.class)) {
            return Long.valueOf(value);
        } else if (cls.isAssignableFrom(Short.class) || cls.isAssignableFrom(short.class)) {
            return Short.valueOf(value);
        } else if (cls.isAssignableFrom(Byte.class) || cls.isAssignableFrom(byte.class)) {
            return Byte.valueOf(value);
        } else if (cls.isAssignableFrom(Boolean.class) || cls.isAssignableFrom(boolean.class)) {
            return Boolean.valueOf(value);
        }
        return null;
    }
}
