package com.example.assignment.domain.todolist.entity;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.example.assignment.domain.todolist.entity.enums.TodoStatus;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "TODOLIST")
public class Todolist {

	@Id
	@Column(name = "TODOLIST_SEQ")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@Column(name = "TITLE", nullable = false)
	private String title;

	@Column(name = "DESCRIPTION")
	private String description;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS", nullable = false)
	private TodoStatus status;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	private LocalDateTime modifiedAt;

	@ManyToOne
	@JoinColumn(name = "AM_USER_SEQ", nullable = false)
	private AmUser user;

	@PrePersist
	protected void onPersist() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		modifiedAt = LocalDateTime.now();
	}

	public static Todolist createTodolist(AmUser user, String title, String description) {
		return Todolist.builder()
			.title(title)
			.description(description)
			.status(TodoStatus.TODO)
			.user(user)
			.build();
	}

	public void updateStatus(TodoStatus newStatus) {
		if (this.status == TodoStatus.IN_PROGRESS && newStatus == TodoStatus.PENDING) {
			this.status = newStatus;
		} else if (this.status == TodoStatus.PENDING) {
			this.status = newStatus;
		} else if (newStatus == TodoStatus.TODO || newStatus == TodoStatus.IN_PROGRESS || newStatus == TodoStatus.DONE) {
			this.status = newStatus;
		} else {
			throw new BusinessLogicException(ExceptionCode.INVALID_STATUS_TRANSITION);
		}
	}

	public void update(String title, String description) {
		this.title = title;
		this.description = description;
	}
}
