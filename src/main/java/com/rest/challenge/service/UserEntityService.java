package com.rest.challenge.service;

import com.rest.challenge.entity.UserEntity;
import com.rest.challenge.exception.UserNotFoundException;
import com.rest.challenge.model.UserEntityDTO;
import com.rest.challenge.repository.UserEntityRepository;
import com.rest.challenge.util.UserEntityMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserEntityService {

    private static final Logger logger = LoggerFactory.getLogger(UserEntityService.class);

    private final UserEntityRepository userEntityRepository;

    private final UserEntityMapper userEntityMapper;

    public UserEntityService(UserEntityRepository userEntityRepository, UserEntityMapper userEntityMapper) {
        this.userEntityRepository = userEntityRepository;
        this.userEntityMapper = userEntityMapper;
    }

    @Cacheable(value = "usersPaginated",
            key = "{#page, #size, #sortBy, #sortDirection}",
            unless = "#result == null || #result.content.isEmpty()")
    public Page<UserEntityDTO> getUsers(int page, int size, String sortBy, String sortDirection){
        logger.info("Fetching all users");
        Sort.Direction direction =
                sortDirection.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userEntityRepository.findAll(pageable).map(userEntityMapper::toDTO);
    }

    @Cacheable(value = "user", key = "#id")
    public Optional<UserEntityDTO> getUserById(Long id) {
        logger.info("Fetching user by ID: {}", id);
        UserEntity userEntity = userEntityRepository.findById(id)
                .orElseThrow( () -> {
                    logger.error("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found");
                });
        return Optional.ofNullable(userEntityMapper.toDTO(userEntity));
    }

    @CachePut(value = "user", key = "#userEntityDTO.email")
    public UserEntityDTO saveUser(UserEntityDTO userEntityDTO) {
        logger.info("Creating new user: {}", userEntityDTO.getEmail());
        return userEntityMapper.toDTO(
                userEntityRepository.save(userEntityMapper.toEntity(userEntityDTO)));
    }

    @CachePut(value = "user", key = "#id")
    public UserEntityDTO updateUser(Long id, UserEntityDTO userEntityDTO) {
        logger.info("Updating user with ID: {}", id);
        return userEntityRepository.findById(id).map(user -> {
            user.setName(userEntityDTO.getName());
            user.setEmail(userEntityDTO.getEmail());
            return userEntityMapper.toDTO(userEntityRepository.save(user));
        }).orElseThrow(() -> {
            logger.error("User not found with ID: {}", id);
            return new UserNotFoundException("User not found");
        });
    }

    @CacheEvict(value = "user", key = "#id")
    public void deleteUser(Long id) {
        logger.warn("Deleting user with ID: {}", id);
        userEntityRepository.deleteById(id);
    }

    @CacheEvict(value = "user", allEntries = true)
    public void clearCache() {
        logger.warn("Clearing users cache");
    }

    @CacheEvict(value = "usersPaginated", allEntries = true)
    public void clearUsersPaginatedCache() {
        logger.info("Clearing paginated users cache");
    }
}
