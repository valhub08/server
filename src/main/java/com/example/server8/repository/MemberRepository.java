package com.example.server8.repository;

import com.example.server8.member.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsByLoginId(String loginId);

    Member findByLoginId(String loginId);
}
