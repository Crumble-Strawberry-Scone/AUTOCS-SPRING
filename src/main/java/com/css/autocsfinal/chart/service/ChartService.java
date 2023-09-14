package com.css.autocsfinal.chart.service;

import com.css.autocsfinal.chart.dto.DeptResult;
import com.css.autocsfinal.chart.entity.DepartmentEntity;
import com.css.autocsfinal.chart.repository.DepartmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChartService {

    private final DepartmentRepository departmentRepository;

    public DeptResult findDept() {

        log.info("[ChartService] DeptResult findDept 실행");
        List<DepartmentEntity> deptList = departmentRepository.findAll();
        log.info("deptList : ", deptList);
        log.info("[ChartService] DeptResult findDept findAll 완료");
        DeptResult result = deptList.stream().map(DeptResult::new).collect(Collectors.toList()).get(1);

        return result;
    }

}
