package com.MCP.AUTH_SERVICE.controllers;

import com.MCP.AUTH_SERVICE.payloads.JwtAuthRequest;
import com.MCP.AUTH_SERVICE.payloads.JwtAuthResponse;
import com.MCP.AUTH_SERVICE.payloads.UserDto;
import com.MCP.AUTH_SERVICE.services.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto){
        UserDto savedUserDto = this.authenticationService.saveUser(userDto);
        return new ResponseEntity<>(userDto, HttpStatus.CREATED);
    }


    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponse> getToken(@RequestBody JwtAuthRequest jwtAuthRequest){
        JwtAuthResponse jwtAuthResponse = this.authenticationService.generateToken(jwtAuthRequest);
        return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
    }


}
