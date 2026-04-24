package tech.artadevs.finances.controllers;

import java.util.Map;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RootController implements ErrorController {
    @GetMapping(value = "/", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> rootHtml() {
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body("<!DOCTYPE html><html lang=\"en\"><head><meta charset=\"UTF-8\"/><title>Finances API</title></head><body><h1>Hello, World!</h1></body></html>");
    }

    @GetMapping(value = "/", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> rootJson() {
        return ResponseEntity.ok(Map.of("message", "Hello, World!"));
    }
}
