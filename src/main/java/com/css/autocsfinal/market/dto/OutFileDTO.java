package com.css.autocsfinal.market.dto;

import lombok.*;

import java.sql.Date;
import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class OutFileDTO {

    private int outFileNo;

    private String original;

    private String change;

    private Date registDate;

    private String kind;

    private char state;

    private int storeNo;
    //
    private StoreInfoDTO store;
}
