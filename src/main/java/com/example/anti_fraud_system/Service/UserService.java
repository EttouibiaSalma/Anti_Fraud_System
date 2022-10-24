package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.User;
import com.example.anti_fraud_system.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepository repository;
    @Autowired
    PasswordEncoder passwordEncoder;

    public User register(User user){
        if (repository.findUserByUsernameIgnoreCase(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return repository.saveAndFlush(user);
    }

    public List<User> list(){
        if (repository.findAll().isEmpty()){
            return new ArrayList<>();
        }
        List<User> l = repository.findAll();
        Collections.sort(l);
        return l;
    }

    public
    Map<String, String> delete(String username){
        Optional<User> user = repository.findUserByUsernameIgnoreCase(username);
        if (!user.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.delete(user.get());
        return Map.of("username", username, "status", "Deleted successfully!");
    }

}
