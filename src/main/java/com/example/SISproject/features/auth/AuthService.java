package com.example.SISproject.features.auth;

import com.example.SISproject.features.auth.dto.MeResponse;
import com.example.SISproject.models.UserModel;
import com.example.SISproject.repositories.UserRepository;
import com.example.SISproject.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public String login(String email, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            return jwtService.generateToken(authentication.getName());

        } catch (AuthenticationException e) {
            return null;
        }
    }

    public MeResponse me(String email){
        UserModel user = userRepository.findUserByEmail(email);
        if (user == null) return null;
        return new MeResponse(
                user.getName(),
                user.getEmail(),
                user.getActivityStatus()
        );
    }

    public String register(String username, String email, String password) {
        if (userRepository.findUserByEmail(email) != null) {
            return null;
        }
        String hashedPassword = passwordEncoder.encode(password);
        userRepository.addNewUser(username,email,hashedPassword);
        return jwtService.generateToken(email);
    }

}
