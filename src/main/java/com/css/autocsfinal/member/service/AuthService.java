package com.css.autocsfinal.member.service;

import com.css.autocsfinal.exception.LoginFailedException;
import com.css.autocsfinal.jwt.TokenProvider;
import com.css.autocsfinal.market.dto.StoreInfoDTO;
import com.css.autocsfinal.market.entity.StoreInfo;
import com.css.autocsfinal.market.repository.StoreInfoRepository;
import com.css.autocsfinal.member.dto.EmployeeAndDepartmentAndPositionDTO;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.dto.TokenDTO;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.EmployeeAndDepartmentAndPositionRepository;
import com.css.autocsfinal.member.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;

    private final PasswordEncoder passwordEncoder;

    private final TokenProvider tokenProvider;

    private final ModelMapper modelMapper;

    private EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository;

    private final StoreInfoRepository storeInfoRepository;


    public AuthService(MemberRepository memberRepository
            , PasswordEncoder passwordEncoder
            , TokenProvider tokenProvider
            , ModelMapper modelMapper
            , EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository,
                       StoreInfoRepository storeInfoRepository){
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.modelMapper = modelMapper;
        this.employeeAndDepartmentAndPositionRepository = employeeAndDepartmentAndPositionRepository;
        this.storeInfoRepository = storeInfoRepository;
    }

    public TokenDTO login(MemberDTO memberDTO) {

        log.info("[AuthService] login Start ==================================");
        log.info("[AuthService] {} ================== ", memberDTO);
        /* 1. 아이디 조회 */
        Member member = memberRepository.findById(memberDTO.getId());

        // 1-1. 같이 토큰에 넣어줄 직원 정보 조회
        EmployeeAndDepartmentAndPosition employee = employeeAndDepartmentAndPositionRepository.findByMemberNo(member.getNo());
        // DTO로 변환
        EmployeeAndDepartmentAndPositionDTO employeeDTO = new EmployeeAndDepartmentAndPositionDTO();
        employeeDTO.setEmployeeNo(employee.getEmployeeNo());
        employeeDTO.setName(employee.getName());
        employeeDTO.setEmployeeJoin(employee.getEmployeeJoin());
        employeeDTO.setEmployeeEmail(employee.getEmployeeEmail());
        employeeDTO.setEmployeePhone(employee.getEmployeePhone());
        //employeeDTO.setEmployeeManager(employee.getEmployeeManager());
        employeeDTO.setDepartment(employee.getDepartment().getName());
        employeeDTO.setPosition(employee.getPosition().getName());


        log.info("[AuthService] member 조회 {} ================== ", member);
        log.info("[AuthService] member의 employee 조회 {} ================== ", employee);

        if(member == null){
            throw new LoginFailedException(memberDTO.getId() + "를 찾을 수 없습니다.");
        }

        /* 2. 비밀번호 매칭
         * passwordEncoder.matches(평문, 다이제스트)
         * */
        if(!passwordEncoder.matches(memberDTO.getPwd(), member.getPwd())){

            log.info("[AuthService] memberDTO.getPwd()! {}", memberDTO.getPwd());
            log.info("[AuthService] member.getPwd())! {}", member.getPwd());

            log.info("[AuthService] Password Match Fail! ");
            throw new LoginFailedException("잘못된 비밀번호 입니다.");
        }

        /* 3. 토큰 발급 */
        TokenDTO tokenDTO = tokenProvider.generateTokenDTO(member, employee);
        log.info("[AuthService] tokenDTO {} =======> ", tokenDTO);

        log.info("[AuthService] login End ==================================");

        return tokenDTO;
    }

    public TokenDTO login2(MemberDTO memberDTO) {

        log.info("[AuthService] login Start ==================================");
        log.info("[AuthService] {} ================== ", memberDTO);
        /* 1. 아이디 조회 */
        Member member = memberRepository.findById(memberDTO.getId());

        // 1-1. 같이 토큰에 넣어줄 영업점 정보 조회
        StoreInfo store = storeInfoRepository.findByMemberNo(member.getNo());
        // DTO로 변환
        StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
        storeInfoDTO.setStoreNo(store.getStoreNo());
        storeInfoDTO.setName(store.getName());


        log.info("[AuthService] store 조회 {} ================== ", member);
        log.info("[AuthService] member의 store 조회 {} ================== ", store);

        if(member == null){
            throw new LoginFailedException(memberDTO.getId() + "를 찾을 수 없습니다.");
        }

        /* 2. 비밀번호 매칭
         * passwordEncoder.matches(평문, 다이제스트)
         * */
        if(!passwordEncoder.matches(memberDTO.getPwd(), member.getPwd())){
            log.info("[AuthService] Password Match Fail! ");
            throw new LoginFailedException("잘못된 비밀번호 입니다.");
        }

        /* 3. 토큰 발급 */
        TokenDTO tokenDTO = tokenProvider.generateTokenDTO2(member, store);
        log.info("[AuthService] tokenDTO {} =======> ", tokenDTO);

        log.info("[AuthService] login End ==================================");

        return tokenDTO;
    }
}
