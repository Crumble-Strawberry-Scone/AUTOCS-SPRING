package com.css.autocsfinal;

import com.css.autocsfinal.main.dto.TodoDTO;
import com.css.autocsfinal.main.repository.TodoRepository;
import com.css.autocsfinal.main.service.TodoService;
import com.css.autocsfinal.market.dto.StoreInfoDTO;
import com.css.autocsfinal.market.entity.StoreInfo;
import com.css.autocsfinal.market.repository.StoreInfoRepository;
import com.css.autocsfinal.member.dto.EmployeeAndDepartmentAndPositionDTO;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.EmployeeAndDepartmentAndPositionRepository;
import com.css.autocsfinal.member.repository.MemberRepository;
import com.css.autocsfinal.mypage.dto.MemberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO;
import com.css.autocsfinal.mypage.dto.MemberFileDTO;
import com.css.autocsfinal.mypage.entity.MemberAndEmployeeAndDepartmentAndPositionAndMemberFile;
import com.css.autocsfinal.mypage.entity.MemberFile;
import com.css.autocsfinal.mypage.repository.MemberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository;
import com.css.autocsfinal.mypage.repository.MemberFileRepository;
import com.css.autocsfinal.schedule.entity.Schedule;
import com.css.autocsfinal.schedule.entity.ScheduleDTO;
import com.css.autocsfinal.schedule.repository.ScheduleRepository;
import com.css.autocsfinal.util.FileUploadUtils;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.repository.query.Param;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;



@SpringBootTest
class AutocsFinalApplicationTests {

    @Autowired
    private TodoService todoService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TodoRepository todoRepository;

    @Autowired
    private MemberFileRepository memberFileRepository;

    @Autowired
    private EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository;


    @Autowired
    public MemberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    public ScheduleRepository scheduleRepository;

    @Test
    void contextLoads() {
    }


    /* 이미지 저장 할 위치 및 응답 할 이미지 주소 */
    @Value("${image.image-dir}")
    private String IMAGE_DIR;
    @Value("${image.image-url}")
    private String IMAGE_URL;


    @Test
    void test() {
        // 입력테스트
        // Given


        MemberFile memberfile = new MemberFile();
        Member member = new Member();
        member.setNo(240);
        memberfile.setMember(member);
//            Todo todo1 = new Todo();
//            memberfile.setRegDate(new Date());
        memberfile.setOriginName("파일 내용");
//            memberfile.setFileNo(2);
        memberFileRepository.save(memberfile);


        // When


        // Then

    }


    @Test
    void test2() {
        //값 변경 테스트
        // Given

        MemberFile memberfile = new MemberFile();
        Member member = new Member();
        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = new EmployeeAndDepartmentAndPosition();
        member.setNo(40);
        employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findByMemberNo(member.getNo());
//        memberFileRepository.findByMemberNo(member.getNo());
        memberfile = memberFileRepository.findByMemberNo(member.getNo());
        ModelMapper modelMapper = new ModelMapper();
        MemberFileDTO file = modelMapper.map(memberfile, MemberFileDTO.class);


        System.out.println("file = " + file);


        // When


        // Then

    }

    @Test
    @Transactional
    void test3() {

        EmployeeAndDepartmentAndPosition employee = employeeAndDepartmentAndPositionRepository.findByMemberNo(41);
        System.out.println("employee.getMember().getNo() = " + employee.getMember().getNo());
//        MemberAndEmployeeAndDepartmentAndPositionAndMemberFile empinfoList = memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository.findByMemberNo(employee.getMember().getNo());
        // Employee 엔티티 리스트를 EmployeeDTO 리스트로 변환하여 반환
        // Employee 엔티티 리스트를 EmployeeDTO 리스트로 변환하여 반환
//        System.out.println("empinfoList = " + empinfoList);
//                    MemberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO = new MemberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO();

//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setEmployeeNo(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getEmployeeNo());
//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setName(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getName());
//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setEmployeeJoin(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getEmployeeJoin());
//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setDepartment(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getDepartment().getName());
//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setPosition(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getPosition().getName());
//                    memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setEmployeeEmail(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getEmployeeEmail());
//
//                    // memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getMember()가 null이 아닌 경우에만 setMemberNo 호출
//                    if (memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getMember() != null) {
//                        memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setMemberNo(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getMember().getNo());
//                        memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setFileNo(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getMemberFile().getFileNo());
//                        memberAndEmployeeAndDepartmentAndPositionAndMemberFileDTO.setOriginName(memberAndEmployeeAndDepartmentAndPositionAndMemberFile.getMemberFile().getOriginName());



    }


    @Test
    @Transactional
    void test4 () {


        int memberNo = 41;
        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findByMemberNo(memberNo);
        System.out.println("memberNo = " + memberNo);

        int result = 0;

        if(employeeAndDepartmentAndPosition != null){
            employeeAndDepartmentAndPosition.setEmployeePhone("010-1234-1234");
            employeeAndDepartmentAndPosition.setEmployeeEmail("jisu@gmail.com");
            System.out.println("getNo = " + employeeAndDepartmentAndPosition.getMember().getNo());
            System.out.println("etEmployeeEmail = " + employeeAndDepartmentAndPosition.getEmployeeEmail());
            System.out.println("getEmployeePhone = " + employeeAndDepartmentAndPosition.getEmployeePhone());
            System.out.println("employeeAndDepartmentAndPosition = " + employeeAndDepartmentAndPosition);


            result =1;
        } else {
//            log.info("[MypageService] uupdateMemberInfo 실패 ");
            new RuntimeException();
        }
    }


    @Test
    void test5() {
        // 비밀번호 찾기 테스트
        Member member = memberRepository.findByNo(41);
        System.out.println("member ==================================== " + member);
        System.out.println("member.getPwd() ===================================== " + member.getPwd());


        String memberpwd = member.getPwd();
        String checkpwd = "poyrdK9Ik7";
        //입력한 pwd가 먼저 와있어야 하고 암호화 되어있는 비밀번호가 뒤에 와야한다.
        boolean checkpws = passwordEncoder.matches( checkpwd,memberpwd);

        System.out.println("checkpws ========================================================= " + checkpws);
    }


    @Test
    void changePw (){

        //given
        int memberNo = 240;
        String newPw = "a1234";
        String encodedPassword = passwordEncoder.encode(newPw);
        Member member = memberRepository.findByNo(memberNo);
        System.out.println("[MypageService] changePwd member : {}");
        System.out.println("member = " + member);
        System.out.println("member.getPwd() = " + member.getPwd());
        int result = 0;

//        if(newPw == null) {
            member.setPwd(encodedPassword);
            System.out.println("member.getPwd()11111111 = " + member.getPwd());
            memberRepository.save(member);
            result = 1;
    }

    @Test
    void insertIMG () {

        int memberNo = 220;
//        String imgurl = "3695ab85677d4adf80691f6c31474c1a.jpg";
        MemberFile memberfile = new MemberFile();
        Member member = memberRepository.findByNo(memberNo);
        memberfile.setMember(member);


        memberfile.setOriginName("3695ab85677d4adf80691f6c31474c1a.jpg");
        memberFileRepository.save(memberfile);
        System.out.println();



    }

    @Test
    void getmemberInfo () {

        int memberNo = 240;
        int num = memberFileRepository.findMaxMemberFileNo(memberNo);

        System.out.println("EmployeeAndDepartmentAndPosition = " + num);

    }

    @Autowired
    private StoreInfoRepository storeInfoRepository;

    @Test
    void getStoreInfo() {


        int memberNo = 220;

        StoreInfo storeInfo = storeInfoRepository.findByMemberNo(memberNo);
        Integer fileNo = memberFileRepository.findMaxMemberFileNo(memberNo);
        Optional<MemberFile> memberFile = memberFileRepository.findById(fileNo);
        StoreInfoDTO storeInfoDTO = new StoreInfoDTO();
        storeInfoDTO.setStoreFile(memberFile.get().getOriginName());
        storeInfoDTO.setStoreNo(storeInfo.getStoreNo());
        storeInfoDTO.setName(storeInfo.getName());
        storeInfoDTO.setEmail(storeInfo.getEmail());
        storeInfoDTO.setAddress(storeInfo.getAddress());
        storeInfoDTO.setPhone(storeInfo.getPhone());
        storeInfoDTO.setId(storeInfo.getMember().getId());
        storeInfoDTO.setPwd(storeInfo.getMember().getPwd());
        storeInfoDTO.setRole(storeInfo.getMember().getRole());
        storeInfoDTO.setDetailAddress(storeInfo.getDetailAddress());
//        storeInfoDTO.setRefMemberNo(storeInfo.getMember().getNo());
        storeInfoDTO.setLicense(storeInfo.getLicense());

        System.out.println("memberFile = " + storeInfoDTO.getStoreFile());
        System.out.println("storeInfoDTO = " + storeInfoDTO);

        System.out.println("storeInfo = " + storeInfo);

    }

    @Test
 void calendertest () {

        int num  = 354;
        Member member = memberRepository.findByNo(num);
        Schedule schedule = new Schedule();
        schedule.setContent("부산지역 가맹점 시설 점검");
        schedule.setName("부산지역 가맹점 시설 점검");
        schedule.setPlace("부산");
        schedule.setEndDate(new Date());
        schedule.setStartDate(new Date());
        schedule.setMember(member);

        scheduleRepository.save(schedule);


    }


    // 멤버 일정가지고 오기
    @Test
    void getCalender() {

        int num =240;
        List<Schedule> schedules = scheduleRepository.findByMemberNo(num);
        List<ScheduleDTO> sclist  = schedules.stream().map(schedule -> {
                ScheduleDTO sc = new ScheduleDTO();
        sc.setName(schedule.getName());
        sc.setScheduleCode(schedule.getScheduleCode());
        sc.setContent(schedule.getContent());
        sc.setStartDate(schedule.getStartDate());
        sc.setMemberNo(schedule.getMember().getNo());
        return sc;
        }).collect(Collectors.toList());



        System.out.println("schedule = " + sclist);
    }

}


