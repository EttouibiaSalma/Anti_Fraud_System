package com.example.anti_fraud_system.Repository;

import com.example.anti_fraud_system.Model.SuspiciousIp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@EnableJpaRepositories
public interface SuspiciousIpRepository extends JpaRepository<SuspiciousIp, Long> {
    Optional<SuspiciousIp> findSuspiciousIpByIp(String ip);
}
