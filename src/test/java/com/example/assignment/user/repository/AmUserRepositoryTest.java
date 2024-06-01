package com.example.assignment.user.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.junit.jupiter.api.extension.ExtendWith;

import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.domain.user.repository.AmUserRepository;

@DataJpaTest
@ExtendWith(SpringExtension.class)
public class AmUserRepositoryTest {

	@Autowired
	private AmUserRepository amUserRepository;

	@Test
	public void testSaveAndFindById() {
		// Given
		AmUser user = AmUser.builder()
			.id("user@example.com")
			.authType(AuthType.ROLE_USER)
			.nickname("example-user")
			.build();

		amUserRepository.save(user);

		// When
		Optional<AmUser> foundUser = amUserRepository.findById("user@example.com");

		// Then
		assertTrue(foundUser.isPresent());
		assertEquals("user@example.com", foundUser.get().getId());
		assertEquals(AuthType.ROLE_USER, foundUser.get().getAuthType());
		assertEquals("example-user", foundUser.get().getNickname());
	}
}
