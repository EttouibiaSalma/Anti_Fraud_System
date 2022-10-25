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

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    Long MAX_ALLOWED = 200L;
    Long MAX_MANUAL_PROCESSING = 1500L;

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

    private void verifyTransactionAmount(Long amount){
        if (amount == null || amount < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (amount > MAX_MANUAL_PROCESSING) {
            errors.add("amount");
            result = "PROHIBITED";
        } else if (amount > MAX_ALLOWED && errors.size() < 1) {
            errors.add("amount");
            result = "MANUAL_PROCESSING";
        } else if (errors.size() < 1 ) {
            result = "ALLOWED";
            errors.add("none");
        }
    }

    private void verifyTransactionRegion(Transaction transaction){
        List<Transaction> transactionsWithinLastHour = transactionRepository.findByNumberAndDateBetween(transaction.getNumber(),
                transaction.getDate().minusHours(1), transaction.getDate());
        List<Regions> regions = transactionsWithinLastHour.stream().map(Transaction::getRegion).filter((region)->!region.equals(transaction.getRegion())).distinct().collect(Collectors.toList());
        if (regions.size() == 2){
            result = "MANUAL_PROCESSING";
            errors.add("region-correlation");
        } else if (regions.size() > 2){
            result = "PROHIBITED";
            errors.add("region-correlation");
        }
    }

    private void verifyTransactionIpCorrelation(Transaction transaction){
        List<Transaction> transactionsWithinLastHour = transactionRepository.findByNumberAndDateBetween(transaction.getNumber()
                , transaction.getDate().minusHours(1), transaction.getDate());
        List<String> ips = transactionsWithinLastHour.stream().map(Transaction::getIp).filter((ip)-> !ip.equals(transaction.getIp())).distinct().collect(Collectors.toList());
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

    public Transaction addTransactionFeedback(Map<String, String> feedback) {
        Transaction transaction = transactionRepository.findById(Long.valueOf(feedback.get("transactionId")))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!feedback.get("feedback").equals("MANUAL_PROCESSING")
                && !feedback.get("feedback").equals("ALLOWED")
                && !feedback.get("feedback").equals("PROHIBITED")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else if (transaction.getResult().equals(feedback.get("feedback"))) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        } else if (!transaction.getFeedback().isBlank() || transaction.getFeedback().equals(feedback.get("feedback"))){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else {
            if ((feedback.get("feedback").equals("ALLOWED") && transaction.getResult().equals("MANUAL_PROCESSING")) ||
                    (feedback.get("feedback").equals("ALLOWED") && transaction.getResult().equals("PROHIBITED"))){
                MAX_ALLOWED = increaseNewLimit(transaction.getAmount(), MAX_ALLOWED);
            } else if ((transaction.getResult().equals("ALLOWED") && feedback.get("feedback").equals("MANUAL_PROCESSING")) ||
                    (transaction.getResult().equals("ALLOWED") && feedback.get("feedback").equals("PROHIBITED"))) {
                MAX_ALLOWED = decreaseNewLimit(transaction.getAmount(), MAX_ALLOWED);
            } else if ((transaction.getResult().equals("PROHIBITED") && feedback.get("feedback").equals("ALLOWED")) ||
                    (transaction.getResult().equals("PROHIBITED") && feedback.get("feedback").equals("MANUAL_PROCESSING"))) {
                MAX_MANUAL_PROCESSING = increaseNewLimit(transaction.getAmount(), MAX_MANUAL_PROCESSING);
            } else if ( (feedback.get("feedback").equals("PROHIBITED") && transaction.getResult().equals("ALLOWED"))
                    || (feedback.get("feedback").equals("PROHIBITED") && transaction.getResult().equals("MANUAL_PROCESSING")) ){
                MAX_MANUAL_PROCESSING = decreaseNewLimit(transaction.getAmount(), MAX_MANUAL_PROCESSING);
            }
            transaction.setFeedback(feedback.get("feedback"));
            transactionRepository.save(transaction);
            return transaction;
        }
    }

    private Long increaseNewLimit(Long amount, Long limit){
        return (long) Math.ceil(0.8 * limit + 0.2 * amount);
    }

    private Long decreaseNewLimit(Long amount, Long limit){
        return (long) Math.ceil(0.8 * limit - 0.2 * amount);
    }

    public List<Transaction> getAllTransactions() {
        if (transactionRepository.findAll().size() == 0){
            return new ArrayList<>();
        }
        List<Transaction> transactionList = transactionRepository.findAll();
        Collections.sort(transactionList);
        return transactionList;
    }

    public List<Transaction> getTransactionsByCardNumber(String cardNumber) {
        if (!stolenCardsService.verifyCardNumber(cardNumber)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        List<Transaction> transactions = transactionRepository.findAllByNumber(cardNumber);
        if (transactions.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return transactions;
    }
}
