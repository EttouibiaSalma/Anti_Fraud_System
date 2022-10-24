package com.example.anti_fraud_system.Repository;

import com.example.anti_fraud_system.Model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@EnableJpaRepositories
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findAllByDateBetween(LocalDateTime dateTimeStart, LocalDateTime dateTimeEnd);
}
}
