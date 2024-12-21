package com.admin.login.controller;

import com.admin.login.dto.SignDto;
import com.admin.login.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/signIn")
    public ResponseEntity<Boolean> signIn(@RequestBody SignDto signDto) {

        return ResponseEntity.ok(true);
    }

    @PostMapping(value = "/signUp")
    public ResponseEntity<Boolean> signUp(@RequestBody SignDto signDto) {


        authenticationService.signUp(signDto.getUsername(), signDto.getPassword());

        return ResponseEntity.ok(true);
    }
}
