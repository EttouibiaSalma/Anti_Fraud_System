package com.example.anti_fraud_system.Repository;
import com.example.anti_fraud_system.Model.StolenCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface StolenCardRepository extends JpaRepository<StolenCard, Long> {

    Optional<StolenCard> findStolenCardByNumber(String number);
}
