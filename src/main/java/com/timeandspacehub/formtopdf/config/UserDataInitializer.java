package com.timeandspacehub.formtopdf.config;

import com.timeandspacehub.formtopdf.entity.Role;
import com.timeandspacehub.formtopdf.entity.User;
import com.timeandspacehub.formtopdf.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class UserDataInitializer {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository,
                                PasswordEncoder passwordEncoder) {

        return args -> {
            if (userRepository.count() == 0) {

                User user1 = new User();
                user1.setUsername("user1");
                user1.setPassword(passwordEncoder.encode("password1"));
                user1.setRoles(Set.of(Role.USER));

                User user2 = new User();
                user2.setUsername("user2");
                user2.setPassword(passwordEncoder.encode("password2"));
                user2.setRoles(Set.of(Role.USER));

                User admin = new User();
                admin.setUsername("admin1");
                admin.setPassword(passwordEncoder.encode("admin123"));
                admin.setRoles(Set.of(Role.ADMIN));

                User powerUser = new User();
                powerUser.setUsername("poweruser");
                powerUser.setPassword(passwordEncoder.encode("power123"));
                powerUser.setRoles(Set.of(Role.USER, Role.ADMIN));

                userRepository.save(user1);
                userRepository.save(user2);
                userRepository.save(admin);
                userRepository.save(powerUser);
            }
        };
    }
}
