package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 *         2018/3/15
 */
public class KeyCache {

    /**
     * 静态值
     */
    private String staticKey;

    /**
     * 是否含有动态值
     */
    private boolean hasDynamicKey = false;

    /**
     * 动态配置
     */
    private DivideConfigInfo configIno;

    /**
     * 产生时间
     */
    private Long timestamp = System.currentTimeMillis();


    //---get---set方法


    public String getStaticKey() {
        return staticKey;
    }

    public void setStaticKey(String staticKey) {
        this.staticKey = staticKey;
    }

    public boolean isHasDynamicKey() {
        return hasDynamicKey;
    }

    public void setHasDynamicKey(boolean hasDynamicKey) {
        this.hasDynamicKey = hasDynamicKey;
    }

    public DivideConfigInfo getConfigIno() {
        return configIno;
    }

    public void setConfigIno(DivideConfigInfo configIno) {
        this.configIno = configIno;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
