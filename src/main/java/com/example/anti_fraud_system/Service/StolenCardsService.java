package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.StolenCard;
import com.example.anti_fraud_system.Repository.StolenCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class StolenCardsService {

    @Autowired
    StolenCardRepository cardRepository;

    public List<StolenCard> stolenCardList(){
        if (cardRepository.findAll().isEmpty()){
            return new ArrayList<>();
        }
        List<StolenCard> list = cardRepository.findAll();
        Collections.sort(list);
        return list;
    }

    public StolenCard addStolenCard(StolenCard card){
        Optional<StolenCard> stolenCardOptional = cardRepository.findStolenCardByNumber(card.getNumber());
        if (stolenCardOptional.isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (!verifyCardNumber(card.getNumber())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return cardRepository.save(card);
    }

    public boolean verifyCardNumber(String cardNumber){
        int nDigits = cardNumber.length();

        int nSum = 0;
        boolean isSecond = false;
        for (int i = nDigits - 1; i >= 0; i--)
        {

            int d = cardNumber.charAt(i) - '0';

            if (isSecond)
                d = d * 2;
            nSum += d / 10;
            nSum += d % 10;

            isSecond = !isSecond;
        }
        return (nSum % 10 == 0);
    }

    public Map<String, String> deleteCardNumber(String number){
        Optional<StolenCard> card = cardRepository.findStolenCardByNumber(number);
        if (!verifyCardNumber(number)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if (!card.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        cardRepository.delete(card.get());
        return Map.of("status", "Card " +number + " successfully removed!");
    }
}
