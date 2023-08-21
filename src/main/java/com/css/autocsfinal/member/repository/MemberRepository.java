package com.css.autocsfinal.member.repository;

import com.css.autocsfinal.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Member findByMemberId(String Id);  // 회원아이디로 조회

    //Member findByMemberEmail(String memberEmail);

    @Query("SELECT MAX(m.no) FROM Member m")
    Integer maxMemberCode();

//    /* purchase 도메인 추가하면서 추가한 메소드 */
//    @Query("SELECT a.memberCode FROM Member a WHERE a.memberId = ?1")
//    int findMemberCodeByMemberId(String orderMemberId);
}