package com.example.server8.controller;

import com.example.server8.dto.MemberDTO;
import com.example.server8.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final Logger logger = LoggerFactory.getLogger(MemberController.class);

    private final MemberService memberService;
    private final AuthenticationManager authenticationManager;
    private final com.example.server8.util.JwtTokenUtil jwtTokenUtil;
    private final UserDetailsService userDetailsService;

    // 회원가입 화면 반환
    @GetMapping("/member/save")
    public String saveForm() {
        return "save";
    }

    // 회원가입 요청 처리
    @PostMapping("/member/save")
    public String save(@ModelAttribute MemberDTO memberDTO) {
        logger.info("회원가입 요청: {}", memberDTO);
        memberService.save(memberDTO);
        return "redirect:/member/index";  // 성공적으로 가입된 후 이동할 페이지
    }

    // 회원가입 성공 후 보여줄 화면
    @GetMapping("/member/index")
    public String index() {
        return "index";  // index.html로 이동
    }

    // JWT 로그인 요청 처리
    @PostMapping("/member/login")
    public String login(@RequestBody MemberDTO memberDTO) {
        // 사용자 인증 처리
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(memberDTO.getMemberEmail(), memberDTO.getMemberPassword())
        );

        // 사용자 정보 로드
        UserDetails userDetails = userDetailsService.loadUserByUsername(memberDTO.getMemberEmail());

        // JWT 토큰 생성
        String jwtToken = jwtTokenUtil.generateAccessToken(userDetails.getUsername());

        // 클라이언트에 JWT 토큰 반환 (로그인 성공 시)
        return jwtToken;
    }
    @GetMapping("/responseEntity")
    public ResponseEntity<String> responseEntity() {
        return ResponseEntity.ok("ok");
    }

}