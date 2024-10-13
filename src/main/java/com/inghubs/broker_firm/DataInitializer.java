package com.inghubs.broker_firm;

import com.inghubs.broker_firm.entity.User;
import com.inghubs.broker_firm.enums.ROLE;
import com.inghubs.broker_firm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername("admin").isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(bCryptPasswordEncoder.encode("admin123"));

            adminUser.setRole(ROLE.ADMIN);
            userRepository.save(adminUser);
            System.out.println("Admin Customer created: " + adminUser.getUsername());
        }
        if (userRepository.findByUsername("cust1").isEmpty()){
           User user = new User();
           user.setUsername("cust1");
           user.setPassword(bCryptPasswordEncoder.encode("cust123"));

           user.setRole(ROLE.CUSTOMER);
           userRepository.save(user);
        }
    }
}
