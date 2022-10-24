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

    public Map<String, Object> register(User user){
        if (repository.findUserByUsernameIgnoreCase(user.getUsername()).isPresent()){
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        }
        if (repository.findAll().size() == 0){
            user.setRole("ADMINISTRATOR");
            user.setAccountNonLocked(true);
        } else {
            user.setRole("MERCHANT");
            user.setAccountNonLocked(false);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repository.saveAndFlush(user);
        return Map.of("id", user.getId(), "username", user.getUsername(),
                "name", user.getName(), "role", user.getRole());
    }

    public List<Map<String, Object>> list(){
        if (repository.findAll().isEmpty()){
            return new ArrayList<>();
        }
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (User u: repository.findAll()){
            mapList.add(Map.of("id", u.getId(), "name", u.getName(),"username", u.getUsername(), "role", u.getRole()));
        }
        mapList.stream().sorted();
        return mapList;
    }

    public Map<String, Object> changeRole(Map<String, String> assignedRole){
        Optional<User> u = repository.findUserByUsernameIgnoreCase(assignedRole.get("username"));
        if (!u.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (assignedRole.get("role").equals(u.get().getRole())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT);
        } else if (assignedRole.get("role").equalsIgnoreCase("SUPPORT") || assignedRole.get("role").equalsIgnoreCase("MERCHANT")) {
            User updatedUser = u.get();
            updatedUser.setRole(assignedRole.get("role"));
            repository.saveAndFlush(updatedUser);
            return Map.of("id", updatedUser.getId(), "username", updatedUser.getUsername(),
                    "name", updatedUser.getName(), "role", updatedUser.getRole());
        }
        else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    public Map<String, String> changeStatus(Map<String, String> assignedStatus){
        Optional<User> u = repository.findUserByUsernameIgnoreCase(assignedStatus.get("username"));
        if (!u.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else if (u.get().getRole().equals("ADMINISTRATOR")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        } else {
            User user = u.get();
            if (assignedStatus.get("operation").equals("LOCK")){
                user.setAccountNonLocked(false);
            } else user.setAccountNonLocked(true);
            repository.saveAndFlush(user);
            String operation = assignedStatus.get("operation").equals("UNLOCK") ? "unlocked!" : "locked!";
            return Map.of("status", "User "+ assignedStatus.get("username") +" "+ operation);
        }
    }
    public Map<String, String> delete(String username){
        Optional<User> user = repository.findUserByUsernameIgnoreCase(username);
        if (!user.isPresent()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        repository.delete(user.get());
        return Map.of("username", username, "status", "Deleted successfully!");
    }

}
