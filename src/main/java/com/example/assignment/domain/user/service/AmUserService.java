package com.example.assignment.domain.user.service;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assignment.domain.user.dto.AmUserDto;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.mapper.AmUserMapper;
import com.example.assignment.domain.user.repository.AmUserRepository;
import com.example.assignment.global.config.PBKDF2Encoder;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AmUserService {

	private final AmUserMapper amUserMapper;
	private final PBKDF2Encoder pbkdf2Encoder;
	private final AmUserRepository amUserRepository;

	@Transactional
	public AmUserDto.RegisterResponse registerUser(AmUserDto.Register registerDto) {
		log.info("회원 가입 요청 - ID: {}", registerDto.getId());
		verifyExistId(registerDto.getId());

		if (!registerDto.getPassword().equals(registerDto.getPasswordRepeat())) {
			log.error("비밀번호 불일치 - ID: {}", registerDto.getId());
			throw new BusinessLogicException(ExceptionCode.PASSWORD_MISMATCH);
		}

		AmUser amUser = amUserRepository.save(AmUser.createUser(registerDto.getId(), registerDto.getNickname(), registerDto.getPassword(), pbkdf2Encoder));
		log.info("회원 가입 성공 - ID: {}", registerDto.getId());

		return amUserMapper.ToRegisterResponse(amUser);
	}

	@Transactional
	public AmUserDto.DeleteUserResponse deleteUser(AmUser currentUser, AmUserDto.DeleteUserRequest deleteUserRequest) {
		log.info("회원 탈퇴 요청 - ID: {}", currentUser.getId());

		if (!pbkdf2Encoder.encode(deleteUserRequest.getPassword()).equals(currentUser.getPassword())) {
			log.error("비밀번호 불일치 - ID: {}", currentUser.getId());
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		amUserRepository.delete(currentUser);
		log.info("회원 탈퇴 성공 - ID: {}", currentUser.getId());

		return amUserMapper.ToDeleteUserResponse(currentUser);
	}

	public void verifyExistId(String id) {
		Optional<AmUser> user = amUserRepository.findById(id);
		if (user.isPresent()) {
			log.error("이미 존재하는 ID - ID: {}", id);
			throw new BusinessLogicException(ExceptionCode.EMAIL_EXIST);
		}
	}

	@Transactional(readOnly = true)
	public AmUser findById(String userId) {
		log.info("사용자 조회 - ID: {}", userId);
		return amUserRepository.findById(userId)
			.orElseThrow(() -> {
				log.error("사용자를 찾을 수 없음 - ID: {}", userId);
				return new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
			});
	}
}
