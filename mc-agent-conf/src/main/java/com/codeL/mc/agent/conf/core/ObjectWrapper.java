package com.codeL.mc.agent.conf.core;

/**
 * <p>Description: 用于包装对象</p>
 * <p></p>
 *
 * @author laihj
 *         2018/2/5
 */
public class ObjectWrapper {

    /**
     * 对象
     */
    private Object object;

    /**
     * 初始化完成标志
     */
    private boolean finishInit = false;

    public Object getObject() {
        return object;
    }

    public boolean isFinishInit() {
        return finishInit;
    }

    public void setFinishInit(boolean finishInit) {
        this.finishInit = finishInit;
    }

    public ObjectWrapper(Object object) {
        this.object = object;
    }

}
