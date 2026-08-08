package cn.yanque.models.ai.prompt.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.ai.prompt.pojo.PromptTemplateReq;
import cn.yanque.models.ai.prompt.pojo.PromptTemplateRes;
import cn.yanque.models.ai.prompt.pojo.PromptVersionReq;
import cn.yanque.models.ai.prompt.pojo.PromptVersionRes;
import cn.yanque.models.ai.prompt.service.PromptTemplateService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promptTemplates")
@RequirePermission("menu:ai:prompt")
public class PromptTemplateController {

    @Autowired
    private PromptTemplateService service;

    @GetMapping
    public ApiResponse<PageResult<PromptTemplateRes>> page(
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.success(service.page(code, name, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<PromptTemplateRes> detail(@PathVariable Long id) {
        return ApiResponse.success(service.detail(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> create(@Valid @RequestBody PromptTemplateReq req) {
        return ApiResponse.success(Map.of("id", service.create(req)));
    }

    @PutMapping("/{id}")
    public ApiResponse<Map<String, Long>> update(@PathVariable Long id, @Valid @RequestBody PromptTemplateReq req) {
        return ApiResponse.success(Map.of("id", service.update(id, req)));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Map<String, Long>> delete(@PathVariable Long id) {
        return ApiResponse.success(Map.of("id", service.delete(id)));
    }

    @GetMapping("/{id}/versions")
    public ApiResponse<List<PromptVersionRes>> versions(@PathVariable Long id) {
        return ApiResponse.success(service.versions(id));
    }

    @PostMapping("/{id}/versions")
    public ApiResponse<Map<String, Long>> createVersion(@PathVariable Long id, @Valid @RequestBody PromptVersionReq req) {
        return ApiResponse.success(Map.of("id", service.createVersion(id, req)));
    }

    @PutMapping("/{id}/versions/{versionId}/activate")
    public ApiResponse<Map<String, Long>> activate(@PathVariable Long id, @PathVariable Long versionId) {
        return ApiResponse.success(Map.of("id", service.activate(id, versionId)));
    }
}
