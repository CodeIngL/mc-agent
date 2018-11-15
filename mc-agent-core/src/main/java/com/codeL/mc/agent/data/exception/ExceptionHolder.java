package com.codeL.mc.agent.data.exception;

/**
 * <p>Description: </p>
 * <p></p>
 *
 * @author laihj
 */
public class ExceptionHolder {

    /**
     *
     */
    private Throwable throwable;

    /**
     *
     */
    private long timestamp;

    /**
     *
     */
    private int counts;


    public ExceptionHolder() {
    }

    public ExceptionHolder(Throwable throwable, long timestamp) {
        this.throwable = throwable;
        this.timestamp = timestamp;
        this.counts = 0;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getCounts() {
        return counts;
    }

    public void setCounts(int counts) {
        this.counts = counts;
    }

}
