package com.sharifyy.javatalskreactive;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
public class BookController {


    @GetMapping("/books")
    public Flux<Book> books(JwtAuthenticationToken token){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Flux.just(
                new Book("Implementing domain driven design","Vaughn Vernon"),
                new Book("TDD by example","Kent Beck"),
                new Book("Agile software practices","Robert Martin"),
                new Book("Refactoring","Martin Fowler")
        );
    }

	@PostMapping("/books")
	public Mono<Book> create(@RequestBody Book book){
		return Mono.just(book);
	}

    @GetMapping("/token")
    public Jwt token(@AuthenticationPrincipal Jwt jwt){
        return jwt;
    }

	@GetMapping("/profile")
	public Mono<String> profile(){
		return Mono.just("scope test");
	}

    public record Book(String title,String author){}
}
