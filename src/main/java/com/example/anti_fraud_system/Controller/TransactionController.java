package com.example.anti_fraud_system.Controller;

import com.example.anti_fraud_system.Model.Transaction;
import com.example.anti_fraud_system.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud")
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public ResponseEntity<Map<String, Object>> postTransaction(@RequestBody @Valid Transaction transaction){
        return new ResponseEntity<>(transactionService.postTransaction(transaction), HttpStatus.OK);
    }

}
