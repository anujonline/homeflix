package com.homeflix.app;

import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
public class JC {
    private final Map<String, Duration> stringMap = new ConcurrentHashMap<>();

    public void startSession(String sessionId) {
        try {
            stringMap.putIfAbsent(sessionId, new Duration().setStartTime(LocalDateTime.now()));
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void stopSession(String sessionId) {
        try {
            stringMap.get(sessionId).setEndTime(LocalDateTime.now());
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/sessions/all")
    public Map<String, Duration> getAllSessions() {
        return stringMap;
    }

    @GetMapping("/sessions/alive")
    public long getAliveSessions() {
        return stringMap.values().stream().filter(duration -> ObjectUtils.anyNull(duration.getEndTime())).count();
    }
}

@Data
@Accessors(chain = true)
class Duration {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
