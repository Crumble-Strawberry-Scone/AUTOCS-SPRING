package com.css.autocsfinal.main.repository;

import com.css.autocsfinal.chart.entity.DepartmentEntity;
import com.css.autocsfinal.main.entity.Todo;
import com.css.autocsfinal.member.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;



public interface TodoRepository extends JpaRepository<Todo,Integer> {

    // 투두리스트 전체 조회
    List<Todo> findAll();

    // 멤버번호로 리스트조회
    List<Todo> findByMemberNo(int memberNo);


}
