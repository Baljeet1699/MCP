package com.MCP.AUTH_SERVICE.payloads;

import com.MCP.AUTH_SERVICE.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtAuthResponse {
    private String token;
    private User user;
}
