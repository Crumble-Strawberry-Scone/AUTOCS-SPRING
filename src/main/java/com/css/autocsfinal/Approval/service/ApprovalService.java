package com.css.autocsfinal.Approval.service;

import com.css.autocsfinal.Approval.dto.*;
import com.css.autocsfinal.Approval.entity.*;
import com.css.autocsfinal.Approval.repository.*;
import com.css.autocsfinal.common.Criteria;
import com.css.autocsfinal.member.entity.Employee;
import com.css.autocsfinal.member.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalService {

    private final ModelMapper modelMapper;
    private final ApprovalRepository approvalRepository;
    private final AppEmplRepository appEmplRepository;
    private final DocumentRepository documentRepository;
    private final PurchaseRepository purchaseRepository;
    private final DocumentFileRepository documentFileRepository;
    private final ReceiveRepository receiveRepository;
    private final ApproverRepository approverRepository;
    private final TrafficRepository trafficRepository;
    private final BusinessRepository businessRepository;
    private final EmployeeRepository employeeRepository;
    private final VacationRepository vacationRepository;
    private final PayRepository payRepository;
    private final ApprovalAndDocumentRepository approvalAndDocumentRepository;
    private final ReceiverAndDocumentRepository receiverAndDocumentRepository;
    private final BusinessDocRepository businessDocRepository;
    private final TrafficDocRepository trafficDocRepository;
    private final PurchaseDocRepository purchaseDocRepository;

    /* 결재선 가져오기 */
    public List<AppDeptResult> findDept() {

        log.info("[ApprovalService] findDept 접근 완료");

        List<AppDeptEntity> deptList = approvalRepository.findByUpperDeptCode("B1");

        List<AppDeptResult> results = new ArrayList<>();

        for(int i = 0; i < deptList.size(); i++) {
            AppDeptResult result = new AppDeptResult();
            result.setId(i + 1);
            result.setParent(0);
            result.setText(deptList.get(i).getName());

            results.add(result);
        }

        List<AppEmplEntity> emplList = appEmplRepository.findAll();

        for(int i = 0; i < emplList.size(); i++) {
            AppDeptResult result = new AppDeptResult();

            switch(emplList.get(i).getDeptCode()) {
                case "H1" : result.setId(deptList.size() + i + 1);
                            result.setParent(1);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
                case "F1" : result.setId(deptList.size() + i + 1);
                            result.setParent(2);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
                case "M1" : result.setId(deptList.size() + i + 1);
                            result.setParent(3);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
                case "M2" : result.setId(deptList.size() + i + 1);
                            result.setParent(4);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
                case "S1" : result.setId(deptList.size() + i + 1);
                            result.setParent(5);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
                case "S2" : result.setId(deptList.size() + i + 1);
                            result.setParent(6);
                            result.setText(emplList.get(i).getName() + " " + emplList.get(i).getPosition().getName());
                            result.setPosition(emplList.get(i).getPosition().getName());
                            result.setEmpNo(emplList.get(i).getEmployeeNo());
                            result.setName(emplList.get(i).getName());
                            results.add(result); break;
            }
        }

        return results;
    }

    /* 구매요청 */
    @Transactional
    public void registPurchase(PurchaseListDTO purchaseList, List<MultipartFile> files) {

        log.info("[ApprovalService] registPurchase purchaseList : {}", purchaseList);

        Date date = new Date();

        /* 문서테이블에 insert */

        int documentCode = documentInsertMethod(purchaseList.getEmpNo(), date, "구매요청", purchaseList.getDocumentTitle(), "결재요청");
//        DocumentEntity documentEntity = new DocumentEntity();
//        documentEntity.setEmployeeNo(purchaseList.getEmpNo());
//        documentEntity.setApplicationDate(date);
//        documentEntity.setDocumentType("구매요청");
//        documentEntity.setDocumentTitle(purchaseList.getDocumentTitle());
//        documentEntity.setStatus("결재요청");
//        documentRepository.save(documentEntity);

//        int documentCode = documentEntity.getDocumentCode();;

        /* 구매요청 테이블에 값 하나하나 빼서 insert*/
        for(int i = 0; i < purchaseList.getPrice().size(); i++) {

            PurchaseEntity purchaseEntity = new PurchaseEntity();

            purchaseEntity.setDocumentCode(documentCode);
            purchaseEntity.setProductName(purchaseList.getProductName().get(i));
            purchaseEntity.setStandard(purchaseList.getProductSize().get(i));
            purchaseEntity.setAmount(purchaseList.getAmount().get(i));
            purchaseEntity.setUnitPrice(purchaseList.getPrice().get(i));

            if(purchaseList.getNote().size() != 0) {

                if(purchaseList.getNote().get(i) != null) {

                    purchaseEntity.setRemarks(purchaseList.getNote().get(i));
                }
            }

            purchaseRepository.save(purchaseEntity);
        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */

        fileInsertMethod(files, "purchase", documentCode);

//        String root = "C:\\dev\\approvalFile\\";
//        String mainFilePath = root + "purchase";
//
//        File mkdir = new File(mainFilePath);
//        if(!mkdir.exists()) {
//            mkdir.mkdirs();
//        }
//
//        if(files != null) {
//            for (int i = 0; i < files.size(); i++) {
//                String originName = files.get(i).getOriginalFilename();
//                String ext = originName.substring(originName.lastIndexOf("."));
//                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;
//
//                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
//                documentFileEntity.setOriginName(originName);
//                documentFileEntity.setModifyName(modifyName);
//                documentFileEntity.setFilePath(mainFilePath);
//                documentFileEntity.setDocumentCode(documentCode);
//
//                try {
//                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
//                    documentFileRepository.saveAndFlush(documentFileEntity);
//                } catch (IOException e) {
//                    new File(mainFilePath + "\\" + modifyName).delete();
//                    documentFileRepository.delete(documentFileEntity);
//                }
//            }
//        }

        /* 수신자 테이블 insert */

        receiverInsertMethod(purchaseList.getReceive(), documentCode);

//        if(purchaseList.getReceive() != null) {
//            for (int i = 0; i < purchaseList.getReceive().size(); i++) {
//
//                ReceiverEntity receiverEntity = receiveRepository.save(new ReceiverEntity(
//                        purchaseList.getReceive().get(i), documentCode, "대기중"
//
//                ));
//
//                log.info("[ApprovalService] 수신자 테이블 수신자 : {} ", purchaseList.getReceive().get(i));
//            }
//        }

        /* 결재 테이블 insert */

        approvalInsertMethod(purchaseList.getAllow(), documentCode, date);

//        for(int i = 0; i < purchaseList.getAllow().size(); i++) {
//
//            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
//                    documentCode, purchaseList.getAllow().get(i), "결재요청", date, ""
//            ));
//        }
    }

    /* 여비정산 */
    @Transactional
    public void registTraffic(TrafficListDTO trafficList, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */

        int documentCode = documentInsertMethod(trafficList.getEmpNo(), date, "여비정산", trafficList.getDocumentTitle(), "결재요청");

//        DocumentEntity documentEntity = new DocumentEntity();
//        documentEntity.setEmployeeNo(trafficList.getEmpNo());
//        documentEntity.setApplicationDate(date);
//        documentEntity.setDocumentType("여비정산");
//        documentEntity.setStatus("결재요청");
//        documentEntity.setDocumentTitle(trafficList.getDocumentTitle());
//        documentRepository.save(documentEntity);
//
//        int documentCode = documentEntity.getDocumentCode();

        /* 여비정산 테이블 */
        for(int i = 0; i < trafficList.getTrafficDate().size(); i++) {

            TrafficEntity trafficEntity = new TrafficEntity();
            trafficEntity.setTrafficDate(trafficList.getTrafficDate().get(i));
            trafficEntity.setTime(trafficList.getTrafficTime().get(i));
            trafficEntity.setStartPoint(trafficList.getFrom().get(i));
            trafficEntity.setDestination(trafficList.getTo().get(i));
            trafficEntity.setDistance(trafficList.getDistance().get(i));
            trafficEntity.setPrice(trafficList.getTrafficPrice().get(i));
            trafficEntity.setVehicle(trafficList.getVehicle().get(i));
            trafficEntity.setBusiness(trafficList.getBusiness().get(i));
            trafficEntity.setDocumentCode(documentCode);

            trafficRepository.save(trafficEntity);
        }

        /* 결재 테이블 */

        approvalInsertMethod(trafficList.getAllow(), documentCode, date);

//        for(int i = 0; i < trafficList.getAllow().size(); i++) {
//
//            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
//                    documentCode, trafficList.getAllow().get(i), "결재요청", date, ""
//            ));
//        }

        /* 수신자 */

        receiverInsertMethod(trafficList.getReceive(), documentCode);

//        if(trafficList.getReceive() != null) {
//            for (int i = 0; i < trafficList.getReceive().size(); i++) {
//
//                receiveRepository.save(new ReceiverEntity(
//                        trafficList.getReceive().get(i), documentCode, "대기중"
//                ));
//            }
//        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */

        fileInsertMethod(files, "traffic", documentCode);

//        String root = "C:\\dev\\approvalFile\\";
//        String mainFilePath = root + "traffic";
//
//        File mkdir = new File(mainFilePath);
//        if(!mkdir.exists()) {
//            mkdir.mkdirs();
//        }
//
//        if(files != null) {
//            for (int i = 0; i < files.size(); i++) {
//                String originName = files.get(i).getOriginalFilename();
//                String ext = originName.substring(originName.lastIndexOf("."));
//                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;
//
//                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
//                documentFileEntity.setOriginName(originName);
//                documentFileEntity.setModifyName(modifyName);
//                documentFileEntity.setFilePath(mainFilePath);
//                documentFileEntity.setDocumentCode(documentCode);
//
//                try {
//                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
//                    documentFileRepository.saveAndFlush(documentFileEntity);
//                } catch (IOException e) {
//                    new File(mainFilePath + "\\" + modifyName).delete();
//                    documentFileRepository.delete(documentFileEntity);
//                }
//            }
//        }
    }

    /* 업무보고 */
    @Transactional
    public void registBusiness(BusinessDTO business, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */

        int documentCode = documentInsertMethod(business.getEmpNo(), date, "업무보고", business.getDocumentTitle(), "결재요청");

//        DocumentEntity documentEntity = new DocumentEntity();
//        documentEntity.setEmployeeNo(business.getEmpNo());
//        documentEntity.setApplicationDate(date);
//        documentEntity.setDocumentType("업무보고");
//        documentEntity.setDocumentTitle(business.getDocumentTitle());
//        documentEntity.setStatus("결재요청");
//        documentRepository.save(documentEntity);
//
//        int documentCode = documentEntity.getDocumentCode();

        /* 업무보고 테이블 */
        BusinessEntity businessEntity = new BusinessEntity();
        businessEntity.setBusinessContent(business.getBusinessContent());
        businessEntity.setBusinessNote(business.getBusinessNote());
        businessEntity.setDocumentCode(documentCode);

        businessRepository.save(businessEntity);

        /* 결재 테이블 */

        approvalInsertMethod(business.getAllow(), documentCode, date);

//        for(int i = 0; i < business.getAllow().size(); i++) {
//
//            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
//                    documentCode, business.getAllow().get(i), "결재요청", date, ""
//            ));
//        }

        /* 수신자 */

        receiverInsertMethod(business.getReceive(), documentCode);

//        if(business.getReceive() != null) {
//            for (int i = 0; i < business.getReceive().size(); i++) {
//
//                receiveRepository.save(new ReceiverEntity(
//                        business.getReceive().get(i), documentCode, "대기중"
//                ));
//            }
//        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */

        fileInsertMethod(files, "business", documentCode);

//        String root = "C:\\dev\\approvalFile\\";
//        String mainFilePath = root + "business";
//
//        File mkdir = new File(mainFilePath);
//        if(!mkdir.exists()) {
//            mkdir.mkdirs();
//        }
//
//        if(files != null) {
//            for (int i = 0; i < files.size(); i++) {
//                String originName = files.get(i).getOriginalFilename();
//                String ext = originName.substring(originName.lastIndexOf("."));
//                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;
//
//                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
//                documentFileEntity.setOriginName(originName);
//                documentFileEntity.setModifyName(modifyName);
//                documentFileEntity.setFilePath(mainFilePath);
//                documentFileEntity.setDocumentCode(documentCode);
//
//                try {
//                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
//                    documentFileRepository.saveAndFlush(documentFileEntity);
//                } catch (IOException e) {
//                    new File(mainFilePath + "\\" + modifyName).delete();
//                    documentFileRepository.delete(documentFileEntity);
//                }
//            }
//        }
    }

    /* 남은 휴가 가져오기 */
    public int getVacation(int employeeNo) {

        Employee employee = employeeRepository.findByEmployeeNo(employeeNo);

        return employee.getAnnual();
    }

    /* 휴가 신청 */
    @Transactional
    public void registVacation(VacationListDTO vacation, List<MultipartFile> files) throws ParseException {

        Date date = new Date();

        /* 문서테이블에 insert */

        int documentCode = documentInsertMethod(vacation.getEmpNo(), date, "휴가신청", vacation.getDocumentTitle(), "결재요청");

//        DocumentEntity documentEntity = new DocumentEntity();
//        documentEntity.setEmployeeNo(vacation.getEmpNo());
//        documentEntity.setApplicationDate(date);
//        documentEntity.setDocumentType("휴가신청");
//        documentEntity.setStatus("결재요청");
//        documentEntity.setDocumentTitle(vacation.getDocumentTitle());
//        documentRepository.save(documentEntity);
//
//        int documentCode = documentEntity.getDocumentCode();
        log.info("vacation ================ {}", vacation);
        /* 휴가 신청 테이블 */
        VacationEntity vacationEntity = new VacationEntity();

        vacationEntity.setStartDate(new SimpleDateFormat("yyyy-MM-dd").parse(vacation.getStartDate()));
        vacationEntity.setEndDate(new SimpleDateFormat("yyyy-MM-dd").parse(vacation.getEndDate()));
        vacationEntity.setVacationReason(vacation.getVacationReason());
        vacationEntity.setVacationType(vacation.getVacationType());
        vacationEntity.setHalfDayOff(vacation.getHalf());
        vacationEntity.setDocumentCode(documentCode);

        vacationRepository.save(vacationEntity);

        /* 결재 테이블 */

        approvalInsertMethod(vacation.getAllow(), documentCode, date);

//        for(int i = 0; i < vacation.getAllow().size(); i++) {
//
//            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
//                    documentCode, vacation.getAllow().get(i), "결재요청", date, ""
//            ));
//        }

        /* 수신자 */

        receiverInsertMethod(vacation.getReceive(), documentCode);

//        if(vacation.getReceive() != null) {
//            for (int i = 0; i < vacation.getReceive().size(); i++) {
//
//                receiveRepository.save(new ReceiverEntity(
//                        vacation.getReceive().get(i), documentCode, "대기중"
//                ));
//            }
//        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */

        fileInsertMethod(files, "vacation", documentCode);

//        String root = "C:\\dev\\approvalFile\\";
//        String mainFilePath = root + "vacation";
//
//        File mkdir = new File(mainFilePath);
//        if(!mkdir.exists()) {
//            mkdir.mkdirs();
//        }
//
//        if(files != null) {
//            for (int i = 0; i < files.size(); i++) {
//                String originName = files.get(i).getOriginalFilename();
//                String ext = originName.substring(originName.lastIndexOf("."));
//                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;
//
//                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
//                documentFileEntity.setOriginName(originName);
//                documentFileEntity.setModifyName(modifyName);
//                documentFileEntity.setFilePath(mainFilePath);
//                documentFileEntity.setDocumentCode(documentCode);
//
//                try {
//                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
//                    documentFileRepository.saveAndFlush(documentFileEntity);
//                } catch (IOException e) {
//                    new File(mainFilePath + "\\" + modifyName).delete();
//                    documentFileRepository.delete(documentFileEntity);
//                }
//            }
//        }
    }

    /* 비용 청구 */
    @Transactional
    public void registPay(PayListDTO pay, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */

        int documentCode = documentInsertMethod(pay.getEmpNo(), date, "비용청구", pay.getDocumentTitle(), "결재요청");

        /* 비용청구 테이블 */
        for(int i = 0; i < pay.getPayDate().size(); i++) {
            PayEntity payEntity = new PayEntity();

            payEntity.setDay(pay.getPayDate().get(i));
            payEntity.setPrice(pay.getPayPrice().get(i));
            payEntity.setBusiness(pay.getPayReason().get(i));
            payEntity.setDocumentCode(documentCode);

            payRepository.save(payEntity);
        }

        /* 결재 테이블 */

        approvalInsertMethod(pay.getAllow(), documentCode, date);

        /* 수신자 */
        receiverInsertMethod(pay.getReceive(), documentCode);

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */

        fileInsertMethod(files, "pay", documentCode);
    }

    /* 전자결재 홈 */
    public Object findHomeList(int employeeNo) {

        List<ApprovalEntity> app = approverRepository.findByEmployeeNo(employeeNo);
        int toMe = 0;

        for(int p = 0; p < app.size(); p++) {
            String status = app.get(p).getStatus();

            if(status.equals("결재요청")) {
                toMe += 1;
            }
        }

        List<Integer> toMeList = new ArrayList<>();
        toMeList.add(toMe);
        List<DocumentEntity> document = documentRepository.findByEmployeeNoOrderByDocumentCodeDesc(employeeNo);

        int documentCode = 0;
        int count1 = 0;
        int count2 = 0;
        int approvalCode = 0;
        List<PassDataDTO> passData1 = new ArrayList<>();
        List<PassDataDTO> passData2 = new ArrayList<>();
        List<List<Object>> passData3 = new ArrayList<>();

        for(int i = 0; i < document.size(); i++) {

            documentCode = document.get(i).getDocumentCode();

            List<ApprovalEntity> approval = approverRepository.findByDocumentCode(documentCode);

            for(int j = 0; j < approval.size(); j++) {

                if(approval != null && approval.get(j).getStatus().equals("결재요청")) {
                    count1 += 1;
                } else if(approval != null && approval.get(j).getStatus().equals("승인됨")) {
                    count2 += 1;
                }
            }

            List<DocumentFileEntity> documentFile = documentFileRepository.findByDocumentCode(documentCode);

            if(count1 != 0 && passData1.size() < 3) {
                PassDataDTO passData = new PassDataDTO();

                passData.setDocumentCode(documentCode);
                passData.setApplicationDate(document.get(i).getApplicationDate());
                passData.setDocumentType(document.get(i).getDocumentType());
                passData.setFileNum(documentFile.size());
                passData.setDocumentTitle(document.get(i).getDocumentTitle());
                passData.setStatus("결재요청");

                passData1.add(passData);

                count1 = 0;
            }

            if(approval != null && count2 == approval.size() && passData2.size() < 3) {

                PassDataDTO passData = new PassDataDTO();

                passData.setDocumentCode(documentCode);
                passData.setApplicationDate(document.get(i).getApplicationDate());
                passData.setDocumentType(document.get(i).getDocumentType());
                passData.setFileNum(documentFile.size());
                passData.setDocumentTitle(document.get(i).getDocumentTitle());
                passData.setStatus("승인됨");

                passData2.add(passData);

                count2 = 0;
            }
        }

        passData3.add(Collections.singletonList(passData1));
        passData3.add(Collections.singletonList(passData2));
        passData3.add(Collections.singletonList(toMeList));

        return passData3;
    }

    /* 발신 문서함 페이징 처리 */
    public int selectTotal(int employeeNo) {

        List<DocumentEntity> document = documentRepository.findByEmployeeNo(employeeNo);

        return document.size();
    }

    /* 발신 문서함 */
    public List<DocumentDTO> sendWithPaging(Criteria cri, int employeeNo) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("applicationDate").descending());

        Page<DocumentEntity> result = documentRepository.findByEmployeeNo(employeeNo, paging);

        List<DocumentDTO> documentList = result.stream()
                .map(document -> modelMapper
                        .map(document, DocumentDTO.class)).collect(Collectors.toList());

        return documentList;
    }

    /* 업무 문서함 페이징 처리 */
    public int selectMyBusiness(int employeeNo) {

        List<DocumentEntity> document = documentRepository.findByEmployeeNoAndDocumentType(employeeNo, "업무보고");

        return document.size();
    }

    /* 업무 문서함 */
    public List<DocumentDTO> myBusinessWithPaging(Criteria cri, int employeeNo) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("applicationDate").descending());

        Page<DocumentEntity> result = documentRepository.findByEmployeeNoAndDocumentType(employeeNo, paging, "업무보고");

        List<DocumentDTO> documentList = result.stream()
                .map(document -> modelMapper
                        .map(document,DocumentDTO.class)).collect(Collectors.toList());

        return documentList;
    }

    /* 결재 대기 페이징*/
    public int selectAppWait(int employeeNo) {

        List<ApprovalEntity> approval = approverRepository.findByEmployeeNoAndStatus(employeeNo, "결재요청");

        return approval.size();
    }

    /* 결재 대기 페이지 */
    public List<ApprovalAndDocumentDTO> appWaitPaging(Criteria cri, int employeeNo) {
        log.info("================================= employeeNo {}", employeeNo);

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("approvalCode").ascending());

        Page<ApprovalAndDocumentEntity> result = approvalAndDocumentRepository.findByEmployeeNoAndStatus(employeeNo, "결재요청",paging);

        List<ApprovalAndDocumentDTO> approvalList = result.stream()
                .map(approval -> modelMapper
                        .map(approval, ApprovalAndDocumentDTO.class)).collect(Collectors.toList());

        return approvalList;
    }

    /* 참조/열람 대기 페이징*/
    public int selectSeeWait(int employeeNo) {

        List<ReceiverEntity> receive = receiveRepository.findByEmployeeNoAndStatus(employeeNo, "대기중");

        return receive.size();
    }

    /* 참조/열람 대기 페이지 */
    public List<ReceiverAndDocumentDTO> seeWaitPaging(Criteria cri, int employeeNo) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("documentCode").ascending());

        ReceiverDocumentEntityKey key = new ReceiverDocumentEntityKey();
        key.setNo(employeeNo);
        Page<ReceiveDocumentEntity> result = receiverAndDocumentRepository.findByKeyNoAndStatus(employeeNo,"대기중", paging);

        List<ReceiverAndDocumentDTO> receiverList = result.stream()
                .map(receiver -> modelMapper
                        .map(receiver, ReceiverAndDocumentDTO.class)).collect(Collectors.toList());

        return receiverList;
    }

    /* 결재 문서함 페이징 */
    public int selectMyApp(int employeeNo) {

        List<ApprovalEntity> approval = approverRepository.findByEmployeeNo(employeeNo);

        return approval.size();
    }

    /* 결재 문서함 페이지 */
    public List<ApprovalAndDocumentDTO> myAppPaging(Criteria cri, int employeeNo) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("documentCode").ascending());

        Page<ApprovalAndDocumentEntity> result = approvalAndDocumentRepository.findByEmployeeNo(employeeNo, paging);


        List<ApprovalAndDocumentDTO> myAppList = result.stream()
                .map(myApp -> modelMapper
                        .map(myApp, ApprovalAndDocumentDTO.class)).collect(Collectors.toList());

        return myAppList;
    }

    /* 참조/열람 문서함 페이징 */
    public int selectMySee(int employeeNo) {

        List<ReceiverEntity> receive = receiveRepository.findByEmployeeNo(employeeNo);

        return receive.size();
    }

    /* 참조/열람 문서함 */
    public Object mySeePaging(Criteria cri, int employeeNo) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count, Sort.by("documentCode").ascending());

        ReceiverDocumentEntityKey key = new ReceiverDocumentEntityKey();
        key.setNo(employeeNo);
        Page<ReceiveDocumentEntity> result = receiverAndDocumentRepository.findByKeyNo(employeeNo, paging);

        List<ReceiverAndDocumentDTO> receiverList = result.stream()
                .map(receiver -> modelMapper
                        .map(receiver, ReceiverAndDocumentDTO.class)).collect(Collectors.toList());

        return receiverList;
    }

    /* 업무보고 문서 불러오기 */
    public BusinessDocDTO getBusinessDoc(int documentCode) {

        BusinessDocEntity businessDoc = businessDocRepository.findByDocumentCode(documentCode);

        BusinessEntity result = businessRepository.findByDocumentCode(documentCode);

        List<DocumentFileEntity> fileEntity = documentFileRepository.findByDocumentCode(documentCode);

        BusinessDocDTO business = modelMapper.map(businessDoc, BusinessDocDTO.class);

        BusinessDTO resultDTO = modelMapper.map(result, BusinessDTO.class);

        List<DocumentFileDTO> files = fileEntity.stream()
                        .map(file -> modelMapper
                                .map(file, DocumentFileDTO.class)).collect(Collectors.toList());

        business.setBusiness(resultDTO);
        business.setFiles(files);

        return business;
    }

    /* 여비정산 문서 불러오기*/
    public TrafficDocDTO getTrafficDoc(int documentCode) {

        BusinessDocEntity trafficDocEntity = businessDocRepository.findByDocumentCode(documentCode);

        List<TrafficEntity> trafficEntity = trafficRepository.findByDocumentCode(documentCode);

        List<DocumentFileEntity> fileEntity = documentFileRepository.findByDocumentCode(documentCode);

        TrafficDocDTO traffic = modelMapper.map(trafficDocEntity, TrafficDocDTO.class);

        List<TrafficDocListDTO> trafficDTO = trafficEntity.stream().map(trafficData -> modelMapper.map(trafficData, TrafficDocListDTO.class)).collect(Collectors.toList());

        List<DocumentFileDTO> files = fileEntity.stream()
                .map(file -> modelMapper
                        .map(file, DocumentFileDTO.class)).collect(Collectors.toList());

        traffic.setTraffic(trafficDTO);
        traffic.setFiles(files);

        return traffic;
    }

    /* 구매요청 문서 불러오기 */
    public PurchaseDocDTO getPurchaseDoc(int documentCode) {

        BusinessDocEntity purchaseDocEntity = businessDocRepository.findByDocumentCode(documentCode);

        List<PurchaseEntity> purchaseEntity = purchaseRepository.findByDocumentCode(documentCode);

        List<DocumentFileEntity> fileEntity = documentFileRepository.findByDocumentCode(documentCode);

        PurchaseDocDTO purchase = modelMapper.map(purchaseDocEntity, PurchaseDocDTO.class);

        List<PurchaseDTO> purchaseDTO = purchaseEntity.stream()
                .map(pur -> modelMapper
                        .map(pur, PurchaseDTO.class)).collect(Collectors.toList());

        List<DocumentFileDTO> files = fileEntity.stream()
                .map(file -> modelMapper
                        .map(file, DocumentFileDTO.class)).collect(Collectors.toList());

        purchase.setPurchase(purchaseDTO);
        purchase.setFiles(files);

        return purchase;
    }

    /* 휴가신청 문서 불러오기 */
    public VacationDocDTO getVacationDoc(int documentCode) {

        BusinessDocEntity vacationEntity = businessDocRepository.findByDocumentCode(documentCode);

        VacationEntity vacation = vacationRepository.findByDocumentCode(documentCode);

        List<DocumentFileEntity> fileEntity = documentFileRepository.findByDocumentCode(documentCode);

        VacationDocDTO vacationDoc = modelMapper.map(vacationEntity, VacationDocDTO.class);

        VacationListDTO vacationDTO = modelMapper.map(vacation, VacationListDTO.class);

        List<DocumentFileDTO> files = fileEntity.stream()
                .map(file -> modelMapper
                        .map(file, DocumentFileDTO.class)).collect(Collectors.toList());

        vacationDoc.setVacation(vacationDTO);
        vacationDoc.setFiles(files);

        return vacationDoc;
    }

    /* 비용청구 문서 불러오기 */
    public PayDocDTO getPayDoc(int documentCode) {

        BusinessDocEntity payDocEntity = businessDocRepository.findByDocumentCode(documentCode);

        List<PayEntity> pay = payRepository.findByDocumentCode(documentCode);

        List<DocumentFileEntity> fileEntity = documentFileRepository.findByDocumentCode(documentCode);

        PayDocDTO payDoc = modelMapper.map(payDocEntity, PayDocDTO.class);

        List<PayDTO> payDTO = pay.stream().map(payData -> modelMapper.map(payData, PayDTO.class)).collect(Collectors.toList());

        List<DocumentFileDTO> files = fileEntity.stream()
                .map(file -> modelMapper
                        .map(file, DocumentFileDTO.class)).collect(Collectors.toList());

        payDoc.setPay(payDTO);
        payDoc.setFiles(files);

        return payDoc;
    }

    /* 수정 가능한지 안한지 */
    public boolean getAppYN(int documentCode) {

        boolean yn = false;
        int count = 0;

        List<ApprovalEntity> appList = approverRepository.findByDocumentCode(documentCode);
        List<ReceiverEntity> receiverList = receiveRepository.findByDocumentCode(documentCode);

        for(int i = 0; i < appList.size(); i++) {

            if(appList.get(i).getStatus().equals("승인됨")) {

                count += 1;
            }
        }

        for(int i = 0; i < receiverList.size(); i++) {

            if(receiverList.get(i).getStatus().equals("확인")) {

                count += 1;
            }
        }

        log.info("count  ====================================== {} ", count);

        if(count > 0) {
            yn = false;
        } else {
            yn = true;
        }

        return yn;
    }

    /* 문서 삭제 */
    @Transactional
    public void deleteDocument(int documentCode) {

        purchaseRepository.deleteByDocumentCode(documentCode);
        trafficRepository.deleteByDocumentCode(documentCode);
        businessRepository.deleteByDocumentCode(documentCode);
        vacationRepository.deleteByDocumentCode(documentCode);
        payRepository.deleteByDocumentCode(documentCode);
        approverRepository.deleteByDocumentCode(documentCode);
        receiveRepository.deleteByDocumentCode(documentCode);
        documentFileRepository.deleteByDocumentCode(documentCode);
        documentRepository.deleteByDocumentCode(documentCode);
    }

    /* 승인 로직 */
    @Transactional
    public void putApproval(int documentCode, int employeeNo) {

        List<ApprovalEntity> appList = approverRepository.findByDocumentCode(documentCode);

        int count = 0;

        ApprovalEntity approval = approverRepository.findByEmployeeNoAndDocumentCode(employeeNo,documentCode);
        approval.setStatus("승인됨");

        for(int i = 0; i < appList.size(); i++) {
            if(appList.get(i).getStatus().equals("승인됨")) {
                count += 1;
            }
        }

        if(count == appList.size()) {
            DocumentEntity document = documentRepository.findByDocumentCode(documentCode);
            document.setStatus("승인됨");
        }

    }

    /* 코멘트 insert */
    @Transactional
    public void backDocument(String comment, int documentCode, int employeeNo) {

        ApprovalEntity approval = approverRepository.findByEmployeeNoAndDocumentCode(employeeNo, documentCode);
        approval.setCMT(comment);

        List<ApprovalEntity> approvalList = approverRepository.findByDocumentCode(documentCode);

        for(int i = 0; i < approvalList.size(); i++) {

            approvalList.get(i).setStatus("반려됨");
        }

        DocumentEntity document = documentRepository.findByDocumentCode(documentCode);
        document.setStatus("반려됨");
    }

    /* 휴가 줄이기 */
    @Transactional
    public void putVacation(int documentCode, int useDate) {

        DocumentEntity document = documentRepository.findByDocumentCode(documentCode);
        int employeeNo = document.getEmployeeNo();
        Employee employee = employeeRepository.findByEmployeeNo(employeeNo);

        employee.setAnnual(employee.getAnnual() - useDate);
    }

    public byte[] downloadFile(int fileCode) throws IOException {

        DocumentFileEntity file = documentFileRepository.findByDocumentFileCode(fileCode);
        String path = file.getFilePath() + "\\" + file.getModifyName();

        return Files.readAllBytes(new File(path).toPath());
    }

    public void putReceiver(int employeeNo, int documentCode) {

        ReceiverEntity receiver = receiveRepository.findByEmployeeNoAndDocumentCode(employeeNo, documentCode);

        receiver.setStatus("확인");
    }

    /* TBL_DOCUMENT insert */
    public int documentInsertMethod(int employeeNo, Date applicationDate, String documentType, String documentTitle, String status) {

        DocumentEntity document = new DocumentEntity();
        document.setEmployeeNo(employeeNo);
        document.setApplicationDate(applicationDate);
        document.setDocumentType(documentType);
        document.setDocumentTitle(documentTitle);
        document.setStatus(status);
        documentRepository.save(document);

        return document.getDocumentCode();
    }

    /* TBL_RECEIVER insert */
    public void receiverInsertMethod(List<Integer> list, int documentCode) {

        if(list != null) {

            for(int i = 0; i < list.size(); i++) {

                receiveRepository.save(new ReceiverEntity(
                        list.get(i), documentCode, "대기중"
                ));
            }
        }
    }

    /* TBL_APPROVAL insert */
    public void approvalInsertMethod(List<Integer> list, int documentCode, Date date) {

        for (int i = 0; i < list.size(); i++) {

            ApprovalEntity approval = approverRepository.save(new ApprovalEntity(

                    documentCode, list.get(i), "결재요청", date, ""));
        }
    }

    /* TBL_DOCUMENT_FILE insert */
    public void fileInsertMethod(List<MultipartFile> files, String path, int documentCode) {

        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + path;

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity file = new DocumentFileEntity();
                file.setOriginName(originName);
                file.setModifyName(modifyName);
                file.setFilePath(mainFilePath);
                file.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(file);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(file);
                }
            }
        }
    }
}
