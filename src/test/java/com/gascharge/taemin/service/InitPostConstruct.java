package com.gascharge.taemin.service;

import com.gascharge.taemin.domain.entity.user.User;
import com.gascharge.taemin.domain.entity.user.UserTestData;
import com.gascharge.taemin.domain.repository.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.gascharge.taemin.domain.entity.user.UserTestData.ADMIN_TEST_EMAIL;
import static com.gascharge.taemin.domain.entity.user.UserTestData.USER_TEST_EMAIL;

@Component
@Transactional
@Profile("test")
public class InitPostConstruct {
    @Autowired
    private UserRepository userRepository;
    @EventListener(ApplicationReadyEvent.class)
    public void initTestValue() {
        Optional<User> byEmail = userRepository.findByEmail(USER_TEST_EMAIL);

        User user = null;

        if (byEmail.isEmpty()) {
            user = UserTestData.getCloneUser();
            userRepository.save(user);
        } else {
            user = byEmail.get();
        }

        Optional<User> byEmail1 = userRepository.findByEmail(ADMIN_TEST_EMAIL);

        User admin = null;

        if (byEmail1.isEmpty()) {
            admin = UserTestData.getCloneAdmin();
            userRepository.save(admin);
        } else {
            admin = byEmail1.get();
        }
    }
}
