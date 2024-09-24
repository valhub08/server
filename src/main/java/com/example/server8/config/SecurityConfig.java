package com.example.server8.config;

import com.example.server8.repository.MemberRepository;
import com.example.server8.filter.JwtRequestFilter;
import com.example.server8.util.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final MemberRepository memberRepository;
    private final JwtRequestFilter jwtRequestFilter;  // JWT 필터 주입
    private final JwtTokenUtil jwtTokenUtil;  // JWT 유틸 주입

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            return memberRepository.findByMemberEmail(username)
                    .map(member -> org.springframework.security.core.userdetails.User
                            .withUsername(member.getMemberEmail())
                            .password(member.getMemberPassword())
                            .roles("USER")
                            .build())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        };
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 비활성화, 회원가입 경로는 CSRF 무시
                .csrf(csrf -> csrf.ignoringRequestMatchers("/member/save"))

                // 세션을 사용하지 않고 JWT로 인증 관리
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 엔드포인트별 접근 설정
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/member/save", "/member/index", "/login", "/loginProc").permitAll()
                        .anyRequest().authenticated()
                )

                // 로그인 설정
                .formLogin((auth) -> auth
                        .loginPage("/login")  // 사용자 정의 로그인 페이지
                        .loginProcessingUrl("/loginProc")  // 로그인 폼 처리 URL
                        .permitAll()
                )

                // 로그아웃 설정
                .logout((auth) -> auth
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                );

        // JWT 필터를 UsernamePasswordAuthenticationFilter 전에 추가
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}