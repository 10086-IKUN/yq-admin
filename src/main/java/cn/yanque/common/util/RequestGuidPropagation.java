package cn.yanque.common.util;

import org.slf4j.MDC;

import java.net.http.HttpRequest;

/**
 * 在服务间 HTTP 调用中继续传递当前请求 GUID。
 */
public final class RequestGuidPropagation {

    public static final String HEADER_NAME = "X-Request-Guid";
    private static final String MDC_KEY = "guid";

    private RequestGuidPropagation() {
    }

    public static HttpRequest.Builder apply(HttpRequest.Builder builder) {
        String guid = MDC.get(MDC_KEY);
        if (guid != null && !guid.isBlank()) {
            builder.header(HEADER_NAME, guid);
        }
        return builder;
    }
}
