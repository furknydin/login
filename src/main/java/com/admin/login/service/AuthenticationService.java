package com.admin.login.service;

import com.admin.login.entity.Users;
import com.admin.login.repository.UsersRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {
    private final UsersRepository usersRepository;

    public AuthenticationService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public void signUp(String username, String password) {
        String bcryptPassword = new BCryptPasswordEncoder().encode(password);
        Users user = new Users(username, bcryptPassword,true);

        usersRepository.save(user);
    }

    public Users signIn(String username, String password) {
        String bcryptPassword = new BCryptPasswordEncoder().encode(password);
        return usersRepository.findByUsernameAndPassword(username,bcryptPassword).orElse(null);
    }

}
