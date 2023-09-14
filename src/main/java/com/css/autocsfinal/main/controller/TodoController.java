package com.css.autocsfinal.main.controller;


import com.css.autocsfinal.common.ResponseDTO;
import com.css.autocsfinal.mail.dto.MailDTO;
import com.css.autocsfinal.main.dto.TodoDTO;
import com.css.autocsfinal.main.entity.Todo;
import com.css.autocsfinal.main.service.TodoService;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/todo")
@Tag(name = "TodoList", description = "투두리스트 API")
public class TodoController {
    private final TodoService todoService;
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }


    // 전체 조회
    @Operation(summary = "가입된 모든회원의 todolist 조회", description = "전체 회원의 todoList를 불러옵니다. ", tags = {"TodoController" })
    @GetMapping("/getTodolist")
    public ResponseEntity<ResponseDTO> getTodoList() {

        List<TodoDTO> todoList = todoService.getTodo();
        log.info("[MemberController] selectTodoList {}", todoList);
        log.info("[MemberController] selectTodoList end");

        HttpStatus httpStatus = HttpStatus.OK;
        ResponseDTO responseDTO = new ResponseDTO(httpStatus ,"todo 조회" , todoList);
        return ResponseEntity.status(httpStatus).body(responseDTO);

    }


    // 회원별 조회
    @Operation(summary = "개인별 TodoList 조회", description = "회원 한명의 투두 리스트를 조회합니다.", tags = {"TodoController" })
    @GetMapping("/{memberNo}")
    public ResponseEntity<ResponseDTO> getTodoListMember(@PathVariable int memberNo){

        log.info("[MemberController] getTodoMember start");
        log.info("[MemberController] getTodoMember memberNo  {}" , memberNo);

        // memberNO가 1인 사용자의 todo를 가져오는 서비스 메서드 호출
        TodoDTO todoDTO = new TodoDTO();
        todoDTO.setMemberNo(memberNo);
        List<TodoDTO> todoList = todoService.getTodoByNo(todoDTO.getMemberNo());

        log.info("[MemberController] getTodoMember {}", todoList);
        log.info("[MemberController] getTodoMember end");

        HttpStatus httpStatus = HttpStatus.OK;
        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "memberNO가 1인 사용자의 todo 조회", todoList);
        return ResponseEntity.status(httpStatus).body(responseDTO);
    }


    // 저장
    @Operation(summary = "Todo(할일) 저장", description = "Todo(할일)을 입력하면 저장합니다.", tags = {"TodoController" })
    @PostMapping("/insertTodo")
    public ResponseEntity<ResponseDTO> saveTodo(@RequestBody TodoDTO todoDTO){

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED, "등록 성공", todoService.saveTodo(todoDTO)));
    }

    //선택 삭제
    @Operation(summary = "Todo(할일) 삭제", description = "Todo(할일)을 선택하여 삭제합니다.", tags = {"TodoController" })
    @DeleteMapping ("/deleteTodo")
    public ResponseEntity<ResponseDTO> deleteTodo(@RequestBody TodoDTO todoDTO){

        log.info(" delete 를 위한 TodoDTO {}" , todoDTO);

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "삭제 성공", todoService.deleteTodo(todoDTO)));
    }

    //전체 삭제
    @Operation(summary = "전체 회원의 TodoList를 전체 삭제합니다.", description = "전체 회원의 TodoList를 삭제하여 데이터를 리셋합니다.", tags = {"TodoController" })
    @DeleteMapping("/deleteAllTodo")
    public ResponseEntity<ResponseDTO> deleteTodoAll(){
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "전체 삭제.", todoService.deleteAllTodo()));
    }

    //상태 변화
    @Operation(summary = "Todo(할일) 상태변경", description = "Todo(할일)을 체크하여 진행 상태를 변경합니다 (완료/진행)", tags = {"TodoController" })
    @PutMapping("/toggle")
    public ResponseEntity<ResponseDTO> toggleTodo(@RequestBody TodoDTO todoDTO) {
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "상태 변경.", todoService.toggleTodo(todoDTO)));
    }

    //수정
    @Operation(summary = "Todo(할일) 수정", description = "Todo(할일)을 선택하여 내용을 수정합니다.", tags = {"TodoController" })
    @PutMapping("/updateTodo")
    public ResponseEntity<ResponseDTO> updateTodo(@RequestBody TodoDTO todoDTO){
        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "투두 수정", todoService.updateTodo(todoDTO)));
    }

}
