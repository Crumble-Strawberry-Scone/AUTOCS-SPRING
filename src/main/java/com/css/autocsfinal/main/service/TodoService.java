package com.css.autocsfinal.main.service;

import com.css.autocsfinal.exception.LoginFailedException;
import com.css.autocsfinal.mail.dto.MailDTO;
import com.css.autocsfinal.mail.entity.Mail;
import com.css.autocsfinal.main.dto.TodoDTO;
import com.css.autocsfinal.main.entity.Todo;
import com.css.autocsfinal.main.repository.TodoRepository;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.entity.Employee;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.testng.annotations.Test;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TodoService {

    public final MemberRepository memberRepository;
    public final TodoRepository todoRepository;
    public final ModelMapper modelMapper;

    public TodoService(MemberRepository memberRepository, TodoRepository todoRepository, ModelMapper modelMapper) {
        this.memberRepository = memberRepository;
        this.todoRepository = todoRepository;
        this.modelMapper = modelMapper;
    }




    // 한 회원 조회하기 ( 특정 아이디의 todo리스트를 조회필요) List로 받아야할듯
    public List<TodoDTO> getTodoByNo(int memberNo) {

        log.info("[TodoService] todo 조회하기 Start ===================");
        List<Todo> todos = todoRepository.findByMemberNo(memberNo);
        List<TodoDTO> todoDTOList =todos.stream()
                .map(todo -> {
                    TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTodoNo(todo.getTodoNo());
                    todoDTO.setContent(todo.getContent());
                    todoDTO.setTodoStatus(todo.getTodoStatus());
                    todoDTO.setRegDate(todo.getRegDate());
                    todoDTO.setMemberNo(todo.getMember().getNo());

                    return todoDTO;
                })
                .collect(Collectors.toList());
        return todoDTOList;
    }




// 투두리스트 전체 조회
public List<TodoDTO> getTodo() {
    log.info("[TodoService] todo 조회하기 Start ===================");

    List<Todo> todoList = todoRepository.findAll();
    log.info("todoList : " + todoList);


    List<TodoDTO> todoDTOList = todoList.stream()
            .map(todo -> {
                TodoDTO todoDTO = new TodoDTO();
                    todoDTO.setTodoNo(todo.getTodoNo());
                    todoDTO.setContent(todo.getContent());
                    todoDTO.setTodoStatus(todo.getTodoStatus());
                    todoDTO.setRegDate(todo.getRegDate());
                    todoDTO.setMemberNo(todo.getMember().getNo());

                return todoDTO;
            })
            .collect(Collectors.toList());

    return todoDTOList;
}


    // todo추가하기
    @Transactional
    public String saveTodo(TodoDTO todoDTO) {
        log.info("[TodotService] insertProduct Start ======================================");
        log.info("[TodotService] productDTO : {} " , todoDTO);

        int result = 0; // 결과에 따른 값을 구분하기 위한 용도의 변수

        try{

            Todo insertTodo = modelMapper.map(todoDTO,Todo.class);
            todoRepository.save(insertTodo);
            result=1;

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("[TodotService] insertProduct End ========================================");

        return (result > 0)? " todo 성공" : "todo 실패";


    }

    // 전체 todo삭제
    public Object deleteAllTodo() {
        log.info("[deleteAllTodo] deleteTodo Start ======================================");
        todoRepository.deleteAll();

        return null;
    }


    //todo삭제하기

    public Object deleteTodo(TodoDTO todoDTO) {

        log.info("[TodotService] deleteTodo Start ======================================");
        log.info("[TodotService] todoDTO : {} " , todoDTO);
        int todoNo = todoDTO.getTodoNo();
        log.info("[TodotService] todoNo : {} " , todoNo);
        todoRepository.deleteById(todoNo);

        return null;
    }

    // todo토글 (완료 상태 변경하기 할일 완료 진행)
    @Transactional
    public String toggleTodo(TodoDTO todoDTO) {
        int todoNo = todoDTO.getTodoNo();
        Todo todo = todoRepository.findById(todoNo).get();

        log.info("[TodotService] toggleTodo Start ======================================");
        log.info("[TodotService] toggleTodo todoNo : {} " , todoNo);
        int result = 0;
        if(todo.getTodoStatus() == 'N'){
            todo.setTodoStatus('Y');
            result = 1;
        } else {
            todo.setTodoStatus('N');
        }

        Todo insertTodo = modelMapper.map(todoDTO,Todo.class);
        return (result > 0)? " todo 성공" : "todo 실패";

        }

    // todo수정하기
    @Transactional
    public String updateTodo(TodoDTO todoDTO) {
        int todoNo = todoDTO.getTodoNo();
        Todo todo = todoRepository.findById(todoNo).get();
        log.info("[TodotService] updateTodo Start ======================================");
        log.info("[TodotService] updateTodo todoNo : {} " , todoNo);
        int result = 0;

        if(todo.getContent() != null){
            todo.setContent(todoDTO.getContent());
            todo.setUpDate(LocalDate.now());
            log.info("[TodotService] updateTodo todo.getContent : {} " , todo.getContent());
            log.info("[TodotService] updateTodo todo.setUpDate : {} " , todo.getUpDate());
            result =1;
        } else {
            log.info("[TodotService] updateTodo 실패 ");
            new RuntimeException();
        }

        Todo updateTodo = modelMapper.map(todoDTO,Todo.class);
        return (result > 0)? " todo 수정 성공" : "todo 수정 실패";

    }
}









