package com.css.autocsfinal.member.controller;

import com.css.autocsfinal.common.ResponseDTO;
import com.css.autocsfinal.mail.service.MailService;
import com.css.autocsfinal.market.dto.StoreInfoDTO;
import com.css.autocsfinal.market.repository.StoreInfoRepository;
import com.css.autocsfinal.market.service.EmailService;
import com.css.autocsfinal.member.dto.EmployeeAndDepartmentAndPositionDTO;
import com.css.autocsfinal.member.dto.EmployeeDTO;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.dto.PositionDTO;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.MemberRepository;
import com.css.autocsfinal.member.dto.RequestBodyTypeDTO;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.SecureRandom;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/member")
@Tag(name = "Member", description = "멤버 API")
public class MemberController {
    private final MemberService memberService;

    private final  EmailService emailService;

    public MemberController(MemberService memberService, MailService mailService, EmailService emailService) {
        this.memberService = memberService;
        this.emailService = emailService;
    }

    //사원 등록
    @Operation(summary = "사원 등록 요청", description = "사원을 등록합니다.", tags = {"MemberController"})
    @PostMapping("/insertEmployee")
    public ResponseEntity<ResponseDTO> registerEmployee(@RequestBody EmployeeDTO employeeDTO, MemberDTO memberDTO) {

        log.info("employeeDTO===========================> {}", employeeDTO);

        //사원 등록 전 아이디와 임시비밀번호발급
        String result1 = memberService.insertIdPwd(employeeDTO, memberDTO);

        //사원 등록
        String resultMessage = memberService.insertEmployee(employeeDTO);

        HttpStatus httpStatus = (result1.contains("성공") && resultMessage.contains("성공")) ? HttpStatus.CREATED : HttpStatus.BAD_REQUEST;

        return ResponseEntity
                .status(httpStatus)
                .body(new ResponseDTO(httpStatus, resultMessage, null));
    }

    //사원 조회 리스트
    @Operation(summary = "사원 조회 요청", description = "사원을 조회합니다.", tags = {"MemberController"})
    @GetMapping("/getEmployee")
    public ResponseEntity<ResponseDTO> getEmployee() {
        System.out.println("check ==========================");
        List<EmployeeAndDepartmentAndPositionDTO> employeeDTOList = memberService.getEmployee();

        HttpStatus httpStatus = HttpStatus.OK;

        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "사원 조회 성공", employeeDTOList);

        return ResponseEntity.status(httpStatus).body(responseDTO);
    }

    //직원 아이디 찾기
    @Operation(summary = "직원 아이디 찾기 요청", description = "직원의 아이디를 조회합니다.", tags = {"MemberController"})
    @GetMapping("/findEmployeeId")
    public ResponseEntity<ResponseDTO> findEmployeeId(@RequestParam String name, @RequestParam String employeeEmail) {
        try {
            // 이름과 이메일로 Employee 정보 조회
            EmployeeAndDepartmentAndPositionDTO foundEmployee = memberService.findEmployeeByNameAndEmail(name, employeeEmail);

            if (foundEmployee != null) {
                HttpStatus httpStatus = HttpStatus.OK;
                ResponseDTO responseDTO = new ResponseDTO(httpStatus, "아이디 찾기 성공", foundEmployee);
                return ResponseEntity.status(httpStatus).body(responseDTO);
            }

            // Employee 정보나 아이디가 없을 경우
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "사용자 정보 또는 아이디를 찾을 수 없습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        } catch (Exception e) {
            log.error("Error while finding employee ID: {}", e.getMessage());
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "아이디 찾기 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        }
    }

    //비밀번호 인증번호 만들기
    @Operation(summary = "인증번호 생성", description = "인증번호를 생성합니다.", tags = {"MemberController"})
    private String generateVerificationCode() {
        // 영문 대문자, 소문자, 숫자로 이루어진 문자열
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";

        // 난수 생성기 초기화
        SecureRandom secureRandom = new SecureRandom();

        // 15자리의 인증번호 생성
        StringBuilder verificationCode = new StringBuilder(15);
        for (int i = 0; i < 15; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            verificationCode.append(characters.charAt(randomIndex));
        }

        return verificationCode.toString();
    }

    //직원 비밀번호 찾기
    @Operation(summary = "직원 비밀번호 인증 요청", description = "인증번호 생성 후  이메일 발송합니다.", tags = {"MemberController"})
    @GetMapping("/findEmployeePwd")
    public ResponseEntity<ResponseDTO> findEmployeePwd(@RequestParam String name, @RequestParam String id, @RequestParam String employeeEmail) {
        try {
            // 이름과 이메일로 Employee 정보 조회
            EmployeeAndDepartmentAndPositionDTO foundEmployee = memberService.findEmployeeByNameAndEmail(name, employeeEmail);
            log.info("foundEmployee>>>>>>>>>>>>>>>>>>>>>>{}", foundEmployee);
            log.info("foundEmployee.id>>>>>>>>>>>>>>>>>>>>>>{}", foundEmployee.getMember().getId());

            if (foundEmployee != null && foundEmployee.getMember().getId().equals(id)) {

                // 인증번호 생성
                String verificationCode = generateVerificationCode();

                //인증번호 확인
                log.info("generateVerificationCode>>>>>>>>>>>>>>>>>>>>>>{}", verificationCode);

                // 인증번호 이메일 발송
                emailService.sendVerificationCode(employeeEmail, verificationCode);

                // 결과 배열 생성 및 값 담기
                String[] resultArray = new String[]{verificationCode, employeeEmail, id, name};

                HttpStatus httpStatus = HttpStatus.OK;
                ResponseDTO responseDTO = new ResponseDTO(httpStatus, "아이디 찾기 성공", resultArray);
                return ResponseEntity.status(httpStatus).body(responseDTO);
            }

            // Employee 정보나 아이디가 없을 경우
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "사용자 정보 또는 아이디를 찾을 수 없습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        } catch (Exception e) {
            log.error("Error while finding employee ID: {}", e.getMessage());
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "아이디 찾기 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        }
    }

    //사원 계정 비활성화 신청
    @Operation(summary = "사원 비활성화 신청 요청", description = "사원의 계정 비활성화를 신청합니다.", tags = {"MemberController"})
    @PostMapping("/employeeOut")
    public ResponseEntity<ResponseDTO> employeeOut(@RequestBody RequestBodyTypeDTO requestBody) {

        EmployeeAndDepartmentAndPositionDTO employee = requestBody.getEmployee();
        MemberDTO member = requestBody.getMember();

        log.info("employee===========================> {}", employee);
        log.info("member===========================> {}", member);
        log.info("getName===========================> {}", employee.getName());
        log.info("getDepartment===========================> {}", employee.getDepartment());
        log.info("getDepartment===========================> {}", employee.getPosition());

        int memberNo = member.getNo();

        EmployeeAndDepartmentAndPosition foundEmployee = memberService.findEmployee(memberNo);

        log.info("foundEmployee===========================> {}", foundEmployee);

        //퇴사일과 사유 insert
        foundEmployee.setEmployeeOut(employee.getEmployeeOut());
        foundEmployee.setReason(employee.getReason());

        // Save the updated employee
        memberService.saveEmployee(foundEmployee);

        HttpStatus httpStatus = HttpStatus.OK;
        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "직원 계정 비활성화 신청 성공", foundEmployee);
        return ResponseEntity.status(httpStatus).body(responseDTO);
    }

    //직원 계정 비활성화
    @Operation(summary = "직원 계정 비활성화 시작 요청", description = "계정 비활성화를 시작합니다.", tags = {"MemberController"})
    @PostMapping("/employeeOutGo")
    public ResponseEntity<ResponseDTO> employeeOutGo(@RequestBody EmployeeAndDepartmentAndPositionDTO employee) {
        try {
            log.info("employee===========================> {}", employee);
            log.info("getEmployeeNo===========================> {}", employee.getEmployeeNo());
            log.info("getName===========================> {}", employee.getName());

            int employeeNo =  employee.getEmployeeNo();

            // 직원 번호로 멤버 정보 찾기
            EmployeeAndDepartmentAndPosition foundEmployee = memberService.findEmployeeByNo(employeeNo);
            log.info("foundEmployee>>>>>>>>>>>>>>>>>>>>>>{}", foundEmployee);

            if (foundEmployee != null) {

                // 계정 상태값 변경해주기
                foundEmployee.getMember().setState("N");
                log.info("setState>>>>>>>>>>>>>>>>>>>>>>{}", foundEmployee);

                memberService.saveEmployee(foundEmployee);


                HttpStatus httpStatus = HttpStatus.OK;
                ResponseDTO responseDTO = new ResponseDTO(httpStatus, "계정 비활성화 성공", null);
                return ResponseEntity.status(httpStatus).body(responseDTO);
            }
            // Employee 정보나 아이디가 없을 경우
            HttpStatus httpStatus = HttpStatus.NOT_FOUND;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "사용자 정보 또는 아이디를 찾을 수 없습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        } catch (Exception e) {
            log.error("Error while finding employee ID: {}", e.getMessage());
            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            ResponseDTO responseDTO = new ResponseDTO(httpStatus, "아이디 찾기 중 오류가 발생했습니다.", null);
            return ResponseEntity.status(httpStatus).body(responseDTO);
        }


    }

    // 한명회원 정보 조회
    @Operation(summary = "마이페이지에 불러올 사원의 정보 조회(프로필 사진 포함)", description = "사원 한명의 정보를 조회합니다.(프로필 사진 포함)", tags = {"MemberController" })
    @GetMapping("/{memberNo}")
    public ResponseEntity<ResponseDTO> getEmployeeOne(@PathVariable int memberNo) {
        System.out.println("check ==========================");
        EmployeeAndDepartmentAndPositionDTO employeeDTOList = memberService.findEmployeeId(memberNo);

        HttpStatus httpStatus = HttpStatus.OK;

        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "사원 조회 성공", employeeDTOList);

        return ResponseEntity.status(httpStatus).body(responseDTO);
    }

    // 한명 매장 정보 조회
    @Operation(summary = "마이페이지에 불러올 매장 정보 조회", description = "하나의 매장의 정보를 조회합니다.", tags = {"MemberController" })
    @GetMapping("store/{memberNo}")
    public ResponseEntity<ResponseDTO> getStoreOne(@PathVariable int memberNo) {

        System.out.println("check ==========================");
        StoreInfoDTO storeInfo = memberService.findStoreId(memberNo);
        HttpStatus httpStatus = HttpStatus.OK;

        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "매장 조회 성공", storeInfo);

        return ResponseEntity.status(httpStatus).body(responseDTO);
    }



    @GetMapping("/selectEmployee")
    public ResponseEntity<ResponseDTO> findbyAllEmployee(){

        return ResponseEntity.ok().body(new ResponseDTO(HttpStatus.OK, "모든 회원 조회 성공", memberService.findbyAllEmployee()));
    }

}
