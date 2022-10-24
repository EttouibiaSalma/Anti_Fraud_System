package com.example.anti_fraud_system.Model;

import javax.persistence.Entity;

@Entity
public class Transaction {
    Integer id;
    int amount;

    public Transaction() {
    }

    public Transaction(Integer id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
