package com.example.anti_fraud_system.Model;

import com.example.anti_fraud_system.Enum.Roles;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class User implements Comparable<User>{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    //Long provides us with the largest ID values
    //possible without the risk of possible overflows in the data.
    private Long id;
    @NotBlank(message = "username is mandatory")
    private String username;
    @NotBlank(message = "name is mandatory")
    private String name;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "password is mandatory")
    private String password;

    private Roles role;

    @JsonIgnore
    private boolean isAccountNonLocked = false;


    public User() {
    }

    public User(String username, String name, String password, Roles role) {
        this.username = username;
        this.name = name;
        this.password = password;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isAccountNonLocked() {
        return isAccountNonLocked;
    }

    public Roles getRole() {
        return role;
    }

    public void setRole(Roles role) {
        this.role = role;
    }

    public void setAccountNonLocked(boolean accountNonLocked) {
        isAccountNonLocked = accountNonLocked;
    }

    @Override
    public int compareTo(User u){
        return this.getId().compareTo(u.getId());
    }
}