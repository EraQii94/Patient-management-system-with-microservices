package com.pm.apigateway.filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class JwtValidationGatewayFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final WebClient webClient;

    /// Constructor to initialize WebClient with the authentication service URL
    public  JwtValidationGatewayFilterFactory(WebClient.Builder webClientBuilder,
                                              @Value("${auth.service.url}") String authServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(authServiceUrl).build();
    }

    /// Applies the filter logic to validate JWT tokens
    @Override
    public GatewayFilter apply(Object config) {
        return (exchange, chain) ->{
            // Extract the Authorization header
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");

            // If the token is missing or doesn't start with "Bearer ", respond with 401 Unauthorized
            if(token == null || !token.startsWith("Bearer ")){
                exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            // Call the auth service to validate the token
            return webClient.get()
                    .uri("/validate")
                    .header(HttpHeaders.AUTHORIZATION, token)
                    .retrieve()
                    .toBodilessEntity()
                    .then(chain.filter(exchange));
        };
    }
}
