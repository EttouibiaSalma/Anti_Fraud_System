package com.example.anti_fraud_system.Controller;

import com.example.anti_fraud_system.Model.SuspiciousIp;
import com.example.anti_fraud_system.Service.SuspiciousIpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/antifraud/suspicious-ip")
public class SuspiciousIpController {

    @Autowired
    SuspiciousIpService ipService;

    @GetMapping
    public ResponseEntity<List<SuspiciousIp>> listAddresses (){
        return new ResponseEntity<>(ipService.listSuspiciousIpAddresses(), HttpStatus.OK);
    }
    @PostMapping
    public ResponseEntity<SuspiciousIp> addAddress(@RequestBody @Valid SuspiciousIp ip){
        return new ResponseEntity(ipService.addAddress(ip), HttpStatus.OK);
    }

    @DeleteMapping("/{ip}")
    public ResponseEntity<Map<String, String>> deleteAddress(@PathVariable String ip){
        return new ResponseEntity<>(ipService.delete(ip), HttpStatus.OK);
    }

}

