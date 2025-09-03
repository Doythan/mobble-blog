package org.example.mobble._util.error;

import org.example.mobble._util.error.ex.*;
import org.example.mobble._util.util.Resp;
import org.example.mobble._util.util.Script;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // ← 핵심: 리턴값을 “바디”로 보냄
public class GlobalExceptionHandler {

    // 302 FOUND
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<String> ex302(Exception302 e) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.back(e.getMessage()));
    }

    // 400 Bad Request
    @ExceptionHandler(Exception400.class)
    public ResponseEntity<String> ex400(Exception400 e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.back(e.getMessage()));
    }

    // 401 Unauthorized
    @ExceptionHandler(Exception401.class)
    public ResponseEntity<String> ex401(Exception401 e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.href(e.getMessage(), "/login-form"));
    }

    // 403 Forbidden
    @ExceptionHandler(Exception403.class)
    public ResponseEntity<String> ex403(Exception403 e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.back(e.getMessage()));
    }

    // 404 Not Found
    @ExceptionHandler(Exception404.class)
    public ResponseEntity<String> ex404(Exception404 e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.back(e.getMessage()));
    }

    // Unknown Server Error
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> exUnknown(Exception e) {
        System.out.println("Error Log : " + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.TEXT_HTML)
                .body(Script.back("관리자에게 문의해주세요."));
    }

    // ===== API 오류는 JSON으로 =====
    @ExceptionHandler(ExceptionApi400.class)
    public ResponseEntity<Resp<?>> exApi400(ExceptionApi400 e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Resp.fail(400, e.getMessage()));
    }

    @ExceptionHandler(ExceptionApi401.class)
    public ResponseEntity<Resp<?>> exApi401(ExceptionApi401 e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Resp.fail(401, e.getMessage()));
    }

    @ExceptionHandler(ExceptionApi403.class)
    public ResponseEntity<Resp<?>> exApi403(ExceptionApi403 e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Resp.fail(403, e.getMessage()));
    }

    @ExceptionHandler(ExceptionApi404.class)
    public ResponseEntity<Resp<?>> exApi404(ExceptionApi404 e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Resp.fail(404, e.getMessage()));
    }
}
