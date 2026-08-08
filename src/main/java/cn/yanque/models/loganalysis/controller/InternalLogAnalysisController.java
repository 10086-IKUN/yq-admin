package cn.yanque.models.loganalysis.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.loganalysis.pojo.vo.req.LogSearchReq;
import cn.yanque.models.loganalysis.pojo.vo.res.LogSearchRes;
import cn.yanque.models.loganalysis.service.LogAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/ai/log-analysis")
public class InternalLogAnalysisController {

    private final LogAnalysisService logAnalysisService;

    @Value("${yanque.ai-internal-token:}")
    private String expectedToken;

    @PostMapping("/search")
    public ApiResponse<LogSearchRes> search(
            @RequestBody @Valid LogSearchReq request,
            @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        if (!validToken(token)) {
            return ApiResponse.fail(403, "内部接口令牌无效");
        }
        return ApiResponse.success(logAnalysisService.search(request));
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
}
