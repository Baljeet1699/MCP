package com.MCP.AUTH_SERVICE.services;

import com.MCP.AUTH_SERVICE.entities.User;
import com.MCP.AUTH_SERVICE.exception.ResourceNotFoundException;
import com.MCP.AUTH_SERVICE.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(username).orElseThrow(() -> new ResourceNotFoundException("User","email "+username,0));
        return user;
    }

}
