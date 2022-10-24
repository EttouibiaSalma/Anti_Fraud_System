package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.SuspiciousIp;
import com.example.anti_fraud_system.Repository.SuspiciousIpRepository;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class SuspiciousIpService {

    @Autowired
    SuspiciousIpRepository suspiciousIpRepository;

    public List<SuspiciousIp> listSuspiciousIpAddresses(){
        if (suspiciousIpRepository.findAll().isEmpty()){
            return new ArrayList<>();
        }
        List<SuspiciousIp> sortedAddresses = suspiciousIpRepository.findAll();
        Collections.sort(sortedAddresses);
        return sortedAddresses;
    }

    public SuspiciousIp addAddress( SuspiciousIp ipAddress){
        Optional<SuspiciousIp> ip = suspiciousIpRepository.findSuspiciousIpByIp(ipAddress.getIp());
        if (ip.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (!verifyAddress(ipAddress.getIp())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return suspiciousIpRepository.save(ipAddress);
    }

    public Map<String, String> delete(String suspiciousIp){
        Optional<SuspiciousIp> ip = suspiciousIpRepository.findSuspiciousIpByIp(suspiciousIp);
        if (!verifyAddress(suspiciousIp)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        else if (!ip.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        suspiciousIpRepository.delete(ip.get());
        return Map.of("status", "IP " + suspiciousIp + " successfully removed!");
    }

    public boolean verifyAddress(String ipAddress){
        InetAddressValidator validator = InetAddressValidator.getInstance();
        if (validator.isValidInet4Address(ipAddress)) {
            return true;
        }
        else {
            return false;
        }
    }

}
