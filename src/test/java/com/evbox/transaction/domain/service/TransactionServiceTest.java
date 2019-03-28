package com.evbox.transaction.domain.service;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import com.evbox.transaction.domain.model.projection.TransactionDTO;
import com.evbox.transaction.domain.model.projection.TransactionStopDTO;
import com.evbox.transaction.domain.model.projection.TransactionTotalDTO;
import com.evbox.transaction.domain.repository.TransactionRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.AdditionalAnswers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TransactionServiceTest {

    @Mock
    private TransactionRepository repository;

    private TransactionService service = new TransactionService();

    private Transaction transaction = new Transaction(1);

    private TransactionDTO transactionInfo = new TransactionDTO();

    private TransactionStopDTO stopInfo = new TransactionStopDTO();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(service, "repository", repository);
        ReflectionTestUtils.setField(transactionInfo, "stationId", 1);
        ReflectionTestUtils.setField(stopInfo, "consumption", 20);

        Mockito.when(repository.save(Mockito.any(Transaction.class))).then(AdditionalAnswers.returnsFirstArg());
        Mockito.when(repository.findById("1")).thenReturn(Optional.of(transaction));
        Mockito.when(repository.findById("2")).thenReturn(Optional.empty());
        Mockito.when(repository.findByStartedAtAfterOrEndedAtAfter(Mockito.any(LocalDateTime.class), Mockito.any(LocalDateTime.class)))
                .thenReturn(Collections.singletonList(new Transaction(10)));
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotCreateWithNoTransactionInfo() {
        service.create(null);
    }

    @Test
    public void create() {
        final Transaction transaction = service.create(transactionInfo);
        Assert.assertFalse(transaction.isStopped());
    }

    @Test(expected = NullPointerException.class)
    public void shouldNotStopWithNoStopInfo() {
        service.stop("1", null);
    }

    @Test(expected = EmptyResultDataAccessException.class)
    public void shouldNotStopWithInvalidTransaction() {
        service.stop("2", stopInfo);
    }

    @Test
    public void stop() {
        final Transaction transaction = service.stop("1", stopInfo);
        Assert.assertTrue(transaction.isStopped());
    }

    @Test
    public void getAllInLastMinute() {
        final TransactionTotalDTO total = service.getAllInLastMinute();
        Assert.assertEquals(1, total.getStarted());
        Assert.assertEquals(0, total.getStopped());
        Assert.assertEquals(1, total.getTotal());
    }

    @Test
    public void getByStatusInLastMinute() {
        List<Transaction> transactions = service.getByStatusInLastMinute(TransactionStatus.PROGRESS);
        Assert.assertEquals(1, transactions.size());

        transactions = service.getByStatusInLastMinute(TransactionStatus.FINISHED);
        Assert.assertTrue(transactions.isEmpty());
    }

}