package com.css.autocsfinal.chart.controller;

import com.css.autocsfinal.chart.dto.DeptResult;
import com.css.autocsfinal.chart.service.ChartService;
import com.css.autocsfinal.common.ResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/chart")
@RequiredArgsConstructor
@Tag(name = "Chart", description = "조직도 API")
public class ChartController {

    private final ChartService chartService;

    @GetMapping()
    @Operation(summary = "조직도 API", description = "조직도를 계층형태로 불러옵니다.", tags = {"ChartController"})
    public ResponseEntity<ResponseDTO> findDept() {

        DeptResult deptResult = chartService.findDept();

        HttpStatus httpStatus = HttpStatus.OK;

        ResponseDTO responseDTO = new ResponseDTO(httpStatus, "조회 성공", deptResult);

        return ResponseEntity.ok().body(responseDTO);
    }


}
