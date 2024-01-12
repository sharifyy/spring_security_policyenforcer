package com.sharifyy.javatalkskeycloak;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KeycloakAuthorityConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

	@Override
	public Collection<GrantedAuthority> convert(Jwt jwt) {
		return Stream.concat(roles(jwt), scopes(jwt)).toList();
	}

	private Stream<GrantedAuthority> roles(Jwt jwt) {
		Map<String, Object> realmAccess = (Map<String, Object>) jwt.getClaims().getOrDefault("realm_access", List.of());

		if (realmAccess.isEmpty()) return Stream.empty();

		return ((List<String>) realmAccess.getOrDefault("roles", List.of())).stream()
			.map(role -> new SimpleGrantedAuthority("ROLE_" + role));
	}

	private Stream<GrantedAuthority> scopes(Jwt jwt) {
		String scope = (String) jwt.getClaims().getOrDefault("scope", "");
		return Arrays.stream(scope.split(" "))
			.map(s -> new SimpleGrantedAuthority("SCOPE_" + s));
	}
}
