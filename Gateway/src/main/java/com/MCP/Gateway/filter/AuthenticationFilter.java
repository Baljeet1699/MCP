package com.MCP.Gateway.filter;


import com.MCP.Gateway.utils.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class AuthenticationFilter extends AbstractGatewayFilterFactory<AuthenticationFilter.Config> {

    public AuthenticationFilter() {
        super(Config.class);
    }
    @Autowired
    private RouteValidator routeValidator;

    @Autowired
    private JwtUtil jwtUtil;

    private Logger logger = LoggerFactory.getLogger(AuthenticationFilter.class);

    @Override
    public GatewayFilter apply(Config config) {

        return ((exchange, chain) -> {
            ServerHttpRequest request = null;
            if (routeValidator.isSecured.test(exchange.getRequest())){

                if(!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
                    throw new RuntimeException("missing authorization header");
                }

                String requestHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
                logger.info("Header : {}",requestHeader);
                String username = null;
                String token = null;

                if(requestHeader != null && requestHeader.startsWith("Bearer")){

                    token = requestHeader.substring(7);

                    try {
                        username = jwtUtil.getUsernameFromToken(token);
                    }catch (IllegalArgumentException exception){
                        logger.info("IllegalArgument while fetching the username !!");
                        exception.printStackTrace();
                    }catch (ExpiredJwtException exception){
                        logger.info("Jwt token is expired");
                        exception.printStackTrace();
                    }catch (MalformedJwtException exception){
                        logger.info("Some changes done to token, Invalid token!!");
                        exception.printStackTrace();
                    }catch (Exception exception){
                        exception.printStackTrace();
                    }
                }else {
                    logger.info("Invalid token !!");
                }

                if(username != null && SecurityContextHolder.getContext().getAuthentication() == null){

                    try {
                        this.jwtUtil.validateToken(token);

                        request = exchange.getRequest()
                                .mutate()
                                .header("loggedInUser",username)
                                .build();

//                        //set the authentication
//                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//                        SecurityContextHolder.getContext().setAuthentication(authentication);

                    }catch (Exception exception){
                        logger.info("Validation fails!!");
                        throw new RuntimeException("Un authorized access to application");
                    }
                }

            }

            return chain.filter(exchange.mutate().request(request).build());
        });
    }

    public static class Config {

    }

}