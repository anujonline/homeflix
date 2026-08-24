package com.homeflix.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handles POST (and other non-GET) to SPA routes and /api without noisy 405 WARN.
 * The production log showed POST to unknown path -> 405 via DefaultHandlerExceptionResolver.
 * This is often the Tianji tracker or a browser extension POSTing to same origin.
 * We log at DEBUG and return 404/204 to keep Render logs clean.
 */
@RestController
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    // Accept POST to health (some uptime checkers use POST)
    @RequestMapping(value = "/api/health", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.HEAD, RequestMethod.OPTIONS})
    public ResponseEntity<String> healthAlt(HttpServletRequest req) {
        if (!"GET".equalsIgnoreCase(req.getMethod())) {
            log.debug("Health {} from {} {}", req.getMethod(), req.getRemoteAddr(), req.getRequestURI());
        }
        return ResponseEntity.ok("{\"status\":\"ok\"}");
    }

    // Tianji/other analytics may POST to /api/event, /api/send, /api/collect on same host if misconfigured
    @RequestMapping(value = {"/api/event", "/api/send", "/api/collect", "/api/track"}, method = RequestMethod.POST)
    public ResponseEntity<Void> analyticsSink(HttpServletRequest req) {
        log.debug("Analytics sink {} {} dropped", req.getMethod(), req.getRequestURI());
        return ResponseEntity.noContent().build();
    }

    // Catch-all POST to avoid 405 WARN for SPA routes (e.g. POST / or POST /watch/...)
    @RequestMapping(value = "/**", method = RequestMethod.POST)
    public ResponseEntity<Void> postFallback(HttpServletRequest req) {
        // Don't intercept /api/** that actually has no handler — return 404 quietly
        String uri = req.getRequestURI();
        if (uri.startsWith("/api/")) {
            log.debug("Unhandled POST {} from {}", uri, req.getRemoteAddr());
            return ResponseEntity.notFound().build();
        }
        // SPA POST (unlikely) — return 404 without WARN
        log.debug("SPA POST {} from {}", uri, req.getRemoteAddr());
        return ResponseEntity.notFound().build();
    }
}
