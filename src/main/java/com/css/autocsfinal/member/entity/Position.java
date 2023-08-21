package com.css.autocsfinal.member.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "TBL_POSITION")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Position {

    @Id
    @Column(name = "POSITION_CODE")
    private int code;

    @Column(name = "NAME")
    private String name;
}