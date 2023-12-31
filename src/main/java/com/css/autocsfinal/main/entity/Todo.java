package com.css.autocsfinal.main.entity;

import com.css.autocsfinal.member.entity.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.sql.Date;

@Entity
@Table(name = "TBL_TODOLIST")
@SequenceGenerator(
        name = "SEQ_TODOLIST_GENERATOR", // 엔티티에서 지정한 시퀀스 이름
        sequenceName = "SEQ_TODOLIST_NO", // 실제 데이터베이스에 있는 시퀀스 명
        initialValue = 1, allocationSize = 1
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Builder
@Data
public class Todo {

    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "SEQ_TODOLIST_GENERATOR"
    )
    @Column(name = "TODO_NO")
    private Integer todoNo;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "STATUS")
    private char todoStatus = 'N';   // todo리스트 기본 상태값 false

    @Column(name = "REG_DATE")
    private Date regDate;

    @Column(name = "REGIST_DATE")
    private LocalDate upDate;

    // 연관관계 연결 fetch = Lazy는 select쿼리를 호출할 시 연관된 객체는 값이 직접적으로 접근될때 select쿼리가 다시 호출됨.
    @ManyToOne  //fetch = FetchType.LAZY
    @JoinColumn(name= "REF_MEMBER_NO")   // 연관관계 매핑 시 외래키 지정
    private Member member;






}
