package cn.yanque.models.ai.texttosql.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.ai.texttosql.pojo.vo.req.InternalSqlExecuteReq;
import cn.yanque.models.ai.texttosql.pojo.vo.res.InternalSqlExecuteRes;
import cn.yanque.models.ai.texttosql.service.ReadOnlySqlExecutor;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Map;

@RestController
@RequestMapping("/internal/ai/text-to-sql")
public class InternalTextToSqlController {

    @Autowired
    private ReadOnlySqlExecutor executor;

    @Value("${yanque.ai-internal-token:}")
    private String expectedToken;

    @PostMapping("/execute")
    public ApiResponse<?> execute(
            @Valid @RequestBody InternalSqlExecuteReq request,
            @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        if (!validToken(token)) {
            return new ApiResponse<>(403, "内部接口令牌无效", Map.of("exceptionClass", "AccessDeniedException"));
        }
        try {
            InternalSqlExecuteRes result = executor.execute(request);
            return ApiResponse.success(result);
        } catch (Exception exception) {
            return new ApiResponse<>(400, safeMessage(exception), Map.of(
                    "exceptionClass", exception.getClass().getSimpleName()
            ));
        }
    }

    private boolean validToken(String token) {
        if (expectedToken == null || expectedToken.isBlank()) {
            return true;
        }
        return MessageDigest.isEqual(
                expectedToken.getBytes(StandardCharsets.UTF_8),
                (token == null ? "" : token).getBytes(StandardCharsets.UTF_8)
        );
    }

    private String safeMessage(Exception exception) {
        String message = exception.getMessage();
        if (message == null || message.isBlank()) {
            return "数据库暂时无法完成该查询";
        }
        message = message.replaceAll("[\\r\\n]+", " ").trim();
        return message.length() > 180 ? message.substring(0, 180) + "..." : message;
    }
}
