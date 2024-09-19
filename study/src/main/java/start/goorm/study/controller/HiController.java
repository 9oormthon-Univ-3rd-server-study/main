package start.goorm.study.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import start.goorm.study.common.config.LoginUser;

@RestController
public class HiController {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @GetMapping("/hi")
    public void hi(@AuthenticationPrincipal LoginUser loginUser) {
        log.info("loginUsername={}", loginUser.getUsername());
    }
}
