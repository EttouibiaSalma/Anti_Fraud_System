package com.example.anti_fraud_system.Service;

import com.example.anti_fraud_system.Model.User;
import com.example.anti_fraud_system.Model.UserDetailsImpl;
import com.example.anti_fraud_system.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    UserRepository repository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = repository.findUserByUsernameIgnoreCase(username);
        return new UserDetailsImpl(user.get());
    }
}
