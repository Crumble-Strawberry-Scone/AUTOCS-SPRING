package com.css.autocsfinal.mail.service;

import com.css.autocsfinal.common.Criteria;
import com.css.autocsfinal.mail.dto.MailDTO;
//import com.css.autocsfinal.mail.entity.MailList;
//import com.css.autocsfinal.mail.repository.MailListRepository;
import com.css.autocsfinal.mail.dto.MailSaveDTO;
import com.css.autocsfinal.mail.entity.Mail;
import com.css.autocsfinal.mail.entity.MailList;
import com.css.autocsfinal.mail.repository.MailListRepository;
import com.css.autocsfinal.mail.repository.MailRepository;
import com.css.autocsfinal.member.dto.EmployeeDTO;
import com.css.autocsfinal.member.entity.Employee;
import com.css.autocsfinal.member.entity.EmployeeAndDepartmentAndPosition;
import com.css.autocsfinal.member.repository.EmployeeAndDepartmentAndPositionRepository;
import com.css.autocsfinal.member.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailService {

    private final MailRepository mailRepository;
    private final ModelMapper modelMapper;
    private final MailListRepository mailListRepository;
    private final EmployeeAndDepartmentAndPositionRepository employeeAndDepartmentAndPositionRepository;

    // 메일 조회
    public List<MailDTO> findMail(int employeeNo, Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        log.info(positionName + name);

        List<Mail> mailList = mailRepository.findByPositionAndReceiverOrderByMailNoDesc(positionName, name, paging);

        List<MailDTO> mailDTOList = mailList.stream().map(Mail -> modelMapper.map(Mail, MailDTO.class) ).collect(Collectors.toList());


        return mailDTOList;
    }

    // 메일조회(검색)
    public List<MailDTO> findMail(int employeeNo, Criteria cri, String title) {

        title = '%'+ title + '%';

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        List<Mail> mailList = mailRepository.findByPositionAndReceiverAndTitleLikeOrderByMailNoDesc(positionName, name, paging, title);

        List<MailDTO> mailDTOList = mailList.stream().map(Mail -> modelMapper.map(Mail, MailDTO.class) ).collect(Collectors.toList());

        return mailDTOList;
    }

    public int findByMailTotal(int employeeNo) {

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        List<Mail> mailList = mailRepository.findByStatus(positionName, name, "Y");

        log.info("test Number =================================>{}", mailList.size());
        return mailList.size();
    }

    // 북마크 조회
    public Object mailBookmark(int employeeNo,Criteria cri) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);


        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();


        List<Mail> mailList = mailRepository.findByStatus(positionName, name, "Y", paging);

        List<MailDTO> mailDTOList = mailList.stream().map(mail -> modelMapper.map(mail, MailDTO.class)).collect(Collectors.toList());

        log.info("========================================================>{}", mailDTOList);

        return mailDTOList;

    }


    // 북마크 검색 갯수
    public int findByBookmarkMailTotal(int employeeNo, String title) {

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        title = '%' + title +'%';

        List<Mail> mailList = mailRepository.findByStatus(positionName, name, "Y", title);

        return mailList.size();
    }

    // 북마크 검색 결과
    public Object mailBookmark(int employeeNo, Criteria cri, String title) {

        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        title = '%' + title +'%';

        List<Mail> mailList = mailRepository.findByStatus(positionName, name, "Y", title, paging);

        List<MailDTO> mailDTOList = mailList.stream().map(mail -> modelMapper.map(mail, MailDTO.class)).collect(Collectors.toList());

        return mailDTOList;


    }
    @Transactional
    public Object saveMail(MailDTO mailDTO, int employeeNo) {

        mailDTO.setGoDate(new Date());
        mailDTO.setStatus("N");
        mailDTO.setContext(mailDTO.getContext().replace("<p>", ""));
        mailDTO.setContext(mailDTO.getContext().replace("</p>", ""));


        for(int i=0; i < mailDTO.getReceiver().size(); i++){

            MailSaveDTO mailSaveDTO = new MailSaveDTO();

            mailSaveDTO = modelMapper.map(mailDTO, MailSaveDTO.class);

            Mail mail = modelMapper.map(mailSaveDTO, Mail.class);

            mail.setReceiver(mailDTO.getReceiver().get(i));

            Mail mail2 =  mailRepository.save(mail);

            int mailNo = mail2.getMailNo();

            MailList mailList = new MailList(employeeNo, mailNo);

            mailListRepository.save(mailList);
        }

        return null;
    }
    
    // 메일 전체 삭제
    @Transactional
    public Object deleteMail(int employeeNo) {

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        mailListRepository.deleteByEmployeeNo(employeeNo);

        mailRepository.deleteByPositionAndReceiver(positionName, name);

        return null;
    }

    @Transactional
    public Object setMail(MailDTO mailDTO) {

        int mailNo = mailDTO.getMailNo();

        Mail mail = mailRepository.findById(mailNo).get();

        if(mail.getStatus().equals("N")){
            mail.setStatus("Y");
        } else {
            mail.setStatus("N");
        }

        MailDTO resultMail = modelMapper.map(mail, MailDTO.class);

        return resultMail;

    }

    @Transactional
    public Object deleteSelectMail(MailDTO mailDTO) {

        int mailNo = mailDTO.getMailNo();

        log.info("=======================>{}", mailNo);

        mailListRepository.deleteByMailNo(mailNo);
        mailRepository.deleteByMailNo(mailNo);

        return mailNo;
    }

    public Object mailSent(int employeeNo, Criteria cri) {



        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);

        List<MailList> mailLists = mailListRepository.findByPage(employeeNo, paging);

        List<Mail> mailList = new ArrayList<>();


        for(int i = 0; i< mailLists.size(); i++){
            mailList.add(mailLists.get(i).getMail());
        }

        List<MailDTO> mailDTOList = mailList.stream().map(mail -> modelMapper.map(mail, MailDTO.class)).collect(Collectors.toList());

        log.info("마지막테스트입니다.{}", mailDTOList);

        return mailDTOList;
    }

    // 검색
    public Object mailSent(int employeeNo, Criteria cri, String title) {


        int index = cri.getPageNum() - 1;
        int count = cri.getAmount();
        Pageable paging = PageRequest.of(index, count);

        List<MailList> mailLists = mailListRepository.findByPage(employeeNo, paging, title);

        List<Mail> mailList = new ArrayList<>();


        for(int i = 0; i< mailLists.size(); i++){
            mailList.add(mailLists.get(i).getMail());
        }

        List<MailDTO> mailDTOList = mailList.stream().map(mail -> modelMapper.map(mail, MailDTO.class)).collect(Collectors.toList());


        return mailDTOList;
    }


    public int findByMailSentTotal(int employeeNo) {

        List<MailList> mailLists = mailListRepository.findByEmployeeNo  (employeeNo);

        return mailLists.size();
    }


    public int findByMailAllTotal(int employeeNo) {

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        List<Mail> mailList = mailRepository.findByPositionAndReceiverOrderByMailNoDesc(positionName, name);

        return mailList.size();
    }


    public int findByMailSelectTotal(int employeeNo, String title) {


        title = '%'+ title + '%';

        EmployeeAndDepartmentAndPosition employeeAndDepartmentAndPosition = employeeAndDepartmentAndPositionRepository.findById(employeeNo).get();

        String positionName = employeeAndDepartmentAndPosition.getPosition().getName();

        String name = employeeAndDepartmentAndPosition.getName();

        List<Mail> mailList = mailRepository.findByPositionAndReceiverAndTitleLikeOrderByMailNoDesc(positionName, name, title);

        return mailList.size();
    }


    public int findBySelectMailSentTotal(int employeeNo, String title) {


        List<MailList> mailLists = mailListRepository.findByPage(employeeNo, title);

        List<Mail> mailList = new ArrayList<>();


        for(int i = 0; i< mailLists.size(); i++){
            mailList.add(mailLists.get(i).getMail());
        }
        log.info("test==============================================>{}", mailList.size());
        return mailList.size();
    }

}