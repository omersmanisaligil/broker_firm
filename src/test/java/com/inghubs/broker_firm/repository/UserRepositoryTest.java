package com.inghubs.broker_firm.repository;

import com.inghubs.broker_firm.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.inghubs.broker_firm.enums.ROLE;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(includeFilters = @ComponentScan.Filter(type = FilterType.ANNOTATION, classes = Repository.class))
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static User admin;
    private static User customer1;
    private static User customer2;
    private static User customer3;


    @BeforeEach
    public void setUp(){
        admin = new User();
        admin.setId(UUID.randomUUID());
        admin.setUsername("admin1");
        admin.setPassword("admin345");
        admin.setRole(ROLE.ADMIN);

        customer1 = new User();
        customer1.setId(UUID.randomUUID());
        customer1.setUsername("cust1");
        customer1.setPassword("cust345");
        customer1.setRole(ROLE.CUSTOMER);

        customer2 = new User();
        customer2.setId(UUID.randomUUID());
        customer2.setUsername("cust2");
        customer2.setPassword("cust456");
        customer2.setRole(ROLE.CUSTOMER);

        customer3 = new User();
        customer3.setId(UUID.randomUUID());
        customer3.setUsername("cust3");
        customer3.setPassword("cust567");
        customer3.setRole(ROLE.CUSTOMER);

        List<User> users = Arrays.asList(admin,customer1,customer2,customer3);
        userRepository.saveAll(users);
    }

    @Test
    // validate that only customers are fetched
    public void testFindAllCustomers(){
        List<User> customers = userRepository.findAllCustomers();
        for (User user: customers){
            assertThat(user.getRole()).isEqualTo(ROLE.CUSTOMER);
        }
    }

}
