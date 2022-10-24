package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class TransactionService {

    public Map<String, Object> postTransaction(Transaction transaction){
        String result;
        if (transaction.getAmount() > 1500){
            result = "PROHIBITED";
        } else if (transaction.getAmount() > 200) {
            result = "MANUAL_PROCESSING";
        } else if (transaction.getAmount() > 0) {
            result = "ALLOWED";
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return Map.of("result", result);
    }
}
