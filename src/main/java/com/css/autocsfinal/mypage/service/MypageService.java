package com.css.autocsfinal.mypage.service;


import com.css.autocsfinal.market.dto.StoreInfoDTO;
import com.css.autocsfinal.market.entity.StoreInfo;
import com.css.autocsfinal.market.repository.StoreInfo2Repository;
import com.css.autocsfinal.market.repository.StoreInfoRepository;
import com.css.autocsfinal.member.dto.EmployeeAndDepartmentAndPositionDTO;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.EmployeeAndDepartmentAndPositionRepository;
import com.css.autocsfinal.member.repository.EmployeeRepository;
import com.css.autocsfinal.member.repository.MemberRepository;
import com.css.autocsfinal.mypage.dto.MemberFileDTO;
import com.css.autocsfinal.mypage.entity.MemberFile;
import com.css.autocsfinal.mypage.repository.MemberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository;
import com.css.autocsfinal.mypage.repository.MemberFileRepository;
import com.css.autocsfinal.util.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;


@Service
@Slf4j
public class MypageService {

    @Value("${image.image-dir2}")
    private String IMAGE_DIR;

    @Value("${image.image-url2}")
    private String IMAGE_URL;



    private final MemberRepository memberRepository;
    public final MemberFileRepository memberFileRepository;
    public final PasswordEncoder passwordEncoder;
    public final MemberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository;
    public final EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository;
    public final StoreInfoRepository storeInfoRepository;
    public final StoreInfo2Repository storeInfo2Repository;
    public final ModelMapper modelMapper;
    public MypageService(MemberFileRepository memberFileRepository, EmployeeRepository employeeRepository, MemberRepository memberRepository, EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository, PasswordEncoder passwordEncoder, MemberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository, EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository1, StoreInfoRepository storeInfoRepository, StoreInfo2Repository storeInfo2Repository, ModelMapper modelMapper) {
        this.memberFileRepository = memberFileRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository = memberAndEmployeeAndDepartmentAndPositionAndMemberFileRepository;
        this.employeeAndDepartmentAndPositionRepository = employeeAndDepartmentAndPositionRepository1;
        this.storeInfoRepository = storeInfoRepository;
        this.storeInfo2Repository = storeInfo2Repository;
        this.modelMapper = modelMapper;
    }






    @Transactional
    public String updateMemberInfo(EmployeeAndDepartmentAndPositionDTO employeeAndDepartmentAndPositionDTO , MultipartFile fileImage)  {
            // 멤버 번호 담기
            int memberNo = employeeAndDepartmentAndPositionDTO.getMemberNo();
            log.info("[MypageService] updateMemberInfo memberNo : {} " , memberNo);
            //사진파일 매핑
            String imageName = UUID.randomUUID().toString().replace("-", "");
            String replaceFileName = null;

            EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findByMemberNo(memberNo);
            log.info("[MypageService] updateMemberInfo Start ======================================");
            log.info("[MypageService] updateMemberInfo fileImage : {} " , fileImage);
            int result = 0;

            try {
                if(fileImage != null ) {
                    MemberFile memberFile = new MemberFile();
                    replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, fileImage);
                    Member member = memberRepository.findByNo(employeeAndDepartmentAndPositionDTO.getMemberNo());
                    memberFile.setMember(member);
                    memberFile.setOriginName(replaceFileName);
                    memberFile.setRegDate(LocalDate.now());
                    MemberFile saveFile = memberFileRepository.save(memberFile);
                }

                // 값이 null 또는 빈 문자열이 아닌 경우에만 저장
                if (employeeAndDepartmentAndPositionDTO.getEmployeePhone()!= null && !employeeAndDepartmentAndPositionDTO.getEmployeePhone().trim().isEmpty()) {
                    employeeAndDepartmentAndPosition.setEmployeePhone(employeeAndDepartmentAndPositionDTO.getEmployeePhone());
                    log.info("[MypageService]  getEmployeePhone : {}", employeeAndDepartmentAndPosition.getEmployeePhone());
                }
                if (employeeAndDepartmentAndPositionDTO.getEmployeeEmail()!= null && !employeeAndDepartmentAndPositionDTO.getEmployeeEmail().trim().isEmpty()) {
                    employeeAndDepartmentAndPosition.setEmployeeEmail(employeeAndDepartmentAndPositionDTO.getEmployeeEmail());
                    log.info("[MypageService]  getEmployeeEmail : {}", employeeAndDepartmentAndPosition.getEmployeeEmail());
                }
                employeeAndDepartmentAndPositionRepository.save(employeeAndDepartmentAndPosition);


                log.info("[MypageService] updateMemberInfo 성공 ======================================");
                result =1;

            } catch (Exception e){

                log.info("[MypageService] updateMemberInfo 실패 ");
                FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
                throw new NullPointerException();

            }
        return (result > 0)? " 회원정보 수정 성공" : "회원정보 수정 실패";

        }

    // 비밀번호 체크
    @Transactional
    public String checkPwd(int memberNo ,String checkpw) {

        Member member = memberRepository.findByNo(memberNo);
        String pw = member.getPwd();  // 기존 비밀번호
        boolean result = passwordEncoder.matches(checkpw, pw);
        return result? "true" : "false";

    }

    @Transactional
    // 비밀번호 변경
    public String changePwd(int memberNo, String newPw) {

        int result = 0;
        Member member = memberRepository.findByNo(memberNo);
        String encodedPassword = passwordEncoder.encode(newPw);

        if(encodedPassword != null) {

            member.setPwd(encodedPassword);
            memberRepository.save(member);
            result = 1;

        }
        return result == 1 ? "true" : "false";
    }



    @Transactional
    public String updateImg(MemberFileDTO memberFileDTO, MultipartFile empImage) {
        log.info("[MypageService] updateImg Start ======================================");
        log.info("[MypageService] updateImg memberFileDTO ================================= {}", memberFileDTO );
        log.info("[MypageService] updateImg empImage ================================= {}", empImage );
        int memberNo = memberFileDTO.getMemberNo();
        MemberFile memberFile = new MemberFile();
        String imageName = UUID.randomUUID().toString().replace("-","");
        String replaceFileName = null;
            int result =0;


            try {

                replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName,empImage);
                Member member = memberRepository.findByNo(memberNo);
                System.out.println("member = " + member);
                memberFile.setMember(member);
                memberFile.setOriginName(replaceFileName);
                log.info("[MypageService] memberFile.getOriginName() ================================= {}", memberFile.getOriginName() );
                memberFile.setRegDate(LocalDate.now());
                memberFileRepository.save(memberFile);
                result = 1;


            } catch (Exception e){

                FileUploadUtils.deleteFile(IMAGE_DIR,replaceFileName);
                throw new RuntimeException(e);
            }
        log.info("[MypageService] updateImg End ======================================");

        return result == 1 ? "true" : "false";
    }

    public MemberFileDTO getMemberImg(int memberNo) {
        log.info("[TodoService] 이미지 조회하기 Start ===================");

        int fileNo = 0;
        Integer maxImgNo = memberFileRepository.findMaxMemberFileNo(memberNo);
        fileNo = maxImgNo;
        log.info("[TodoService] maxImgNo ===================", maxImgNo);
        MemberFile memberImg = memberFileRepository.findById(fileNo);

        MemberFileDTO memberFileDTO = new MemberFileDTO();
        memberFileDTO.setMemberNo(memberImg.getMember().getNo());
        memberFileDTO.setMemberFileNo(fileNo);
        memberFileDTO.setOriginName(IMAGE_URL+ memberImg.getOriginName());
        Date date = java.sql.Date.valueOf(memberImg.getRegDate());  // LocalDate 타입을 Date타입으로 변환
        memberFileDTO.setRegDate(date);
        log.info("[TodoService] 이미지 이름 ===================", memberFileDTO.getOriginName());
        log.info("[TodoService] 이미지 조회하기 end ===================");


        return memberFileDTO;
    }


    // 매장 정보 변경하기
    @Transactional
    public String updateStoreInfo(StoreInfoDTO storeInfoDTO) {
        int result = 0;

        // 멤버 번호 담기
        int memberNo = storeInfoDTO.getMemberNo();
        log.info("[MypageService] updateStoreInfo memberNo : {} " , memberNo);


        StoreInfo storeInfo = storeInfoRepository.findByMemberNo(memberNo);
        log.info("[MypageService] updateStoreInfo Start ======================================");
        log.info("[MypageService] =========================storeInfo : {} " , storeInfo);
//        try {
            String email = storeInfoDTO.getEmail();
            String phone = storeInfoDTO.getPhone();
            String address = storeInfoDTO.getAddress();
            String detailAddress = storeInfoDTO.getDetailAddress();

            // 값이 null 또는 빈 문자열이 아닌 경우에만 저장
            if (email != null && !email.trim().isEmpty()) {
                storeInfo.setEmail(email);
                log.info("[MypageService]  getEmployeePhone : {}", storeInfo.getEmail());
            }

            if (phone != null && !phone.trim().isEmpty()) {
                storeInfo.setPhone(phone);
            }

            if (address != null && !address.trim().isEmpty()) {
                storeInfo.setAddress(address);
            }

            if (detailAddress != null && !detailAddress.trim().isEmpty()) {
                storeInfo.setDetailAddress(detailAddress);
            }

            storeInfoRepository.save(storeInfo);

            // 저장 성공 시 result를 1로 설정
            result = 1;
//        } catch (Exception e){
//
//            log.info("[MypageService] updateMemberInfo 실패 ");
//            throw new RuntimeException(e);
//
//        }

        return result == 1 ? "true" : "false";
    }
}







