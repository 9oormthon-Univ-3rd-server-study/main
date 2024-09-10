package start.goorm.study.common.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import start.goorm.study.dto.CustomOAuth2User;
import start.goorm.study.dto.UserDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTProvider jwtProvider;
    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //cookie들을 불러온 뒤 Authorization Key에 담긴 쿠키를 찾음
        String token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            log.debug(cookie.getName());
            if (cookie.getName().equals("Authorization")) {
                token = cookie.getValue();
            }
        }
        if (token == null) {
            log.debug("token null");
            filterChain.doFilter(request, response);
            return;
        }
        if (jwtProvider.isExpired(token)) {
            log.debug("token expired");
            filterChain.doFilter(request, response);
            return;
        }
        String username = jwtProvider.getUsername(token);
        String role = jwtProvider.getRole(token);

        UserDto userDto = UserDto.builder()
                .username(username)
                .role(role)
                .build();

        //UserDetails에 회원 정보 객체 담기
        CustomOAuth2User customOAuth2User = new CustomOAuth2User(userDto);
        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customOAuth2User, null, customOAuth2User.getAuthorities());
        //세션에 사용자 등록
        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
