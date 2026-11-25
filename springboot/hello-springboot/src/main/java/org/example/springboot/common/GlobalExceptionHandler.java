package org.example.springboot.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 全局异常处理：把「Whitelabel Error Page」换成能看懂的 JSON。
 *
 * Whitelabel 页面出现的原因是：异常一路抛到最外层，Spring Boot 找不到 /error 的处理方法，
 * 就吐一个 500 HTML。有了这个类，任何异常都会变成 { "code": 401, "message": "..." }。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApi(ApiException e) {
        return ResponseEntity.status(e.getStatus()).body(body(e.getStatus(), e.getMessage()));
    }

    /** 访问了不存在的路径：404，不是 500。 */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoResourceFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(body(404, "接口不存在: /" + e.getResourcePath()));
    }

    /** 请求体不是合法 JSON / 字段类型不对：这是客户端的问题，应该是 400 而不是 500。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleBadBody(HttpMessageNotReadableException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(body(400, "请求体不是合法 JSON，检查引号、逗号和字段类型"));
    }


    /** 兜底：打印完整堆栈到控制台，对外只给简短信息（不泄露内部细节）。 */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleOther(Exception e) {
        log.error("未处理的异常", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(body(500, e.getClass().getSimpleName() + ": " + e.getMessage()));
    }

    private Map<String, Object> body(int code, String message) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("code", code);
        m.put("message", message);
        return m;
    }
}
