package com.evbox.transaction.controller;

import com.evbox.transaction.domain.model.Transaction;
import com.evbox.transaction.domain.model.TransactionStatus;
import com.evbox.transaction.domain.model.projection.TransactionDTO;
import com.evbox.transaction.domain.model.projection.TransactionStopDTO;
import com.evbox.transaction.domain.model.projection.TransactionTotalDTO;
import com.evbox.transaction.domain.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
public class TransactionController {

    private static final String BASE_URI = "/transactions";

    @Autowired
    private TransactionService service;

    @PostMapping(value = BASE_URI)
    public ResponseEntity<Transaction> create(@RequestBody final TransactionDTO body) {
        final Transaction transaction = service.create(body);
        return ResponseEntity.created(URI.create(BASE_URI + transaction.getId())).body(transaction);
    }

    @PutMapping(value = BASE_URI + "/{id}")
    public ResponseEntity<Transaction> stop(@PathVariable("id") final String id, @RequestBody final TransactionStopDTO body) {
        final Transaction transaction = service.stop(id, body);
        return ResponseEntity.ok(transaction);
    }

    @GetMapping(value = BASE_URI)
    public TransactionTotalDTO getAllInLastMinute() {
        return service.getAllInLastMinute();
    }

    @GetMapping(value = BASE_URI + "/stopped")
    public List<Transaction> getStoppedInLastMinute() {
        return service.getByStatusInLastMinute(TransactionStatus.FINISHED);
    }

    @GetMapping(value = BASE_URI + "/started")
    public List<Transaction> getStartedInLastMinute() {
        return service.getByStatusInLastMinute(TransactionStatus.PROGRESS);
    }

    @DeleteMapping(value = BASE_URI)
    public ResponseEntity deleteStopped() {
        service.deleteStopped();
        return ResponseEntity.noContent().build();
    }

}
