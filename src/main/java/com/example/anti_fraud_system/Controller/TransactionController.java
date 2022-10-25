package com.example.anti_fraud_system.Controller;

import com.example.anti_fraud_system.Model.Transaction;
import com.example.anti_fraud_system.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
class TransactionController{

    @Autowired
    TransactionService transactionService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> postTransaction(@RequestBody @Valid Transaction transaction){
        return new ResponseEntity<>(transactionService.postTransaction(transaction), HttpStatus.OK);
    }

    @PutMapping("/transaction")
    public ResponseEntity<Transaction> addFeedback(@RequestBody @Valid Map<String, String> feedback){
        return new ResponseEntity<>(transactionService.addTransactionFeedback(feedback), HttpStatus.OK);
    }

    @GetMapping("/history")
    public ResponseEntity<List<Transaction>> getTransactionHistory(){
        return new ResponseEntity<>(transactionService.getAllTransactions(), HttpStatus.OK);
    }

    @GetMapping("/history/{number}")
    public ResponseEntity<List<Transaction>> getTransactionHistoryByCardNumber(@PathVariable String number){
        return new ResponseEntity<>(transactionService.getTransactionsByCardNumber(number), HttpStatus.OK);
    }

}
