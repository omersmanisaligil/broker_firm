package com.inghubs.broker_firm.repository;

import com.inghubs.broker_firm.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);

    @Query("""
            SELECT u FROM User u WHERE u.role = 'CUSTOMER'
        """)
    List<User> findAllCustomers();
}
