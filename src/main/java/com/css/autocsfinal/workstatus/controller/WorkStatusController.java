package com.css.autocsfinal.workstatus.controller;

import com.css.autocsfinal.common.*;
import com.css.autocsfinal.workstatus.dto.WorkStatusDTO;
import com.css.autocsfinal.workstatus.repository.WorkStatusListRepository;
import com.css.autocsfinal.workstatus.service.WorkStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
@Tag(name = "WorkStatus", description = "근태 관리 API")
public class WorkStatusController {

    private final WorkStatusService workStatusService;


    @GetMapping("/workStatus/{employeeNo}")
    @Operation(summary = "근태현황 홈 화면", description = "현재 로그인된 직원의 정보를 가져와 근태 현황을 출력 합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByAll(@PathVariable int employeeNo){

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "근태 관리 조회 성공",  workStatusService.findByEmployeeNo(employeeNo)));
    }

    @GetMapping("/department")
    @Operation(summary = "부서별 근태 통계 화면", description = "모든 부서원의 근태관리 현황을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByDepartmentAll(){

        log.info("============================?>>");
        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "본사 근태 관리 조회 성공", workStatusService.findByDepartmentAll()));
    }

    // 인사부 조회
    @GetMapping("/personnel/{name}")
    @Operation(summary = "인사부 근태 통계 화면", description = "인사부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByPersonnel(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            object = workStatusService.findByPersonnel();
        } else{
            object = workStatusService.findByPersonnel(name);
        }

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "인사부 조회 성공", object));
    }

    // 재무/회계부 조회
    @GetMapping("/accounting/{name}")
    @Operation(summary = "재무/회계부 근태 통계 화면", description = "재무/회계부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByAccounting(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            object = workStatusService.findByAccounting();
        } else{
            log.info("============================>22222222222222222222222222222");
            object = workStatusService.findByAccounting(name);
        }

        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "재무/회계부 조회 성공", object));
    }

    // 경영부 조회
    @GetMapping("/management/{name}")
    @Operation(summary = "경영부 근태 통계 화면", description = "인사부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByManagement(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            log.info("============================>111111111111111111111111");
            object = workStatusService.findByManagement();
        } else{
            log.info("============================>22222222222222222222222222222");
            object = workStatusService.findByManagement(name);
        }


        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "경영부 조회 성공", object ));
    }

    // 마케팅부 조회
    @GetMapping("/marketing/{name}")
    @Operation(summary = "마케팅부 근태 통계 화면", description = "인사부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByMarketing(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            log.info("============================>111111111111111111111111");
            object = workStatusService.findByMarketing();
        } else{
            log.info("============================>22222222222222222222222222222");
            object = workStatusService.findByMarketing(name);
        }



        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "마케팅부 조회 성공", object ));
    }

    // 영업부 조회
    @GetMapping("/sales/{name}")
    @Operation(summary = "영업부 근태 통계 화면", description = "인사부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findBySales(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            log.info("============================>111111111111111111111111");
            object = workStatusService.findBySales();
        } else{
            log.info("============================>22222222222222222222222222222");
            object = workStatusService.findBySales(name);
        }
        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "영업부 조회 성공", object));
    }

    // 서비스부 조회
    @GetMapping("/service/{name}")
    @Operation(summary = "서비스부 근태 통계 화면", description = "인사부의 근태 통계 화면을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByService(@PathVariable String name){

        Object object;

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            log.info("============================>111111111111111111111111");
            object = workStatusService.findByService();
        } else{
            log.info("============================>22222222222222222222222222222");
            object = workStatusService.findByService(name);
        }



        return ResponseEntity
                .ok()
                .body(new ResponseDTO(HttpStatus.OK, "서비스부 조회 성공", object));
    }

    // 출근하기
    @PostMapping("/attendance/{employeeNo}")
    @Operation(summary = "메인 페이지 화면", description = "현재 시간을 기점으로 출근을 하여 값을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> saveAttendance(@PathVariable int employeeNo ){


        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.CREATED, "출근 성공", workStatusService.saveAttendance(employeeNo)));
    }

    // 퇴근
    @PutMapping("/quitting/{employeeNo}")
    @Operation(summary = "메인 페이지 화면", description = "현재 시간을 기점으로 퇴근을 하여 값을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> saveQuitting(@PathVariable int employeeNo){

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "퇴근 성공", workStatusService.saveQuitting(employeeNo)));
    }


    // 본사 근태 현황
    @GetMapping("/headOffice/{page}/{search}")
    @Operation(summary = "본사 근태 통계 현황 화면", description = "모든 직원의 근태 통계를 이번주 월요일을 기준으로 값을 출력합니다.", tags = {"WorkStatusController"})
    public ResponseEntity<ResponseDTO> findByHeadOffice( @PathVariable(name = "page", required = false) int offset,
                                                         @PathVariable(name = "search", required = false) String name){

        int total;

        Criteria cri = new Criteria(Integer.valueOf(offset), 8);

        PagingResponseDTO pagingResponseDTO = new PagingResponseDTO();

        if(name.equals("절대로아무도검색하지않을만한값입니다.")){
            total = workStatusService.findByHeadOfficeTotal();
            pagingResponseDTO.setData(workStatusService.findByHeadOffice(cri));
        } else {
            total = workStatusService.findByHeadOfficeTotal(name);
            pagingResponseDTO.setData(workStatusService.findByHeadOffice(cri, name));
        }

        pagingResponseDTO.setPageInfo(new PageDTO(cri, total));

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "본사 근태관리 조회 성공", pagingResponseDTO));
    }
}
























