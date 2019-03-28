package com.evbox.transaction.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.util.UUID;

@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Transaction {

    private String id = UUID.randomUUID().toString();

    private int stationId;

    private LocalDateTime startedAt = LocalDateTime.now();

    private TransactionStatus status = TransactionStatus.PROGRESS;

    private LocalDateTime endedAt;

    private int consumption;

    public Transaction(final int stationId) {
        Preconditions.checkArgument(stationId > 0, "Invalid station id");
        this.stationId = stationId;
    }

    public String getId() {
        return id;
    }

    @JsonIgnore
    public boolean isStopped() {
        return TransactionStatus.FINISHED.equals(status);
    }

    @JsonIgnore
    public boolean isStatus(final TransactionStatus status) {
        return this.status.equals(status);
    }

    @JsonIgnore
    public boolean changedInLastMinute() {
        final LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        return oneMinuteAgo.isBefore(startedAt) || (endedAt != null && oneMinuteAgo.isBefore(endedAt));
    }

    public void stop(final int consumption) {
        Preconditions.checkArgument(consumption > 0, "Invalid consumption");
        this.consumption = consumption;
        status = TransactionStatus.FINISHED;
        endedAt = LocalDateTime.now();
    }

}
