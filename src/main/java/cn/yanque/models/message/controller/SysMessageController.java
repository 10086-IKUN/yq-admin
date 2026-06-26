package cn.yanque.models.message.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.message.pojo.entity.SysMessageEntity;
import cn.yanque.models.message.service.SysMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/message")
@Tag(name = "SysMessageController", description = "系统消息管理")
public class SysMessageController {

    @Autowired
    private SysMessageService sysMessageService;

    @GetMapping("/list")
    @Operation(description = "获取消息列表")
    public ApiResponse<List<SysMessageEntity>> list(
            @RequestParam(required = false) Integer isRead,
            HttpServletRequest request) {
        Long userId = currentUserId(request);
        return ApiResponse.success(sysMessageService.list(userId, isRead));
    }

    @GetMapping("/unread-count")
    @Operation(description = "获取未读消息数")
    public ApiResponse<Map<String, Integer>> unreadCount(HttpServletRequest request) {
        Long userId = currentUserId(request);
        int count = sysMessageService.countUnread(userId);
        return ApiResponse.success(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    @Operation(description = "标记已读")
    public ApiResponse<Void> markAsRead(@PathVariable Long id) {
        sysMessageService.markAsRead(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/read-all")
    @Operation(description = "标记全部已读")
    public ApiResponse<Void> markAllAsRead(HttpServletRequest request) {
        Long userId = currentUserId(request);
        sysMessageService.markAllAsRead(userId);
        return ApiResponse.success(null);
    }

    private Long currentUserId(HttpServletRequest request) {
        return Long.parseLong(String.valueOf(request.getAttribute("userId")));
    }
}
