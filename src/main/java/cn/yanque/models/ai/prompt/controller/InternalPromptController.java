package cn.yanque.models.ai.prompt.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.ai.prompt.pojo.PromptTemplateRes;
import cn.yanque.models.ai.prompt.service.PromptTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/internal/ai/prompts")
public class InternalPromptController {

    @Autowired
    private PromptTemplateService service;

    @Value("${yanque.ai-internal-token:}")
    private String expectedToken;

    @GetMapping("/active")
    public ApiResponse<Map<String, Object>> active(
            @RequestParam String code,
            @RequestHeader(value = "X-Internal-Token", required = false) String token) {
        verifyToken(token);
        PromptTemplateRes prompt = service.activeByCode(code.trim());
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("code", prompt.getCode());
        data.put("content", prompt.getActiveContent());
        data.put("versionId", prompt.getActiveVersionId());
        data.put("versionNo", prompt.getActiveVersionNo());
        return ApiResponse.success(data);
    }

    private void verifyToken(String token) {
        if (expectedToken == null || expectedToken.isBlank()) {
            return;
        }
        byte[] expected = expectedToken.getBytes(StandardCharsets.UTF_8);
        byte[] actual = (token == null ? "" : token).getBytes(StandardCharsets.UTF_8);
        if (!MessageDigest.isEqual(expected, actual)) {
            throw new BusinessException(403, "内部接口令牌无效");
        }
    }
}
