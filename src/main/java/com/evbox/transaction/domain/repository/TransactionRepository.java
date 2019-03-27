package com.evbox.transaction.domain.repository;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, String> {

    // TODO: change database to a time shard scheme, improving searches by date (e.g. ElasticSearch)
    List<Transaction> findByStartedAtAfterOrEndedAtAfter(LocalDateTime momentStart, LocalDateTime momentEnd);

    void deleteByStatus(TransactionStatus status);

}
