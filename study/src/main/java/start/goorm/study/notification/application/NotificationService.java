package start.goorm.study.notification.application;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import start.goorm.study.common.domain.ClockHolder;
import start.goorm.study.notification.dto.EventPayload;
import start.goorm.study.notification.infrastructure.EmitterRepository;


import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class NotificationService {

    private final EmitterRepository emitterRepository;
    private final ClockHolder clockHolder;
    private static final long TIMEOUT = 60 * 1000L * 10; // 10분
    private static final long RECONNECTION_TIMEOUT = 1000L; // 1초
    private final Logger log = LoggerFactory.getLogger(getClass());

    // emitter : 방출기
    // SseEmitter : 클라이언트랑 매핑되는 통신객체
    public SseEmitter subscribe(Long userId, String lastEventId) {
        String eventId = generateEventId(userId);
        SseEmitter sseEmitter = emitterRepository.save(eventId, new SseEmitter(TIMEOUT));
        registerEmitterHandler(eventId, sseEmitter);
        sendToClient(eventId, sseEmitter, "알림 구독 성공 [userId] = " + userId); // 첫 연결 후 아무 데이터가 보내지지않은채, 재연결을 시도하면 503 에러가 발생한다고 한다.
        recoverData(userId, lastEventId, sseEmitter);
        return sseEmitter;
    }

    public void broadcast(EventPayload eventPayload) {
        Map<String, SseEmitter> emitters = emitterRepository.findAll();
        emitters.forEach((k, v) -> {
            try {
                v.send(SseEmitter.event()
                        .name("broadcast event")
                        .id("broadcast event 1")
                        .reconnectTime(RECONNECTION_TIMEOUT)
                        .data(eventPayload, MediaType.APPLICATION_JSON));
                log.info("sended notification, id={}, payload={}", k, eventPayload);
            } catch (IOException e) {
                //SSE 세션이 이미 해제된 경우
                log.error("fail to send emitter id={}, {}", k, e.getMessage());
            }
        });
    }

    private void recoverData(Long userId, String lastEventId, SseEmitter sseEmitter) {
        if (Objects.nonNull(lastEventId)) {
            Map<String, Object> events = emitterRepository.findAllEventStartWithUserId(userId);
            events.entrySet().stream()
                    .filter(e -> lastEventId.compareTo(e.getKey()) < 0) // ascii a - b e.g. lastEventId : 3, 1 2 3/ "4 5"
                    .forEach(e -> sendToClient(e.getKey(), sseEmitter, e.getValue()));
        }
    }

    private void sendToClient(String eventId, SseEmitter sseEmitter, Object data) {
        SseEmitter.SseEventBuilder event = getSseEvent(eventId, data);
        try {
            sseEmitter.send(event);
        } catch (IOException e) {
            log.error("구독 실패, eventId={}, {}", eventId, e.getMessage());
        }
    }

    private void registerEmitterHandler(String eventId, SseEmitter sseEmitter) {
        sseEmitter.onCompletion(() -> {
            log.info("연결이 끝났습니다. : eventId={}", eventId);
            emitterRepository.deleteByEventId(eventId);
        });
        sseEmitter.onTimeout(() -> {
            log.info("Timeout이 발생했습니다. : eventId={}", eventId);
            emitterRepository.deleteByEventId(eventId);
        });
        sseEmitter.onError((e) -> {
            log.info("에러가 발생했습니다. error={}, eventId={}", e.getMessage(), eventId);
            emitterRepository.deleteByEventId(eventId);
        });
    }

    private SseEmitter.SseEventBuilder getSseEvent(String eventId, Object data) {
        return SseEmitter.event()
                .id(eventId)
                .data(data)
                .reconnectTime(RECONNECTION_TIMEOUT);
    }

    private String generateEventId(Long userId) {
        return userId + "_"+ clockHolder.mills();
    }
}
