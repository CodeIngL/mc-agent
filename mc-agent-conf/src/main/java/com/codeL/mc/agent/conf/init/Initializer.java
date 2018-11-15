package com.codeL.mc.agent.conf.init;

/**
 * <p>Description: 初始化接口，依赖构建后，可以调用bean的初始化话接口</p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/7
 */
public interface Initializer {

    /**
     * 调用
     */
    void init();

}
