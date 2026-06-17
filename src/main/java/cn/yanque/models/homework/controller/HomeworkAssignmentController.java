package cn.yanque.models.homework.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAnswerPublishReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.homework.pojo.vo.res.HomeworkIdRes;
import cn.yanque.models.homework.service.HomeworkAssignmentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端作业发布接口。
 */
@RestController
@RequestMapping("/api/homeworkAssignment")
public class HomeworkAssignmentController {

    @Autowired
    private HomeworkAssignmentService homeworkAssignmentService;

    /**
     * 发布作业。
     * 当前登录老师 ID 从 request 中取，用来记录发布人。
     */
    @PostMapping
    @RequirePermission("homework:create")
    public ApiResponse<HomeworkIdRes> create(@Valid @RequestBody HomeworkAssignmentReq req,
                                             HttpServletRequest request) {
        return ApiResponse.success(homeworkAssignmentService.create(req, currentUserId(request)));
    }

    /**
     * 编辑作业基础信息。
     * 附件字段也在 req 中，前端会在点击“确定”后再提交最终结果。
     */
    @PutMapping("{id}")
    @RequirePermission("homework:update")
    public ApiResponse<HomeworkIdRes> update(@PathVariable Long id,
                                             @Valid @RequestBody HomeworkAssignmentReq req) {
        return ApiResponse.success(homeworkAssignmentService.update(id, req));
    }

    /**
     * 删除作业记录。
     * 这里删除的是作业业务数据，不是单独的附件删除按钮。
     */
    @DeleteMapping("{id}")
    @RequirePermission("homework:update")
    public ApiResponse<HomeworkIdRes> delete(@PathVariable Long id) {
        return ApiResponse.success(homeworkAssignmentService.delete(id));
    }

    /**
     * 关闭作业。
     * 用于停止继续提交，同时记录关闭老师。
     */
    @PutMapping("{id}/close")
    @RequirePermission("homework:status")
    public ApiResponse<HomeworkIdRes> close(@PathVariable Long id, HttpServletRequest request) {
        return ApiResponse.success(homeworkAssignmentService.close(id, currentUserId(request)));
    }

    /**
     * 发布或替换答案附件。
     * 老师点击答案弹窗“确定”后调用。
     */
    @PutMapping("{id}/answer")
    @RequirePermission("homework:answer")
    public ApiResponse<HomeworkIdRes> publishAnswer(@PathVariable Long id,
                                                    @Valid @RequestBody HomeworkAnswerPublishReq req,
                                                    HttpServletRequest request) {
        return ApiResponse.success(homeworkAssignmentService.publishAnswer(id, req, currentUserId(request)));
    }

    /**
     * 删除作业附件。
     * 会同时删除 OSS 文件并清空数据库中的附件字段。
     */
    @DeleteMapping("{id}/attachment")
    @RequirePermission("homework:update")
    public ApiResponse<HomeworkIdRes> deleteAttachment(@PathVariable Long id) {
        return ApiResponse.success(homeworkAssignmentService.deleteAttachment(id));
    }

    /**
     * 删除答案附件。
     * 会同时删除 OSS 文件，并把答案状态恢复为未发布。
     */
    @DeleteMapping("{id}/answerAttachment")
    @RequirePermission("homework:answer")
    public ApiResponse<HomeworkIdRes> deleteAnswerAttachment(@PathVariable Long id) {
        return ApiResponse.success(homeworkAssignmentService.deleteAnswerAttachment(id));
    }

    /**
     * 查询作业详情。
     * 编辑弹窗打开时会用它拿完整数据。
     */
    @GetMapping("{id}")
    @RequirePermission("homework:view")
    public ApiResponse<HomeworkAssignmentRes> detail(@PathVariable Long id) {
        return ApiResponse.success(homeworkAssignmentService.detail(id));
    }

    /**
     * 分页查询作业列表。
     * 支持标题、班级、课程、状态等筛选。
     */
    @GetMapping
    @RequirePermission("homework:view")
    public ApiResponse<PageResult<HomeworkAssignmentRes>> page(@ModelAttribute HomeworkAssignmentPageReq req) {
        return ApiResponse.success(homeworkAssignmentService.page(req));
    }

    private Long currentUserId(HttpServletRequest request) {
        // 登录拦截器会把 userId 放到 request，这里统一转成 Long。
        return Long.parseLong(String.valueOf(request.getAttribute("userId")));
    }
}
