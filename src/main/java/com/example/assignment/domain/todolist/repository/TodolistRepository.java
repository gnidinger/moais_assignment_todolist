package com.example.assignment.domain.todolist.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.user.entity.AmUser;

public interface TodolistRepository extends JpaRepository<Todolist, Long> {
	Todolist findFirstByUserOrderByCreatedAtDesc(AmUser user);

	List<Todolist> findByUserOrderByCreatedAtDesc(AmUser user);

	List<Todolist> findByStatusAndModifiedAtBefore(TodoStatus status, LocalDateTime cutoffDate);
}
