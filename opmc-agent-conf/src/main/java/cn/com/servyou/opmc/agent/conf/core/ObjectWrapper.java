package cn.com.servyou.opmc.agent.conf.core;

/**
 *
 * <p>Description: 用于包装对象</p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/2/5
 */
public class ObjectWrapper {

    private Object object;

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
