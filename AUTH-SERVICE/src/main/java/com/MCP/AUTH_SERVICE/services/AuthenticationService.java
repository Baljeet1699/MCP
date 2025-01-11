package com.MCP.AUTH_SERVICE.services;

import com.MCP.AUTH_SERVICE.entities.User;
import com.MCP.AUTH_SERVICE.exception.ApiException;
import com.MCP.AUTH_SERVICE.payloads.JwtAuthRequest;
import com.MCP.AUTH_SERVICE.payloads.JwtAuthResponse;
import com.MCP.AUTH_SERVICE.payloads.UserDto;
import com.MCP.AUTH_SERVICE.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    public UserDto saveUser(UserDto userDto){
        userDto.setPassword(this.passwordEncoder.encode(userDto.getPassword()));
        User user = this.userRepository.save(this.modelMapper.map(userDto, User.class));
        return this.modelMapper.map(user, UserDto.class);
    }

    public JwtAuthResponse generateToken(JwtAuthRequest jwtAuthRequest){
        this.authenticate(jwtAuthRequest.getUsername(), jwtAuthRequest.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(jwtAuthRequest.getUsername());
        String token = this.jwtService.generateToken(userDetails.getUsername());

        JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
        jwtAuthResponse.setToken(token);
        jwtAuthResponse.setUser((User) userDetails);

        return jwtAuthResponse;
    }

    private void authenticate(String username, String password){

        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(username,password);
        try{
            this.authenticationManager.authenticate(usernamePasswordAuthenticationToken);
        }catch (BadCredentialsException exception){
            throw new ApiException("Invalid username or password");
        }
    }
}
