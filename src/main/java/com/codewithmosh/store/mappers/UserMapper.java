package com.codewithmosh.store.mappers;


import com.codewithmosh.store.dtos.RegisterUserRequest;
import com.codewithmosh.store.dtos.UpdateUserRequest;
import com.codewithmosh.store.dtos.UserDto;
import com.codewithmosh.store.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")// So spring can create beans of this type at run time.
public interface UserMapper {
// @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    void update(UpdateUserRequest request, @MappingTarget User user);

    User toEntity(RegisterUserRequest request);
}
