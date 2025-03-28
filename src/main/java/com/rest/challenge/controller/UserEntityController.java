package com.rest.challenge.controller;

import com.rest.challenge.model.UserEntityDTO;
import com.rest.challenge.service.UserEntityService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
@RequestMapping("/users")
@Tag(name = "User API", description = "User management operations")
public class UserEntityController {

    private static final Logger logger = LoggerFactory.getLogger(UserEntityController.class);

    private final UserEntityService userEntityService;

    public UserEntityController(UserEntityService userEntityService) {
        this.userEntityService = userEntityService;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Get all users from the database")
    @CircuitBreaker(name = "userEntityCircuitBreaker", fallbackMethod = "circuitBreakGetUsers")
    @RateLimiter(name = "userEntityRateLimiter", fallbackMethod = "rateLimitGetUsers")
    public Page<UserEntityDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "10") int size,
                                        @RequestParam(defaultValue = "id") String sortBy,
                                        @RequestParam(defaultValue = "asc") String sortDirection) {
        logger.info("GET request to fetch all users");
        return userEntityService.getUsers(page, size, sortBy, sortDirection);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "Fetches a user by their unique ID.")
    public ResponseEntity<UserEntityDTO> getUserById(@PathVariable Long id) {
        logger.info("GET request for user with ID: {}", id);
        return userEntityService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Operation(summary = "Create a new user",
            description = "Adds a new user to the system.")
    public ResponseEntity<UserEntityDTO> saveUser(@Valid @RequestBody UserEntityDTO userEntityDTO) {
        logger.info("POST request to create a user: {}", userEntityDTO.getEmail());
        return new ResponseEntity<>(userEntityService.saveUser(userEntityDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user",
            description = "Updates an existing user in the system.")
    public ResponseEntity<UserEntityDTO> updateUser(@PathVariable Long id,
                                                    @Valid @RequestBody UserEntityDTO userEntityDTO) {
        logger.info("PUT request to update user with ID: {}", id);
        try {
            return ResponseEntity.ok(userEntityService.updateUser(id, userEntityDTO));
        } catch (RuntimeException e) {
            logger.error("Failed to update user with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete user",
            description = "Deletes a user from the system.")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.warn("DELETE request to remove user with ID: {}", id);
        userEntityService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    public Page<UserEntityDTO> circuitBreakGetUsers(int page, int size,
                                                   String sortBy, String sortDirection, Throwable ex){
        logger.error("Fallback for circuit breaker: {}", ex.getMessage());
        return new PageImpl<>(Collections.emptyList());
    }

    public Page<UserEntityDTO> rateLimitGetUsers(int page, int size, String sortBy,
                                                 String sortDirection, Throwable ex) {
        logger.error("Fallback for rate limit exceeded: {}", ex.getMessage());
        return new PageImpl<>(Collections.emptyList());
    }
}
