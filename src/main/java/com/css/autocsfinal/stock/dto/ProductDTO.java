package com.css.autocsfinal.stock.dto;

import lombok.*;

import java.sql.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ProductDTO {

    private int productNo;
    private String name;
    private int stock;
    private int price;
    private String etc;
    private Date registDate;
    private Date unusedDate;
    private String status;
    private int refProductCategoryNo;
    private int refProductStandardNo;
    private int refProductUnitNo;

}
