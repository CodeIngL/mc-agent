package com.codeL.mc.agent.data.metrics;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 */
public class MetricsKey {

    /**
     * 静态键
     */
    private String key;

    /**
     * 动态键
     */
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
