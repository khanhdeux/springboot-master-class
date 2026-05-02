package com.example.userservice.mapper;

import com.example.userservice.dto.UserRequestDTO;
import com.example.userservice.dto.UserResponseDTO;
import com.example.userservice.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {
    User toEntity(UserRequestDTO dto);
    
    // NEU: Für die Antwort an den Client
    UserResponseDTO toResponseDto(User entity);
    
    void updateEntityFromDto(UserRequestDTO dto, @MappingTarget User entity);
}