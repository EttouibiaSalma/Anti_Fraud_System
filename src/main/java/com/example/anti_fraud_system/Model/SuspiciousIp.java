package com.example.anti_fraud_system.Model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class SuspiciousIp implements Comparable<SuspiciousIp>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank
    private String ip;

    public SuspiciousIp() {
    }

    public SuspiciousIp(Long id, String ip) {
        this.id = id;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public int compareTo(SuspiciousIp suspiciousIp){
        return this.getId().compareTo(suspiciousIp.getId());
    }
}
