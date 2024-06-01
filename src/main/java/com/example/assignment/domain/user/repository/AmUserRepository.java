package com.example.assignment.domain.user.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.assignment.domain.user.entity.AmUser;

public interface AmUserRepository extends JpaRepository<AmUser, Long> {
	Optional<AmUser> findById(String id);
}
