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
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import start.goorm.study.common.config.LoginUser;
import start.goorm.study.domain.User;
import start.goorm.study.dto.CustomOAuth2User;
import start.goorm.study.dto.UserDto;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = findToken(request);
        if (verifyToken(request, response, filterChain, token)) return;
        User user = getUser(token);
        setSecuritySession(user);
        filterChain.doFilter(request, response);
    }

    private static void setSecuritySession(User user) {
        LoginUser loginUser = new LoginUser(user);
        //스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);
    }

    private User getUser(String token) {
        String username = jwtProvider.getUsername(token);
        String name = jwtProvider.getName(token);
        String role = jwtProvider.getRole(token);

        User user = User.builder()
                .username(username)
                .name(name)
                .role(role)
                .build();
        return user;
    }

    private boolean verifyToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain, String token) throws IOException, ServletException {
        if (token == null) {
            log.debug("token null");
            filterChain.doFilter(request, response);
            return true;
        }
        if (jwtProvider.isExpired(token)) {
            log.debug("token expired");
            filterChain.doFilter(request, response);
            return true;
        }
        return false;
    }

    private static String findToken(HttpServletRequest request) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Authorization")) {
                token = cookie.getValue();
            }
        }
        return token;
    }
}
