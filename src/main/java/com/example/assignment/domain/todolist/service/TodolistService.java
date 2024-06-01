package com.example.assignment.domain.todolist.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.assignment.domain.todolist.dto.TodolistDto;
import com.example.assignment.domain.todolist.entity.Todolist;
import com.example.assignment.domain.todolist.mapper.TodolistMapper;
import com.example.assignment.domain.todolist.repository.TodolistRepository;
import com.example.assignment.domain.user.entity.AmUser;
import com.example.assignment.global.error.exception.BusinessLogicException;
import com.example.assignment.global.error.exception.ExceptionCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodolistService {

	private final TodolistMapper todolistMapper;
	private final TodolistRepository todolistRepository;

	@Transactional
	public TodolistDto.CreateTodolistResponse createTodolist(AmUser user, TodolistDto.CreateTodolistRequest createTodolistRequest) {
		log.info("Todolist 생성 요청 - 사용자 ID: {}, 제목: {}", user.getId(), createTodolistRequest.getTitle());
		Todolist todolist = todolistRepository.save(Todolist.createTodolist(user, createTodolistRequest.getTitle(), createTodolistRequest.getDescription()));
		log.info("Todolist 생성 성공 - Todolist ID: {}, 제목: {}", todolist.getSeq(), todolist.getTitle());
		return todolistMapper.toCreateTodolistResponse(todolist);
	}

	@Transactional(readOnly = true)
	public TodolistDto.RetrieveTodolistResponse retrieveMostRecentTodolist(AmUser user) {
		log.info("가장 최근의 Todolist 조회 요청 - 사용자 ID: {}", user.getId());
		Todolist todolist = todolistRepository.findFirstByUserOrderByCreatedAtDesc(user);
		if (todolist == null) {
			log.info("가장 최근의 Todolist 항목 없음 - 사용자 ID: {}", user.getId());
			return null;
		}
		log.info("가장 최근의 Todolist 조회 성공 - Todolist ID: {}, 제목: {}", todolist.getSeq(), todolist.getTitle());
		return todolistMapper.toRetrieveTodolistResponse(todolist);
	}

	@Transactional(readOnly = true)
	public List<TodolistDto.ListTodolistResponse> retrieveTodolists(AmUser user) {
		log.info("모든 Todolist 조회 요청 - 사용자 ID: {}", user.getId());
		List<Todolist> todolists = todolistRepository.findByUserOrderByCreatedAtDesc(user);
		log.info("모든 Todolist 조회 성공 - 사용자 ID: {}, 항목 수: {}", user.getId(), todolists.size());
		return todolistMapper.toRetrieveTodolistResponseList(todolists);
	}

	@Transactional
	public TodolistDto.UpdateTodolistStatusResponse updateTodolistStatus(AmUser user, Long todolistSeq, TodolistDto.UpdateTodolistStatusRequest updateTodolistStatusRequest) {
		log.info("Todolist 상태 변경 요청 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
		Todolist todolist = findById(todolistSeq);

		if (!todolist.getUser().equals(user)) {
			log.error("Todolist 상태 변경 실패 - 권한 없음 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		todolist.updateStatus(updateTodolistStatusRequest.getTodoStatus());
		log.info("Todolist 상태 변경 성공 - Todolist ID: {}, 새로운 상태: {}", todolistSeq, updateTodolistStatusRequest.getTodoStatus());
		return todolistMapper.toUpdateTodolistStatusResponse(todolistRepository.save(todolist));
	}

	@Transactional
	public TodolistDto.UpdateTodolistResponse updateTodolist(AmUser user, Long todolistSeq, TodolistDto.UpdateTodolistRequest updateTodolistRequest) {
		log.info("Todolist 업데이트 요청 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
		Todolist todolist = findById(todolistSeq);

		if (!todolist.getUser().equals(user)) {
			log.error("Todolist 업데이트 실패 - 권한 없음 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		todolist.update(updateTodolistRequest.getTitle(), updateTodolistRequest.getDescription());
		log.info("Todolist 업데이트 성공 - Todolist ID: {}, 새로운 제목: {}", todolistSeq, updateTodolistRequest.getTitle());
		return todolistMapper.toUpdateTodolistResponse(todolistRepository.save(todolist));
	}

	@Transactional
	public TodolistDto.DeleteTodolistResponse deleteTodolist(AmUser user, Long todolistSeq) {
		log.info("Todolist 삭제 요청 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
		Todolist todolist = findById(todolistSeq);

		if (!todolist.getUser().equals(user)) {
			log.error("Todolist 삭제 실패 - 권한 없음 - 사용자 ID: {}, Todolist ID: {}", user.getId(), todolistSeq);
			throw new BusinessLogicException(ExceptionCode.UNAUTHORIZED);
		}

		todolistRepository.delete(todolist);
		log.info("Todolist 삭제 성공 - Todolist ID: {}", todolistSeq);

		return todolistMapper.toDeleteTodolistResponse(todolist);
	}

	@Transactional(readOnly = true)
	private Todolist findById(Long todolistSeq) {
		log.info("Todolist 조회 - Todolist ID: {}", todolistSeq);
		return todolistRepository.findById(todolistSeq)
			.orElseThrow(() -> {
				log.error("Todolist 조회 실패 - Todolist ID: {}", todolistSeq);
				return new BusinessLogicException(ExceptionCode.INVALID_TODOLIST_ID);
			});
	}
}
