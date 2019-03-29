package com.evbox.transaction.domain.service;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import com.evbox.transaction.domain.model.projection.TransactionDTO;
import com.evbox.transaction.domain.model.projection.TransactionStopDTO;
import com.evbox.transaction.domain.model.projection.TransactionTotalDTO;
import com.google.common.base.Preconditions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    //TODO: add persistence layer with a time shard / time series database (e.g. InfluxDB, ElasticSearch)
    private Map<String, Transaction> transactions = new ConcurrentHashMap<>();

    public Transaction create(final TransactionDTO transactionInfo) {
        Preconditions.checkNotNull(transactionInfo, "Transaction info cannot be null");
        final Transaction transaction = new Transaction(transactionInfo.getStationId());
        transactions.put(transaction.getId(), transaction);
        return transaction;
    }

    public Transaction stop(final String id, final TransactionStopDTO stopInfo) {
        Preconditions.checkNotNull(stopInfo, "Transaction stop info cannot be null");
        final Transaction transaction = Optional.ofNullable(transactions.get(id)).orElseThrow(() -> new EmptyResultDataAccessException(1));
        transaction.stop(stopInfo.getConsumption());
        return transaction;
    }

    public TransactionTotalDTO getAllInLastMinute() {
        final TransactionTotalDTO total = new TransactionTotalDTO();
        transactions.values().stream().filter(Transaction::changedInLastMinute).forEach(total::add);
        return total;
    }

    public List<Transaction> getByStatusInLastMinute(final TransactionStatus status) {
        return transactions.values().stream().filter(Transaction::changedInLastMinute)
                .filter(t -> t.isStatus(status)).collect(Collectors.toList());
    }

    public void deleteStopped() {
        final List<String> idsToRemote = transactions.entrySet().stream()
                .filter(e -> e.getValue().isStopped()).map(Map.Entry::getKey).collect(Collectors.toList());
        idsToRemote.forEach(i -> transactions.remove(i));
    }

}
