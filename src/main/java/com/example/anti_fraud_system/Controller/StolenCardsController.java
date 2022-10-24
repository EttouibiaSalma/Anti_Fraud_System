package com.example.anti_fraud_system.Controller;

import com.example.anti_fraud_system.Model.StolenCard;
import com.example.anti_fraud_system.Service.StolenCardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
public class StolenCardsController {

    @Autowired
    StolenCardsService cardsService;

    @GetMapping("/api/antifraud/stolencard")
    public ResponseEntity<List<StolenCard>> listStolenCards(){
        return new ResponseEntity<>(cardsService.stolenCardList(), HttpStatus.OK);
    }

    @PostMapping("/api/antifraud/stolencard")
    public ResponseEntity<StolenCard> postStolenCard(@RequestBody @Valid StolenCard card){
        return new ResponseEntity<>(cardsService.addStolenCard(card), HttpStatus.OK);
    }

    @DeleteMapping("/api/antifraud/stolencard/{cardNumber}")
    public ResponseEntity<Map<String, String>> deleteCard(@PathVariable String cardNumber){
        return new ResponseEntity<>(cardsService.deleteCardNumber(cardNumber), HttpStatus.OK);
    }
}