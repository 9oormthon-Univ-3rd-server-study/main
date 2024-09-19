package start.goorm.study.notification.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import start.goorm.study.common.config.LoginUser;
import start.goorm.study.notification.application.NotificationService;
import start.goorm.study.notification.dto.EventPayload;


@RequiredArgsConstructor
@RestController
public class NotificationController {

    private final NotificationService notificationService;

    // lastEventId : 처음 구독할 때는 Last-Event-ID 헤더가 포함되지 않습니다.
    // 네트워크 등의 문제(새로고침 x)
    @GetMapping(value = "/api/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId,
            @AuthenticationPrincipal LoginUser loginUser) {
        System.out.println("userId " + loginUser.getUserId());
        return notificationService.subscribe(loginUser.getUserId(), lastEventId);
    }

    @PostMapping("/api/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody EventPayload eventPayload) {
        notificationService.broadcast(eventPayload);
        return ResponseEntity.ok().build();
    }

}

