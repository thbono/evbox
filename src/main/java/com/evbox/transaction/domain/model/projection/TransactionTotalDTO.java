package com.evbox.transaction.domain.model.projection;

public class TransactionTotalDTO {

    private int started;
    private int stopped;
    private int total;

    public int getStarted() {
        return started;
    }

    public void setStarted(int started) {
        this.started = started;
    }

    public int getStopped() {
        return stopped;
    }

    public void setStopped(int stopped) {
        this.stopped = stopped;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
