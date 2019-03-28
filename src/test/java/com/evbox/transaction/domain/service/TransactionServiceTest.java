package com.evbox.transaction.domain.service;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import com.evbox.transaction.domain.model.projection.TransactionDTO;
import com.evbox.transaction.domain.model.projection.TransactionStopDTO;
import com.evbox.transaction.domain.model.projection.TransactionTotalDTO;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

public class TransactionServiceTest {

    private TransactionService service;

    private TransactionDTO transactionInfo = new TransactionDTO();

    private TransactionStopDTO stopInfo = new TransactionStopDTO();

    @Before
    public void setUp() {
        ReflectionTestUtils.setField(transactionInfo, "stationId", 1);
        ReflectionTestUtils.setField(stopInfo, "consumption", 20);
        service = new TransactionService();
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
        Transaction transaction = service.create(transactionInfo);
        transaction = service.stop(transaction.getId(), stopInfo);
        Assert.assertTrue(transaction.isStopped());
    }

    @Test
    public void getAllInLastMinute() {
        service.create(transactionInfo);
        Transaction transaction = service.create(transactionInfo);
        service.stop(transaction.getId(), stopInfo);
        final TransactionTotalDTO total = service.getAllInLastMinute();
        Assert.assertEquals(1, total.getStarted());
        Assert.assertEquals(1, total.getStopped());
        Assert.assertEquals(2, total.getTotal());
    }

    @Test
    public void getByStatusInLastMinute() {
        service.create(transactionInfo);
        Transaction transaction = service.create(transactionInfo);
        service.stop(transaction.getId(), stopInfo);

        List<Transaction> transactions = service.getByStatusInLastMinute(TransactionStatus.PROGRESS);
        Assert.assertEquals(1, transactions.size());

        transactions = service.getByStatusInLastMinute(TransactionStatus.FINISHED);
        Assert.assertEquals(1, transactions.size());
    }

}