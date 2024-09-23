package com.example.server8.service;

import com.example.server8.dto.MemberDTO;
import com.example.server8.entity.MemberEntity;
import com.example.server8.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder; // 암호화 처리할 PasswordEncoder 주입

    // 회원가입 로직
    public void save(MemberDTO memberDTO) {
        // 비밀번호를 암호화 처리
        String encryptedPassword = passwordEncoder.encode(memberDTO.getMemberPassword());

        // DTO -> Entity 변환 (암호화된 비밀번호 사용)
        MemberEntity memberEntity = MemberEntity.toMemberEntity(memberDTO, encryptedPassword);

        memberRepository.save(memberEntity);
    }

}