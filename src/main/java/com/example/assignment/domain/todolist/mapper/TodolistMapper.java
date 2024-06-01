package com.example.assignment.domain.todolist.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.example.assignment.domain.todolist.dto.TodolistDto;
import com.example.assignment.domain.todolist.entity.Todolist;

@Mapper(componentModel = "spring")
public interface TodolistMapper {
	TodolistDto.CreateTodolistResponse toCreateTodolistResponse(Todolist todolist);

	TodolistDto.RetrieveTodolistResponse toRetrieveTodolistResponse(Todolist todolist);

	List<TodolistDto.ListTodolistResponse> toRetrieveTodolistResponseList(List<Todolist> todolist);

	TodolistDto.UpdateTodolistStatusResponse toUpdateTodolistStatusResponse(Todolist todolist);

	TodolistDto.UpdateTodolistResponse toUpdateTodolistResponse(Todolist todolist);

	TodolistDto.DeleteTodolistResponse toDeleteTodolistResponse(Todolist todolist);
}
