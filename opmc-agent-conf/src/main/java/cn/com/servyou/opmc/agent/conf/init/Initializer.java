package cn.com.servyou.opmc.agent.conf.init;

/**
 * <p>Description: 初始化接口，依赖构建后，可以调用bean的初始化话接口</p>
 * <p>税友软件集团有限公司</p>
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
