package com.evbox.transaction.domain.model.projection;

import com.evbox.transaction.domain.model.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class TransactionTotalDTO {

    private int started = 0;
    private int stopped = 0;
    private int total = 0;

    public int getStarted() {
        return started;
    }

    public int getStopped() {
        return stopped;
    }

    public int getTotal() {
        return total;
    }

    @JsonIgnore
    public void add(final Transaction transaction) {
        total++;
        // brackets omitted to avoid new stack context
        if (transaction.isStopped()) stopped++;
        else started++;
    }

}
