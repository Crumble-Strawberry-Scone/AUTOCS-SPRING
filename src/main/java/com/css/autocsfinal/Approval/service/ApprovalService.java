package com.css.autocsfinal.Approval.service;

import com.css.autocsfinal.Approval.dto.*;
import com.css.autocsfinal.Approval.entity.*;
import com.css.autocsfinal.Approval.repository.*;
import com.css.autocsfinal.member.entity.Employee;
import com.css.autocsfinal.member.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ApprovalService {

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
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setEmployeeNo(purchaseList.getEmpNo());
        documentEntity.setApplicationDate(date);
        documentEntity.setDocumentType("구매요청");
        documentEntity.setDocumentTitle(purchaseList.getDocumentTitle());
        documentRepository.save(documentEntity);

        int documentCode = documentEntity.getDocumentCode();;

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

        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + "purchase";

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
                documentFileEntity.setOriginName(originName);
                documentFileEntity.setModifyName(modifyName);
                documentFileEntity.setFilePath(mainFilePath);
                documentFileEntity.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(documentFileEntity);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(documentFileEntity);
                }
            }
        }

        /* 수신자 테이블 insert */
        if(purchaseList.getReceive() != null) {
            for (int i = 0; i < purchaseList.getReceive().size(); i++) {

                ReceiverEntity receiverEntity = receiveRepository.save(new ReceiverEntity(
                        purchaseList.getReceive().get(i), documentCode, "대기중"

                ));

                log.info("[ApprovalService] 수신자 테이블 수신자 : {} ", purchaseList.getReceive().get(i));
            }
        }

        /* 결재 테이블 insert */
        for(int i = 0; i < purchaseList.getAllow().size(); i++) {

            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
                    documentCode, purchaseList.getAllow().get(i), "결재요청", date, ""
            ));
        }
    }

    /* 여비정산 */
    @Transactional
    public void registTraffic(TrafficListDTO trafficList, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setEmployeeNo(trafficList.getEmpNo());
        documentEntity.setApplicationDate(date);
        documentEntity.setDocumentType("구매요청");
        documentEntity.setDocumentTitle(trafficList.getDocumentTitle());
        documentRepository.save(documentEntity);

        int documentCode = documentEntity.getDocumentCode();

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
        for(int i = 0; i < trafficList.getAllow().size(); i++) {

            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
                    documentCode, trafficList.getAllow().get(i), "결재요청", date, ""
            ));
        }

        /* 수신자 */
        if(trafficList.getReceive() != null) {
            for (int i = 0; i < trafficList.getReceive().size(); i++) {

                receiveRepository.save(new ReceiverEntity(
                        trafficList.getReceive().get(i), documentCode, "대기중"
                ));
            }
        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */
        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + "traffic";

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
                documentFileEntity.setOriginName(originName);
                documentFileEntity.setModifyName(modifyName);
                documentFileEntity.setFilePath(mainFilePath);
                documentFileEntity.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(documentFileEntity);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(documentFileEntity);
                }
            }
        }
    }

    /* 업무보고 */
    @Transactional
    public void registBusiness(BusinessDTO business, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setEmployeeNo(business.getEmpNo());
        documentEntity.setApplicationDate(date);
        documentEntity.setDocumentType("구매요청");
        documentEntity.setDocumentTitle(business.getDocumentTitle());
        documentRepository.save(documentEntity);

        int documentCode = documentEntity.getDocumentCode();

        /* 업무보고 테이블 */
        BusinessEntity businessEntity = new BusinessEntity();
        businessEntity.setBusinessContent(business.getBusiness());
        businessEntity.setRemarks(business.getBusinessNote());
        businessEntity.setDocumentCode(documentCode);

        businessRepository.save(businessEntity);

        /* 결재 테이블 */
        for(int i = 0; i < business.getAllow().size(); i++) {

            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
                    documentCode, business.getAllow().get(i), "결재요청", date, ""
            ));
        }

        /* 수신자 */
        if(business.getReceive() != null) {
            for (int i = 0; i < business.getReceive().size(); i++) {

                receiveRepository.save(new ReceiverEntity(
                        business.getReceive().get(i), documentCode, "대기중"
                ));
            }
        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */
        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + "business";

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
                documentFileEntity.setOriginName(originName);
                documentFileEntity.setModifyName(modifyName);
                documentFileEntity.setFilePath(mainFilePath);
                documentFileEntity.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(documentFileEntity);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(documentFileEntity);
                }
            }
        }
    }

    /* 남은 휴가 가져오기 */
    public int getVacation(int employeeNo) {

        Employee employee = employeeRepository.findByEmployeeNo(employeeNo);

        return employee.getAnnual();
    }

    /* 휴가 신청 */
    @Transactional
    public void registVacation(VacationListDTO vacation, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setEmployeeNo(vacation.getEmpNo());
        documentEntity.setApplicationDate(date);
        documentEntity.setDocumentType("구매요청");
        documentEntity.setDocumentTitle(vacation.getDocumentTitle());
        documentRepository.save(documentEntity);

        int documentCode = documentEntity.getDocumentCode();

        /* 휴가 신청 테이블 */
        VacationEntity vacationEntity = new VacationEntity();

        Date startDate = new Date(vacation.getStartDate());
        Date endDate = new Date(vacation.getEndDate());

        vacationEntity.setStartDate(startDate);
        vacationEntity.setEndDate(endDate);
        vacationEntity.setVacationReason(vacation.getVacationReason());
        vacationEntity.setVacationType(vacation.getVacationType());
        vacationEntity.setHalfDayOff(vacation.getHalf());
        vacationEntity.setDocumentCode(documentCode);

        vacationRepository.save(vacationEntity);

        /* 결재 테이블 */
        for(int i = 0; i < vacation.getAllow().size(); i++) {

            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
                    documentCode, vacation.getAllow().get(i), "결재요청", date, ""
            ));
        }

        /* 수신자 */
        if(vacation.getReceive() != null) {
            for (int i = 0; i < vacation.getReceive().size(); i++) {

                receiveRepository.save(new ReceiverEntity(
                        vacation.getReceive().get(i), documentCode, "대기중"
                ));
            }
        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */
        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + "business";

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
                documentFileEntity.setOriginName(originName);
                documentFileEntity.setModifyName(modifyName);
                documentFileEntity.setFilePath(mainFilePath);
                documentFileEntity.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(documentFileEntity);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(documentFileEntity);
                }
            }
        }
    }

    /* 비용 청구 */
    @Transactional
    public void registPay(PayListDTO pay, List<MultipartFile> files) {

        Date date = new Date();

        /* 문서테이블에 insert */
        DocumentEntity documentEntity = new DocumentEntity();
        documentEntity.setEmployeeNo(pay.getEmpNo());
        documentEntity.setApplicationDate(date);
        documentEntity.setDocumentType("구매요청");
        documentEntity.setDocumentTitle(pay.getDocumentTitle());
        documentRepository.save(documentEntity);

        int documentCode = documentEntity.getDocumentCode();

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
        for(int i = 0; i < pay.getAllow().size(); i++) {

            ApprovalEntity approvalEntity = approverRepository.save(new ApprovalEntity(
                    documentCode, pay.getAllow().get(i), "결재요청", date, ""
            ));
        }

        if(pay.getReceive() != null) {
            /* 수신자 */
            for (int i = 0; i < pay.getReceive().size(); i++) {

                receiveRepository.save(new ReceiverEntity(
                        pay.getReceive().get(i), documentCode, "대기중"
                ));
            }
        }

        /* 파일 저장 경로 만들어서 파일 테이블에 insert */
        String root = "C:\\dev\\approvalFile\\";
        String mainFilePath = root + "business";

        File mkdir = new File(mainFilePath);
        if(!mkdir.exists()) {
            mkdir.mkdirs();
        }

        if(files != null) {
            for (int i = 0; i < files.size(); i++) {
                String originName = files.get(i).getOriginalFilename();
                String ext = originName.substring(originName.lastIndexOf("."));
                String modifyName = UUID.randomUUID().toString().replace("-", "") + ext;

                DocumentFileEntity documentFileEntity = new DocumentFileEntity();
                documentFileEntity.setOriginName(originName);
                documentFileEntity.setModifyName(modifyName);
                documentFileEntity.setFilePath(mainFilePath);
                documentFileEntity.setDocumentCode(documentCode);

                try {
                    files.get(i).transferTo(new File(mainFilePath + "\\" + modifyName));
                    documentFileRepository.saveAndFlush(documentFileEntity);
                } catch (IOException e) {
                    new File(mainFilePath + "\\" + modifyName).delete();
                    documentFileRepository.delete(documentFileEntity);
                }
            }
        }
    }

    /* 전자결재 홈 */
    public Object findHomeList(int employeeNo) {

        List<ApprovalEntity> app = approverRepository.findByEmployeeNo(employeeNo);
        int toMe = 0;

        for(int p = 0; p < app.size(); p++) {
            String status = app.get(p).getStatus();

            if(status.equals("승인요청")) {
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

                if(approval != null && approval.get(i).getStatus().equals("결재요청")) {
                    count1 += 1;
                } else if(approval != null && approval.get(i).getStatus().equals("승인됨")) {
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

//    /* 발신 문서함 */
//    public Object findSend(int employeeNo) {
//
//    }
}