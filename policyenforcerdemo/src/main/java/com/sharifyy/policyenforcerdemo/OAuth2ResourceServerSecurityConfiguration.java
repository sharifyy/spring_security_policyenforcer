package com.sharifyy.policyenforcerdemo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.keycloak.adapters.authorization.integration.jakarta.ServletPolicyEnforcerFilter;
import org.keycloak.adapters.authorization.spi.ConfigurationResolver;
import org.keycloak.adapters.authorization.spi.HttpRequest;
import org.keycloak.representations.adapters.config.PolicyEnforcerConfig;
import org.keycloak.util.JsonSerialization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class OAuth2ResourceServerSecurityConfiguration {

	@Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
	String jwkSetUri;

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
			.authorizeHttpRequests((authorize) -> authorize
				.anyRequest().authenticated()
			)
			.oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt(jwtConfigurer -> jwtConfigurer.decoder(jwtDecoder())))
			.addFilterAfter(policyEnforcerFilter(), BearerTokenAuthenticationFilter.class);
		return http.build();
	}

	@Bean
	ServletPolicyEnforcerFilter policyEnforcerFilter() {
		PolicyEnforcerConfig policyEnforcerConfig = new PolicyEnforcerConfig();
		policyEnforcerConfig.setRealm("java_talks");
		policyEnforcerConfig.setAuthServerUrl("http://localhost:8080");
		policyEnforcerConfig.setResource("policy_client");
		policyEnforcerConfig.setCredentials(Map.of("secret","QaKQm6A2MdXshR7LJ5kG338bw6Xs7TCB"));

		policyEnforcerConfig.setPaths(paths());
		return new ServletPolicyEnforcerFilter(httpRequest -> policyEnforcerConfig);
	}


//	@Bean
//	ServletPolicyEnforcerFilter policyEnforcerFilter() {
//		try {
//			PolicyEnforcerConfig policyEnforcerConfig = JsonSerialization.readValue(getClass().getResourceAsStream("/policy-enforcer.json"), PolicyEnforcerConfig.class);
//			return new ServletPolicyEnforcerFilter(httpRequest -> policyEnforcerConfig);
//		} catch (IOException e) {
//			throw new RuntimeException(e);
//		}
//	}

	private List<PolicyEnforcerConfig.PathConfig> paths() {
		PolicyEnforcerConfig.PathConfig config = new PolicyEnforcerConfig.PathConfig();
		PolicyEnforcerConfig.MethodConfig postMethodConfig = new PolicyEnforcerConfig.MethodConfig();
		postMethodConfig.setMethod("POST");
		postMethodConfig.setScopes(List.of("book:books:create"));

		PolicyEnforcerConfig.MethodConfig getMethodConfig = new PolicyEnforcerConfig.MethodConfig();
		getMethodConfig.setMethod("GET");
		getMethodConfig.setScopes(List.of("book:books:list"));

		config.setMethods(List.of(postMethodConfig, getMethodConfig));
		config.setId("book");
		config.setPath("/books");

		return List.of(config);
	}

	@Bean
	JwtDecoder jwtDecoder() {
		return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build();
	}

}
