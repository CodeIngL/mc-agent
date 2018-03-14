package cn.com.servyou.yypt.opmc.agent.data.metrics;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
 *
 * @author laihj
 */
public class MetricsKey {

    private String key;

    private String dynamicKey;

    public MetricsKey(String key, String dynamicKey) {
        this.key = key;
        this.dynamicKey = dynamicKey;
    }

    public MetricsKey() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDynamicKey() {
        return dynamicKey;
    }

    public void setDynamicKey(String dynamicKey) {
        this.dynamicKey = dynamicKey;
    }
}
