package com.sharifyy.javatalkskeycloak;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BookController {


    @GetMapping("/books")
    public List<Book> books(JwtAuthenticationToken token){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return List.of(
                new Book("Implementing domain driven design","Vaughn Vernon"),
                new Book("TDD by example","Kent Beck"),
                new Book("Agile software practices","Robert Martin"),
                new Book("Refactoring","Martin Fowler")
        );
    }

	@PostMapping("/books")
	public Book create(@RequestBody Book book){
		return book;
	}


    @GetMapping("/token")
    public Jwt token(@AuthenticationPrincipal Jwt jwt){
        return jwt;
    }

	@GetMapping("/profile")
	public String profile(){
		return "scope test";
	}

    public record Book(String title,String author){}
}
