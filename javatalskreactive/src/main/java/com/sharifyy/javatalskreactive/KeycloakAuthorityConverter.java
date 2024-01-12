package com.sharifyy.javatalskreactive;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class KeycloakAuthorityConverter implements Converter<Jwt, Flux<GrantedAuthority>> {

	@Override
	public Flux<GrantedAuthority> convert(Jwt source) {
		return Flux.concat(roles(source), scopes(source));
	}

	private Flux<GrantedAuthority> roles(Jwt jwt) {
		Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("realm_access", List.of());

		if (realmAccess.isEmpty()) return Flux.empty();
		return Flux.fromIterable(((List<String>) realmAccess.getOrDefault("roles", List.of()))).map(role -> new SimpleGrantedAuthority("ROLE_" + role));
	}

	private Flux<GrantedAuthority> scopes(Jwt jwt) {
		String scope = (String) jwt.getClaims().getOrDefault("scope", "");
		return Flux.fromStream(Arrays.stream(scope.split(" "))).map(s -> new SimpleGrantedAuthority("SCOPE_" + s));
	}


}
