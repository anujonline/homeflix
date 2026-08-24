package com.homeflix.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Pings self to prevent Render free tier spin-down (sleeps after ~15 min idle).
 * Mirrors the Vaadin app's self-calling setup.
 * Disable with SELF_PING_ENABLED=false or by leaving RENDER_EXTERNAL_URL / SELF_PING_URL blank.
 */
@Component
public class KeepAliveScheduler {

    private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

    private final RestTemplate restTemplate;
    private final boolean enabled;
    private final String pingUrl;
    private final long intervalMs;

    public KeepAliveScheduler(RestTemplate restTemplate,
                              @Value("${homeflix.self-ping.enabled:true}") boolean enabled,
                              @Value("${homeflix.self-ping.url:}") String pingUrl,
                              @Value("${homeflix.self-ping.interval-ms:840000}") long intervalMs) {
        this.restTemplate = restTemplate;
        this.enabled = enabled;
        this.pingUrl = pingUrl != null ? pingUrl.trim().replaceAll("/$", "") : "";
        this.intervalMs = intervalMs;
    }

    // fixedDelayString allows env override; initialDelay gives app time to start
    @Scheduled(fixedDelayString = "${homeflix.self-ping.interval-ms:840000}", initialDelay = 60000)
    public void pingSelf() {
        if (!enabled || pingUrl == null || pingUrl.isBlank()) {
            log.debug("Self-ping disabled (enabled={}, url='{}')", enabled, pingUrl);
            return;
        }
        String target = pingUrl + "/api/health";
        try {
            String body = restTemplate.getForObject(target, String.class);
            log.info("Self-ping ok -> {} : {}", target, body);
        } catch (Exception e) {
            log.warn("Self-ping failed -> {} : {}", target, e.getMessage());
        }
    }
}
