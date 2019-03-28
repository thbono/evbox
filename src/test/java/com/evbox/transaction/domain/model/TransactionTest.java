package com.evbox.transaction.domain.model;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TransactionTest {

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotCreateWithInvalidStationId() {
        new Transaction(0);
    }

    @Test
    public void create() {
        final Transaction transaction = new Transaction(1);
        assertFalse(transaction.isStopped());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldNotStopWithInvalidConsumption() {
        final Transaction transaction = new Transaction(1);
        transaction.stop(0);
    }

    @Test
    public void stop() {
        final Transaction transaction = new Transaction(1);
        transaction.stop(20);
        assertTrue(transaction.isStopped());
    }

}