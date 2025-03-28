package com.rest.challenge.service;

import com.rest.challenge.entity.UserEntity;
import com.rest.challenge.model.UserEntityDTO;
import com.rest.challenge.repository.UserEntityRepository;
import com.rest.challenge.util.UserEntityMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserEntityServiceUnitTest {

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserEntityMapper userEntityMapper;

    @InjectMocks
    private UserEntityService userEntityService;

    private UserEntity userEntity;
    private UserEntityDTO userEntityDTO;

    @BeforeEach
    void setUp(){
        userEntity = new UserEntity(1L, "John Doe", "john@example.com");
        userEntityDTO = new UserEntityDTO(1L, "John Doe", "john@example.com");
    }

    @Test
    void testGetAllUsers(){
        Pageable pageable = PageRequest.of(0, 2, Sort.by("name"));
        Page<UserEntity> userPage = new PageImpl<>(List.of(userEntity));

        when(userEntityRepository.findAll(pageable)).thenReturn(userPage);
        when(userEntityMapper.toDTO(userEntity)).thenReturn(userEntityDTO);

        Page<UserEntityDTO> result = userEntityService.getUsers(0,2, "name", "asc");

        assertEquals(1, result.getContent().size());
        assertEquals("John Doe", result.getContent().get(0).getName());

        verify(userEntityRepository).findAll(pageable);
        verify(userEntityMapper).toDTO(userEntity);
    }

    @Test
    void testGetUserById_UserExists(){
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityMapper.toDTO(userEntity)).thenReturn(userEntityDTO);

        Optional<UserEntityDTO> result = userEntityService.getUserById(1L);

        assertEquals("John Doe", result.get().getName());

        verify(userEntityRepository).findById(1L);
        verify(userEntityMapper).toDTO(userEntity);
    }

    @Test
    void testGetUserById_UserNotFound() {
        when(userEntityRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            Optional<UserEntityDTO> result = userEntityService.getUserById(99L);
        } catch (RuntimeException e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(userEntityRepository).findById(99L);
        verifyNoInteractions(userEntityMapper);
    }

    @Test
    void testCreateUser() {
        when(userEntityMapper.toEntity(userEntityDTO)).thenReturn(userEntity);
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userEntityMapper.toDTO(userEntity)).thenReturn(userEntityDTO);

        UserEntityDTO result = userEntityService.saveUser(userEntityDTO);

        assertEquals("John Doe", result.getName());

        verify(userEntityMapper).toEntity(userEntityDTO);
        verify(userEntityRepository).save(userEntity);
        verify(userEntityMapper).toDTO(userEntity);
    }

    @Test
    void testDeleteUser(){
        doNothing().when(userEntityRepository).deleteById(1L);

        userEntityService.deleteUser(1L);

        verify(userEntityRepository).deleteById(1L);
    }

    @Test
    void testUpdateUser_Found(){
        when(userEntityRepository.findById(1L)).thenReturn(Optional.of(userEntity));
        when(userEntityRepository.save(userEntity)).thenReturn(userEntity);
        when(userEntityMapper.toDTO(userEntity)).thenReturn(userEntityDTO);

        UserEntityDTO result = userEntityService.updateUser(1L, userEntityDTO);

        assertEquals("John Doe", result.getName());

        verify(userEntityRepository).findById(1L);
        verify(userEntityRepository).save(userEntity);
        verify(userEntityMapper).toDTO(userEntity);
    }

    @Test
    void testUpdateUser_NotFound(){
        when(userEntityRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            userEntityService.updateUser(99L, userEntityDTO);
        } catch (RuntimeException e) {
            assertEquals("User not found", e.getMessage());
        }

        verify(userEntityRepository, times(1)).findById(99L);
    }
}
