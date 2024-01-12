package com.sharifyy.javatalkskeycloak;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


	@Bean
	SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

		JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthorityConverter());

		return
			http.authorizeHttpRequests(customizer ->
						customizer
							.requestMatchers("/token").permitAll()
							.requestMatchers(HttpMethod.GET, "/books").hasRole("book_reader")
							.requestMatchers(HttpMethod.POST, "/books").hasRole("book_writer")
							.requestMatchers(HttpMethod.GET, "/profile").hasAuthority("SCOPE_profile")
							.anyRequest()
							.authenticated()
				)
				.oauth2ResourceServer(configurer ->
					configurer.jwt(customizer -> customizer.jwtAuthenticationConverter(jwtAuthenticationConverter))
				)
				.build();
	}
}
