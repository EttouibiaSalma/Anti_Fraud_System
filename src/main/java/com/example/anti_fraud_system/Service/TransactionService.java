package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.Transaction;
import com.example.anti_fraud_system.Repository.StolenCardRepository;
import com.example.anti_fraud_system.Repository.SuspiciousIpRepository;
import com.example.anti_fraud_system.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;
    @Autowired
    SuspiciousIpService suspiciousIpService;

    @Autowired
    StolenCardRepository stolenCardRepository;
    @Autowired
    StolenCardsService stolenCardsService;

    List<String> errors;
    String result;
    String info;

    public Map<String, Object> postTransaction(Transaction transaction){
        transactionRepository.save(transaction);
        errors = new LinkedList<>();

        verifyTransactionIp(transaction.getIp());
        verifyTransactionCard(transaction.getNumber());
        verifyTransactionAmount(transaction.getAmount());
        errors.sort((String::compareToIgnoreCase));
        info = errors.stream().map((e) -> e ).collect(Collectors.joining(", "));
        return Map.of("result", result, "info", info);
    }

    public void verifyTransactionIp(String ip){
        if (!suspiciousIpService.verifyAddress(ip)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (suspiciousIpRepository.findSuspiciousIpByIp(ip).isPresent()){
            errors.add("ip");
            result = "PROHIBITED";
        }
    }

    public void verifyTransactionCard(String cardNum){
        if (!stolenCardsService.verifyCardNumber(cardNum)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if (stolenCardRepository.findStolenCardByNumber(cardNum).isPresent()){
            errors.add("card-number");
            result = "PROHIBITED";
        }
    }

    public void verifyTransactionAmount(Long amount){
        if (amount == null || amount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (amount > 1500) {
            errors.add("amount");
            result = "PROHIBITED";
        } else if (amount > 200 && errors.size() < 1) {
            errors.add("amount");
            result = result.equals("PROHIBITED") ? "PROHIBITED" : "MANUAL_PROCESSING";
        } else if (errors.size() < 1) {
            result = "ALLOWED";
            errors.add("none");
        }
    }
}
