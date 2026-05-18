package com.silicon.app.controllers;

import com.silicon.app.models.AuthResponse;
import com.silicon.app.models.entities.Role;
import com.silicon.app.models.entities.User;
import com.silicon.app.security.JWTCore;
import com.silicon.app.repositories.UserRepository;
import com.silicon.app.models.requests.SignupRequest;
import com.silicon.app.models.requests.SigninRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTCore jwtCore;

    public SecurityController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager, JWTCore jwtCore) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtCore = jwtCore;
    }

    @PostMapping("/signup")
    ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        if (userRepository.existsByUsername(signupRequest.getUsername())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username is already taken");
        }
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
        }

        String hashed = passwordEncoder.encode(signupRequest.getPassword());

        User user = new User();
        user.setUsername(signupRequest.getUsername());
        user.setEmail(signupRequest.getEmail());
        user.setPassword(hashed);
        user.setRole(Role.ROLE_USER);
        userRepository.save(user);

        return ResponseEntity.ok(user);

    }

    @PostMapping("/signin")
    ResponseEntity<?> signin(@RequestBody SigninRequest signinRequest) {
        Authentication auth;
        try {
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signinRequest.getUsername(), signinRequest.getPassword())
            );
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
        SecurityContextHolder.getContext().setAuthentication(auth);
        String jwt = jwtCore.generateToken(auth);
        User user = userRepository.findByUsername(signinRequest.getUsername()).orElseThrow();
        return ResponseEntity.ok(new AuthResponse(jwt, user.getRole().toString(), user.getUsername()));
    }
}
