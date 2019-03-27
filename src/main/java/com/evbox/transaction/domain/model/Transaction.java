package com.evbox.transaction.domain.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Preconditions;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Transaction {

    @Id
    private String id = UUID.randomUUID().toString();

    private int stationId;

    private LocalDateTime startedAt = LocalDateTime.now();

    private TransactionStatus status = TransactionStatus.PROGRESS;

    private LocalDateTime endedAt;

    private int consumption;

    private Transaction() {
    }

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

    public void stop(final int consumption) {
        Preconditions.checkArgument(consumption > 0, "Invalid consumption");
        this.consumption = consumption;
        status = TransactionStatus.FINISHED;
        endedAt = LocalDateTime.now();
    }

}
