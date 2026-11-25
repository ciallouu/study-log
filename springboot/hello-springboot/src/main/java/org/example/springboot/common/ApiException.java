package org.example.springboot.common;

/**
 * 业务异常：带 HTTP 状态码，由 GlobalExceptionHandler 统一转成 JSON。
 * 用它替代「返回 null / 返回 false」这种看不出原因的写法。
 */
public class ApiException extends RuntimeException {

    private final int status;

    public ApiException(int status, String message) {
        super(message);
        this.status = status;
    }

    public int getStatus() {
        return status;
    }
}
