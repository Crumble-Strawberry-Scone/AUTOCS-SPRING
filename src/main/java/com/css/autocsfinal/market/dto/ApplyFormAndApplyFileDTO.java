package com.css.autocsfinal.market.dto;

import lombok.*;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ApplyFormAndApplyFileDTO {

    private int applyNo;

    private String address;

    private String name;

    private String email;

    private String license;

    private String state;

    private int refAppleNo;

    private String original;

    private String change;

    private Date registDate;

    private String kind;


}
