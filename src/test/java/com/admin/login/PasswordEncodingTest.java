package com.admin.login;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.DigestUtils;

class PasswordEncodingTest {

    static final String PASSWORD = "password";

    @Test
    void hashPassword() {
        System.out.println(DigestUtils.md5DigestAsHex(PASSWORD.getBytes()));
    }

    @Test
    void testNoOp() {
        PasswordEncoder passwordEncoder = NoOpPasswordEncoder.getInstance();
        System.out.println(passwordEncoder.encode(PASSWORD));

    }
}
