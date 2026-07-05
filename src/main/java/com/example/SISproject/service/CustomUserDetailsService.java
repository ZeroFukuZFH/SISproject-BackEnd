package com.example.SISproject.service;

import com.example.SISproject.models.UserModel;
import com.example.SISproject.repositories.UserRepository;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// THIS CLASS RUNS ONLY ONCE EVER AFTER AUTHENTICATION
// IT DIRECTLY REPLACES THE STANDARD USER DETAILS SERVICE

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserModel userModel = userRepository.findUserByEmail(username);

        if (userModel == null) {
            throw new UsernameNotFoundException("USER NOT FOUND WITH USERNAME: " + username);
        }

        return User.builder()
                .username(userModel.getEmail())
                .password(userModel.getPassword())
                .roles("USER")
                .build();
    }
}