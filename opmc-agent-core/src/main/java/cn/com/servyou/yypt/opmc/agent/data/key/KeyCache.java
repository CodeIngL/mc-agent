package cn.com.servyou.yypt.opmc.agent.data.key;

import cn.com.servyou.yypt.opmc.agent.entity.DivideConfigInfo;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 * @date 2018/3/15
 * @see
 */
public class KeyCache {

    private String staticKey;

    private boolean hasDynamicKey = false;

    private DivideConfigInfo configIno;

    private Long timestamp = System.currentTimeMillis();

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
