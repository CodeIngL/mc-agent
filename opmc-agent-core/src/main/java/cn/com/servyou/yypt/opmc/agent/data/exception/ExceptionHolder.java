package cn.com.servyou.yypt.opmc.agent.data.exception;

/**
 * <p>Description: </p>
 * <p>税友软件集团有限公司</p>
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


    /**
     *
     */
    private boolean available = true;

    public ExceptionHolder() {
    }

    public ExceptionHolder(Throwable throwable, long timestamp) {
        this.throwable = throwable;
        this.timestamp = timestamp;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
