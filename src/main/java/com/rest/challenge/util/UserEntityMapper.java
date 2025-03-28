package com.rest.challenge.util;

import com.rest.challenge.entity.UserEntity;
import com.rest.challenge.model.UserEntityDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UserEntityMapper {
    public UserEntityDTO toDTO(UserEntity userEntity){
        return new UserEntityDTO(
                userEntity.getId(),
                userEntity.getName(),
                userEntity.getEmail());
    }

    public UserEntity toEntity(UserEntityDTO userEntityDTO){
        return new UserEntity(
                userEntityDTO.getId(),
                userEntityDTO.getName(),
                userEntityDTO.getEmail());
    }

    public List<UserEntityDTO> toDTOList(List<UserEntity> userEntities){
        return userEntities.stream()
                .map(this::toDTO)
                .toList();
    }

    public List<UserEntity> toEntityList(List<UserEntityDTO> userEntityDTOs){
        return userEntityDTOs.stream()
                .map(this::toEntity)
                .toList();
    }
}
