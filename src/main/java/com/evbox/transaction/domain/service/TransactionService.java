package com.evbox.transaction.domain.service;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import com.evbox.transaction.domain.model.projection.TransactionDTO;
import com.evbox.transaction.domain.model.projection.TransactionStopDTO;
import com.evbox.transaction.domain.model.projection.TransactionTotalDTO;
import com.evbox.transaction.domain.repository.TransactionRepository;
import com.google.common.base.Preconditions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    private TransactionRepository repository;

    @Transactional
    public Transaction create(final TransactionDTO transactionInfo) {
        Preconditions.checkNotNull(transactionInfo, "Transaction info cannot be null");
        final Transaction transaction = new Transaction(transactionInfo.getStationId());
        return repository.save(transaction);
    }

    @Transactional
    public Transaction stop(final String id, final TransactionStopDTO stopInfo) {
        Preconditions.checkNotNull(stopInfo, "Transaction stop info cannot be null");
        final Transaction transaction = repository.findById(id).orElseThrow(() -> new EmptyResultDataAccessException(1));
        transaction.stop(stopInfo.getConsumption());
        return repository.save(transaction);
    }

    @Transactional(readOnly = true)
    public TransactionTotalDTO getAllInLastMinute() {
        final LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        final List<Transaction> transactions = repository.findByStartedAtAfterOrEndedAtAfter(oneMinuteAgo, oneMinuteAgo);
        final TransactionTotalDTO total = new TransactionTotalDTO();
        total.setTotal(transactions.size());
        total.setStopped((int) transactions.stream().filter(Transaction::isStopped).count());
        total.setStarted(total.getTotal() - total.getStopped());
        return total;
    }

    @Transactional(readOnly = true)
    public List<Transaction> getByStatusInLastMinute(final TransactionStatus status) {
        final LocalDateTime oneMinuteAgo = LocalDateTime.now().minusMinutes(1);
        final List<Transaction> transactions = repository.findByStartedAtAfterOrEndedAtAfter(oneMinuteAgo, oneMinuteAgo);
        return transactions.stream().filter(t -> t.isStatus(status)).collect(Collectors.toList());
    }

    @Transactional
    public void deleteStopped() {
        repository.deleteByStatus(TransactionStatus.FINISHED);
    }

}
