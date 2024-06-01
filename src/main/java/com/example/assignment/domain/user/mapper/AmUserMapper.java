package com.example.assignment.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.example.assignment.domain.user.dto.AmUserDto;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.global.config.security.dto.LoginRequestDto;
import com.example.assignment.global.config.security.dto.LoginResponseDto;

@Mapper(componentModel = "spring")
public interface AmUserMapper {

	AmUserDto.RegisterResponse ToRegisterResponse(AmUser amUser);

	@Mapping(target = "seq", ignore = true)
	@Mapping(target = "nickname", ignore = true)
	@Mapping(target = "authType", ignore = true)
	@Mapping(target = "todolists", ignore = true)
	@Mapping(target = "createdAt", ignore = true)
	@Mapping(target = "modifiedAt", ignore = true)
	AmUser loginRequestToUser(LoginRequestDto loginRequestDto);

	LoginResponseDto ToLoginResponseDto(AmUser amUser);

	AmUserDto.DeleteUserResponse ToDeleteUserResponse(AmUser amUser);
}
