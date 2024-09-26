package start.goorm.study.notification.infrastructure;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Repository
public class SseEmitterRepository implements EmitterRepository{

    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    // 유실된 데이터 처리 위함
    private final Map<String, Object> events = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String eventId, SseEmitter sseEmitter) {
        emitters.put(eventId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEvent(String eventId, Object event) {
        events.put(eventId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllStartWithUserId(Long userId) {
        // Set { 1_11시20분, 1_11시24
        //    Entry { key: "client1", value: SseEmitter@123 }, Map.Entry<String, SseEmitter>
        //    Entry { key: "client2", value: SseEmitter@456 },
        //    Entry { key: "client3", value: SseEmitter@789 }
        //}
        return emitters.entrySet().stream() // 아마 stream쓰려면 iterator 구현한 자료구조여야한걸로 기억
                .filter(e -> e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventStartWithUserId(Long userId) {
        return events.entrySet().stream()
                .filter(e -> e.getKey().startsWith(userId.toString()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteByEventId(String eventId) {
        emitters.remove(eventId);
    }

    @Override
    public void deleteAllStartWithUserId(Long userId) {
        emitters.forEach((k,v) -> {
            if (k.startsWith(userId.toString())) events.remove(k);
        });
    }


    @Override
    public void deleteAllEventStartWithUserId(Long userId) {
        events.forEach((k,v) -> {
            if (k.startsWith(userId.toString())) events.remove(k);
        });
    }

    @Override
    public Map<String, SseEmitter> findAll() {
        return emitters;
    }


    ReentrantLock lock = new ReentrantLock();
}
