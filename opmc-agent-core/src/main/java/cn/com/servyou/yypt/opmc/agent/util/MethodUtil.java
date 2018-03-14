package cn.com.servyou.yypt.opmc.agent.util;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;


/**
 * <p>Function: [功能模块：]</p>
 * <p>Description: [功能描述：方法反射调用工具类]</p>
 * <p>Copyright: Copyright(c) 2009-2018 税友集团</p>
 * <p>Company: 税友软件集团有限公司</p>
 *
 * @author linj
 * @version 1.0
 */
public class MethodUtil {

    /**
     * 反射调用普通方法
     *
     * @param owner      方法拥有者
     * @param methodName 方法名
     * @param args       实际入参
     * @return Object
     */
    public static Object invokeMethod(Object owner, String methodName, Object[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class ownerClass = owner.getClass();
        Class[] argsClass = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClass[i] = args[i].getClass();
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(owner, args);
    }

    /**
     * 反射调用静态方法
     *
     * @param owner      方法拥有者
     * @param methodName 方法名
     * @param args       实际入参
     * @return Object
     */
    public static Object invokeStaticMethod(String owner, String methodName, Object[] args) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Class ownerClass = Class.forName(owner);
        Class[] argsClass = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            argsClass[i] = args[i].getClass();
        }
        Method method = ownerClass.getMethod(methodName, argsClass);
        return method.invoke(null, args);
    }

    /**
     * 判断是否静态方法
     *
     * @param method 方法
     * @return boolean
     */
    public static boolean isStaticMethod(Method method) {
        return Modifier.isStatic(method.getModifiers());
    }


    /**
     * 无参数的反射方法调用
     *
     * @param owner      方法拥有者
     * @param methodName 方法名
     * @return Object
     */
    public static Object invokeMethod(Object owner, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return invokeMethod(owner, methodName, new Object[]{});
    }

    /**
     * 调用get方法
     *
     * @param owner      方法拥有者
     * @param methodName 方法名
     * @return Object
     */
    public static Object invokeGetMethod(Object owner, String methodName) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        methodName = "get" + upperFirstChar(methodName);
        return invokeMethod(owner, methodName);
    }

    /**
     * 从aop切片获取注册方法
     *
     * @param pjp aop切片
     * @return Method
     */
    public static Method getSignatureMethod(ProceedingJoinPoint pjp) {
        return ((MethodSignature) pjp.getSignature()).getMethod();
    }

    /**
     * 从aop切片,利用反射获取真实方法
     *
     * @param pjp            aop切片
     * @param parameterTypes 方法入参
     * @return Method
     */
    public static Method getRealMethod(ProceedingJoinPoint pjp, Class<?>... parameterTypes) throws NoSuchMethodException {
        //获取目标类
        Class targetClass = pjp.getTarget().getClass();
        Signature signature = pjp.getSignature();
        return targetClass.getDeclaredMethod(signature.getName(), parameterTypes);
    }

    /**
     * 大小写互转偏移量
     */
    private static final int CASE_OFFSET = 32;

    /**
     * 首字母大写
     *
     * @param in 字符串
     * @return String
     */
    public static String upperFirstChar(String in) {
        char[] cs = in.toCharArray();
        cs[0] -= CASE_OFFSET;
        return String.valueOf(cs);
    }


}
