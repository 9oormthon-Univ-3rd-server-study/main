package start.goorm.study.notification.infrastructure;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(String eventId, SseEmitter sseEmitter);
    void saveEvent(String eventId, Object event);
    Map<String, SseEmitter> findAllStartWithUserId(Long userId);
    Map<String, Object> findAllEventStartWithUserId(Long userId);
    void deleteByEventId(String eventId);
    void deleteAllStartWithUserId(Long userId);
    void deleteAllEventStartWithUserId(Long userId);
    Map<String, SseEmitter> findAll();
}
