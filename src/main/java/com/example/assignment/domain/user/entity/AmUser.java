package com.example.assignment.domain.user.entity;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.user.entity.enums.AuthType;
import com.example.assignment.global.config.PBKDF2Encoder;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Table(name = "AM_USER")
public class AmUser {

	@Id
	@Column(name = "AM_USER_SEQ")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;

	@Column(name = "ID", nullable = false)
	private String id;

	@Column(name = "NICKNAME", nullable = false)
	private String nickname;

	@Column(name = "PASSWORD")
	private String password;

	@Enumerated(EnumType.STRING)
	@Column(name = "AUTH_TYPE", nullable = false)
	private AuthType authType;

	@OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Todolist> todolists;

	@Column(name = "CREATED_AT")
	private LocalDateTime createdAt;

	@Column(name = "MODIFIED_AT")
	private LocalDateTime modifiedAt;

	@PrePersist
	protected void onPersist() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		modifiedAt = LocalDateTime.now();
	}

	public static AmUser createUser(String id, String nickname, String password, PBKDF2Encoder pbkdf2Encoder) {
		return AmUser.builder()
			.id(id)
			.nickname(nickname)
			.password(pbkdf2Encoder.encode(password))
			.authType(AuthType.ROLE_USER)
			.build();
	}
}
