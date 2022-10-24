package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Enum.Regions;
import com.example.anti_fraud_system.Model.Transaction;
import com.example.anti_fraud_system.Repository.StolenCardRepository;
import com.example.anti_fraud_system.Repository.SuspiciousIpRepository;
import com.example.anti_fraud_system.Repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    StolenCardRepository stolenCardRepository;
    @Autowired
    StolenCardsService stolenCardsService;
    @Autowired
    SuspiciousIpService suspiciousIpService;

    List<String> errors;
    String result;
    String info;

    public Map<String, Object> postTransaction(Transaction transaction){
        transactionRepository.save(transaction);
        errors = new LinkedList<>();

        verifyTransactionIp(transaction.getIp());
        verifyTransactionCard(transaction.getNumber());
        verifyTransactionRegion(transaction);
        verifyTransactionIpCorrelation(transaction);
        verifyTransactionAmount(transaction.getAmount());
        errors.sort((String::compareToIgnoreCase));
        info = errors.stream().map((e) -> e ).collect(Collectors.joining(", "));
        return Map.of("result", result, "info", info);
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

    public void verifyTransactionIp(String ip){
        if (!suspiciousIpService.verifyAddress(ip)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        if (suspiciousIpRepository.findSuspiciousIpByIp(ip).isPresent()){
            errors.add("ip");
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

    public void verifyTransactionRegion(Transaction transaction){
        List<Transaction> transactionsWithinLastHour = transactionRepository.findAllByDateBetween(transaction.getDate(),
                transaction.getDate().minusHours(1));
        List<Regions> regions = transactionsWithinLastHour.stream().map(Transaction::getRegion).filter((region)->!region.equals(transaction.getRegion())).distinct().collect(Collectors.toList());
        if (regions.size() == 2){
            result = "MANUAL_PROCESSING";
            errors.add("region-correlation");
        } else if (regions.size() > 2){
            result = "PROHIBITED";
            errors.add("region-correlation");
        }
    }

    public void verifyTransactionIpCorrelation(Transaction transaction){
        List<Transaction> transactionsWithinLastHour = transactionRepository.findAllByDateBetween(transaction.getDate(),
                transaction.getDate().minusHours(1));
        List<String> ips = transactionsWithinLastHour.stream().map((t) -> t.getIp()).filter((ip)-> !ip.equals(transaction.getIp())).distinct().collect(Collectors.toList());
        if (ips.size() == 2){
            result = "MANUAL_PROCESSING";
            errors.add("ip-correlation");
        } else if (ips.size() > 2){
            result = "PROHIBITED";
            errors.add("ip-correlation");
        }
        else if (!suspiciousIpService.verifyAddress(transaction.getIp())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

}
