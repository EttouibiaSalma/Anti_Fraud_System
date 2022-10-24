package com.example.anti_fraud_system.Controller;

import com.example.anti_fraud_system.Model.User;
import com.example.anti_fraud_system.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping
public class RegistrationController {

    @Autowired
    UserService service;

    @PostMapping("/api/auth/user")
    public ResponseEntity<User> register(@RequestBody @Valid User user){
        return new ResponseEntity<>(service.register(user), HttpStatus.CREATED);
    }

    @GetMapping("/api/auth/list")
    public ResponseEntity<List<User>> listUsers(){
        return new ResponseEntity<>(service.list(), HttpStatus.OK);
    }

    @DeleteMapping("/api/auth/user/{username}")
    public ResponseEntity<Map<String, String>> delete(@PathVariable String username){

        return new ResponseEntity(service.delete(username), HttpStatus.OK);
    }
}
