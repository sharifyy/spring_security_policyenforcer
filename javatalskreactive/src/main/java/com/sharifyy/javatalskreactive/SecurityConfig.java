package com.sharifyy.javatalskreactive;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

//	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
//	private String jwksUri;

	@Bean
	public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {

		ReactiveJwtAuthenticationConverter jwtAuthenticationConverter = new ReactiveJwtAuthenticationConverter();
		jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakAuthorityConverter());
		return http
			.csrf(ServerHttpSecurity.CsrfSpec::disable)
			.authorizeExchange(spec -> {
				spec
					.pathMatchers("/token").permitAll()
					.pathMatchers(HttpMethod.GET, "/books").hasRole("book_reader")
					.pathMatchers(HttpMethod.POST, "/books").hasRole("book_writer")
					.pathMatchers(HttpMethod.GET, "/profile").hasAuthority("SCOPE_profile")
					.anyExchange()
					.authenticated();
			})

			.oauth2ResourceServer(oAuth2ResourceServerSpec ->
				oAuth2ResourceServerSpec.jwt(jwtSpec -> {
					jwtSpec.jwtAuthenticationConverter(jwtAuthenticationConverter);
//					jwtSpec.jwtDecoder(new NimbusReactiveJwtDecoder(jwksUri));
				}))
			.build();
	}

}
