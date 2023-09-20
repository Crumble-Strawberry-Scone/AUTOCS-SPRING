package com.css.autocsfinal.market.service;

import com.css.autocsfinal.market.dto.*;
import com.css.autocsfinal.market.entity.*;
import com.css.autocsfinal.market.repository.*;
import com.css.autocsfinal.member.dto.MemberDTO;
import com.css.autocsfinal.member.entity.Member;
import com.css.autocsfinal.member.repository.MemberRepository;
import com.css.autocsfinal.util.FileUploadUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MarketService {

    private final BCryptPasswordEncoder passwordEncoder;

    /* 이미지 저장 할 위치 및 응답 할 이미지 주소 */
    @Value("${image.image-dir2}")
    private String IMAGE_DIR;

    @Value("${image.image-url2}")
    private String IMAGE_URL;

    private final ModelMapper modelMapper;

    private final MarketRepository marketRepository;

    private final MarketApplyRepository marketApplyRepository;

    private final ApplyFormNApplyFileRepository applyFormNApplyFileRepository;

    private final MemberRepository memberRepository;

    private final EmailService emailService;

    private final StoreInfoRepository storeInfoRepository;

    private final FindRepository findRepository;

    private final OutFileRepository outFileRepository;

    private final OutFileAndStoreRepository outFileAndStoreRepository;

    private final StoreInfo2Repository storeInfo2Repository;

    public MarketService(ModelMapper modelMapper, MarketRepository marketRepository, MarketApplyRepository marketApplyRepository, ApplyFormNApplyFileRepository applyFormNApplyFileRepository, MemberRepository memberRepository, BCryptPasswordEncoder passwordEncoder, EmailService emailService, StoreInfoRepository storeInfoRepository, FindRepository findRepository, OutFileRepository outFileRepository, OutFileAndStoreRepository outFileAndStoreRepository, StoreInfo2Repository storeInfo2Repository) {
        this.modelMapper = modelMapper;
        this.marketRepository = marketRepository;
        this.marketApplyRepository = marketApplyRepository;
        this.applyFormNApplyFileRepository = applyFormNApplyFileRepository;
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.storeInfoRepository = storeInfoRepository;
        this.findRepository = findRepository;
        this.outFileRepository = outFileRepository;
        this.outFileAndStoreRepository = outFileAndStoreRepository;
        this.storeInfo2Repository = storeInfo2Repository;
    }

    @Transactional
    public String insertApply(ApplyFormDTO applyFormDTO, MultipartFile fileImage) {
        log.info("[MarketService] 영업점 신청폼 Insert Start ===================");
        log.info("[MarketService] applyDTO {} =======> " + applyFormDTO);
        log.info("[MarketService] fileImage {} =======> " + fileImage);

        String imageName = UUID.randomUUID().toString().replace("-", "");
        String replaceFileName = null;
        int result = 0; // 결과에 따른 값을 구분하기 위한 용도의 변수

        try {
            replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, fileImage);

            Integer maxFileNo = marketRepository.findMaxFileNo();
            int newFileNo = (maxFileNo != null) ? maxFileNo + 1 : 1;

            log.info("[ProductService] insert Image Name : {}", replaceFileName);

            // ApplyFileDTO 객체 생성 및 값 설정
            ApplyFileDTO applyFileDTO = new ApplyFileDTO();
            applyFileDTO.setFileNo(newFileNo);

            // ApplyFile 엔티티로 매핑하고 저장
            ApplyFile insertForm = modelMapper.map(applyFileDTO, ApplyFile.class);
            insertForm.setOrignal(replaceFileName);
            insertForm.setChange("표준가맹계약서" + newFileNo);
            insertForm.setRegistDate(Date.valueOf(LocalDate.now()));
            insertForm.setKine("표준가맹계약서");

            ApplyFile savedFile = marketRepository.save(insertForm);

            Integer savedFileNo = savedFile.getFileNo();


            // ApplyFormAndApplyFile 객체 생성 및 매핑
            ApplyFormAndApplyFile insertMarketForm = modelMapper.map(applyFormDTO, ApplyFormAndApplyFile.class);
            insertMarketForm.setFileNo(savedFileNo);
            insertMarketForm.setState("W");

            // ApplyFormAndApplyFile 객체 생성 및 매핑
            ApplyFormAndApplyFile applyForm = marketApplyRepository.save(insertMarketForm);

            log.info("[MarketService] insertApply End ===================");
            return (applyForm != null) ? "영업점 신청폼 등록 성공" : "영업점 신청폼 등록 실패";
        } catch (Exception e) {
            log.error("Error while inserting apply form: {}", e.getMessage());
            FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
            throw new RuntimeException(e);
        }
    }

    //영업점 계정 발급 대기 중인 것 조회
    public List<ApplyFormNApplyFileDTO> getMarketStateW() {
        log.info("[MarketService] 계정 발급 전 영업점 신청 조회 Start ===================");

        List<ApplyFormNApplyFile> marketList = applyFormNApplyFileRepository.findAll();
        log.info("marketList : " + marketList);

        List<ApplyFormNApplyFileDTO> marketDTOList = marketList.stream()
                .map(applyFormNApplyFile -> {
                    ApplyFormNApplyFileDTO applyFormNApplyFileDTO = new ApplyFormNApplyFileDTO();

                    applyFormNApplyFileDTO.setApplyNo(applyFormNApplyFile.getApplyNo());
                    applyFormNApplyFileDTO.setName(applyFormNApplyFile.getName());
                    applyFormNApplyFileDTO.setAddress(applyFormNApplyFile.getAddress());
                    applyFormNApplyFileDTO.setDetailAddress(applyFormNApplyFile.getDetailAddress());
                    applyFormNApplyFileDTO.setRegistDate(applyFormNApplyFile.getFile().getRegistDate());
                    applyFormNApplyFileDTO.setEmail(applyFormNApplyFile.getEmail());
                    applyFormNApplyFileDTO.setLicense(applyFormNApplyFile.getLicense());
                    applyFormNApplyFileDTO.setState(applyFormNApplyFile.getState());
                    applyFormNApplyFileDTO.setFileUrl(IMAGE_URL + applyFormNApplyFile.getFile().getOrignal());

                    return applyFormNApplyFileDTO;
                })
                .collect(Collectors.toList());

        return marketDTOList;
    }

    //영업점 계정 생성
    @Transactional
    public String insertIdPwd(MemberDTO memberDTO, StoreInfo2DTO storeInfo2DTO) {
        log.info("[MarketService] 아이디 비밀번호 발급 Start ===================");
        log.info("[AuthService] memberDTO {} =======> " + memberDTO);

        Member registMember = modelMapper.map(memberDTO, Member.class);

        // 아이디 생성
        int nextMemberCode = memberRepository.maxMemberCode() + 1;
        String newUserId = "Store" + nextMemberCode;

        // 랜덤한 비밀번호 생성
        String newPassword = generateRandomPassword();

        log.info("암호화 전 비밀번호 ============================>>>>>>>>>>>>>>>>>>>>>>>>>>> " + newPassword);

        // 아이디와 임시 비밀번호를 이메일로 전송
        String storeEmail = storeInfo2DTO.getEmail();
        emailService.sendEmail(storeEmail, newUserId, newPassword);

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(newPassword);

        registMember.setId(newUserId);
        registMember.setPwd(encodedPassword);
        registMember.setState("Y");
        registMember.setRole("STORE");

        Member result = memberRepository.save(registMember);

        log.info("[AuthService] MarketInsert Result {}", (result != null) ? "계정 생성 성공" : "계정 생성 실패");

        return (result != null) ? "계정 생성 성공" : "계정 생성 실패";
    }

    private String generateRandomPassword() {
        int length = 10;
        boolean useLetters = true;
        boolean useNumbers = true;
        return RandomStringUtils.random(length, useLetters, useNumbers);
    }

    //영업점 Info등록
    @Transactional
    public String insertMarket(StoreInfo2DTO storeInfo2DTO) {
        log.info("[MarketService] 영업점 등록 Start ===================");
        log.info("[MarketService] storeInfo2DTO : " + storeInfo2DTO);

        // 최신 Member 코드 조회
        Integer maxMemberCode = memberRepository.maxMemberCode();
        log.info("maxMemberCode================> {}", maxMemberCode);

        storeInfo2DTO.setMemberNo(maxMemberCode);

        StoreInfo2 insertMarket = modelMapper.map(storeInfo2DTO, StoreInfo2.class);
        log.info("================> {}", insertMarket);

        StoreInfo2 savedStore = storeInfo2Repository.save(insertMarket);

        // license 번호를 사용하여 ApplyForm 조회
        ApplyFormNApplyFile applyForm = applyFormNApplyFileRepository.findByLicense(storeInfo2DTO.getLicense());

        // ApplyForm 상태 변경
        if (applyForm != null) {
            applyForm.setState("O");
            applyFormNApplyFileRepository.save(applyForm);
        }

        log.info("[MarketService] insertMarket End ===================");
        return (savedStore != null) ? "영업점 등록 성공" : "영업점 등록 실패";
    }

    // Store 정보 조회(아이디 찾기)
    public StoreInfoDTO findStoreInfoByNameAndEmail(String email, String name) {

        log.info("email ===================> {}", email);
        log.info("name ===================> {}", name);

        // 이름과 이메일 위치 변경(값이 왜 반대로 들어오는거야)
        String chang = email;
        email = name;
        name = chang;

        StoreInfo store = storeInfoRepository.findByEmailAndName(email, name);

        log.info("store ===================> {}", store);
        if (store != null) {
            StoreInfoDTO storeInfoDTO = modelMapper.map(store, StoreInfoDTO.class);
            log.info("storeInfoDTO ===================> {}", storeInfoDTO);
            return storeInfoDTO;
        }
        return null;
    }

    //영업점 계정 비활성화 신청폼 등록
    @Transactional
    public String insertOut(StoreInfoDTO storeInfoDTO, MultipartFile fileImage) {
        log.info("[MarketService] 영업점 계정 비활성화 신청폼 Insert Start ===================");
        log.info("[MarketService] storeInfoDTO {} =======> " + storeInfoDTO);
        log.info("[MarketService] fileImage {} =======> " + fileImage);

        String imageName = UUID.randomUUID().toString().replace("-", "");
        String replaceFileName = null;
        int result = 0; // 결과에 따른 값을 구분하기 위한 용도의 변수

        try {
            replaceFileName = FileUploadUtils.saveFile(IMAGE_DIR, imageName, fileImage);

            log.info("[ProductService] insert Image Name : {}", replaceFileName);

            // OutFileDTO 객체 생성 및 값 설정
            OutFileDTO outFileDTO = new OutFileDTO();
            OutFile insertFile = modelMapper.map(outFileDTO, OutFile.class);

            // OutFile 엔티티로 매핑하고 저장
            if(replaceFileName != null) {
                insertFile.setOrignal(replaceFileName);
            }
            insertFile.setChange("계약 종료/해지 확인서" + storeInfoDTO.getStoreNo());
            insertFile.setRegistDate(Date.valueOf(LocalDate.now()));
            insertFile.setKine("계약 종료/해지 확인서");
            insertFile.setState('W');
            insertFile.setStoreNo(storeInfoDTO.getStoreNo());

            OutFile savedFile = outFileRepository.save(insertFile);

            log.info("[MarketService] insertOut End ===================");
            return (savedFile != null) ? "영업점 계정 비활성화 신청폼 등록 성공" : "영업점 계정 비활성화 신청폼 등록 실패";
        } catch (Exception e) {
            log.error("Error while inserting apply form: {}", e.getMessage());
            FileUploadUtils.deleteFile(IMAGE_DIR, replaceFileName);
            throw new RuntimeException(e);
        }
    }

    //영업점 계정 비활성화 대기 중인 것 조회
    public List<StoreAndOutDTO> getOutMarketStateW() {
        log.info("[MarketService] 계정 비활성화 전 영업점 신청 조회 Start ===================");

        List<StoreAndOut> marketList = outFileAndStoreRepository.findAll();
        log.info("marketList : " + marketList);

        List<StoreAndOutDTO> marketDTOList = marketList.stream()
                .map(StoreAndOut -> {
                    StoreAndOutDTO storeAndOutDTO = new StoreAndOutDTO();

                    storeAndOutDTO.setOutFileNo(StoreAndOut.getFileNo());
                    storeAndOutDTO.setName(StoreAndOut.getStore().getName());
                    storeAndOutDTO.setAddress(StoreAndOut.getStore().getAddress());
                    storeAndOutDTO.setDetailAddress(StoreAndOut.getStore().getDetailAddress());
                    storeAndOutDTO.setRegistDate(StoreAndOut.getRegistDate());
                    storeAndOutDTO.setEmail(StoreAndOut.getStore().getEmail());
                    storeAndOutDTO.setLicense(StoreAndOut.getStore().getLicense());
                    storeAndOutDTO.setState(StoreAndOut.getState());
                    storeAndOutDTO.setFileUrl(IMAGE_URL + StoreAndOut.getOrignal());
                    storeAndOutDTO.setRefMemberNo(StoreAndOut.getStore().getMember().getNo());
                    storeAndOutDTO.setRefStoreNo(StoreAndOut.getStore().getStoreNo());

                        return storeAndOutDTO;
                    })
                    .collect(Collectors.toList());

            return marketDTOList;
        }
    }