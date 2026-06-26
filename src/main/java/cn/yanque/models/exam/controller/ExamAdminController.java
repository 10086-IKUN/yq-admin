package cn.yanque.models.exam.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperEntity;
import cn.yanque.models.exam.pojo.entity.ExamQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamPaperReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionPageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamQuestionReq;
import cn.yanque.models.exam.pojo.vo.req.ExamReviewReq;
import cn.yanque.models.exam.pojo.vo.req.ExamSchedulePageReq;
import cn.yanque.models.exam.pojo.vo.req.ExamScheduleReq;
import cn.yanque.models.exam.pojo.vo.res.ExamPaperDetailRes;
import cn.yanque.models.exam.service.ExamAdminService;
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

import java.util.List;

/**
 * 管理端在线考试接口。
 */
@RestController
@RequestMapping("/api/exams")
public class ExamAdminController {

    @Autowired
    private ExamAdminService examAdminService;

    /** 新增题目。 */
    @PostMapping("/questions")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> createQuestion(@Valid @RequestBody ExamQuestionReq req,
                                            HttpServletRequest request) {
        return ApiResponse.success(examAdminService.createQuestion(req, currentUserId(request)));
    }

    /** 编辑题目。 */
    @PutMapping("/questions/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> updateQuestion(@PathVariable Long id,
                                            @Valid @RequestBody ExamQuestionReq req) {
        return ApiResponse.success(examAdminService.updateQuestion(id, req));
    }

    /** 删除未被试卷使用的题目。 */
    @DeleteMapping("/questions/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> deleteQuestion(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.deleteQuestion(id));
    }

    /** 分页查询题库。 */
    @GetMapping("/questions")
    @RequirePermission("exam:view")
    public ApiResponse<PageResult<ExamQuestionEntity>> questionPage(
            @ModelAttribute ExamQuestionPageReq req) {
        return ApiResponse.success(examAdminService.questionPage(req));
    }

    /** 新增试卷。 */
    @PostMapping("/papers")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> createPaper(@Valid @RequestBody ExamPaperReq req,
                                         HttpServletRequest request) {
        return ApiResponse.success(examAdminService.createPaper(req, currentUserId(request)));
    }

    /** 编辑试卷。 */
    @PutMapping("/papers/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> updatePaper(@PathVariable Long id,
                                         @Valid @RequestBody ExamPaperReq req) {
        return ApiResponse.success(examAdminService.updatePaper(id, req));
    }

    /** 删除尚未用于考试的试卷。 */
    @DeleteMapping("/papers/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> deletePaper(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.deletePaper(id));
    }

    /** 查询试卷详情。 */
    @GetMapping("/papers/{id}")
    @RequirePermission("exam:view")
    public ApiResponse<ExamPaperDetailRes> paperDetail(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.paperDetail(id));
    }

    /** 分页查询试卷。 */
    @GetMapping("/papers")
    @RequirePermission("exam:view")
    public ApiResponse<PageResult<ExamPaperEntity>> paperPage(
            @ModelAttribute ExamPaperPageReq req) {
        return ApiResponse.success(examAdminService.paperPage(req));
    }

    /** 发布考试。 */
    @PostMapping("/schedules")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> createSchedule(@Valid @RequestBody ExamScheduleReq req,
                                            HttpServletRequest request) {
        return ApiResponse.success(examAdminService.createSchedule(req, currentUserId(request)));
    }

    /** 编辑尚无人参加的考试。 */
    @PutMapping("/schedules/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> updateSchedule(@PathVariable Long id,
                                            @Valid @RequestBody ExamScheduleReq req) {
        return ApiResponse.success(examAdminService.updateSchedule(id, req));
    }

    /** 删除尚无人参加的考试。 */
    @DeleteMapping("/schedules/{id}")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> deleteSchedule(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.deleteSchedule(id));
    }

    /** 老师提前发布答案和成绩。 */
    @PutMapping("/schedules/{id}/publish-answers")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> publishAnswers(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.publishAnswers(id));
    }

    /** 分页查询考试发布记录。 */
    @GetMapping("/schedules")
    @RequirePermission("exam:view")
    public ApiResponse<PageResult<ExamScheduleEntity>> schedulePage(
            @ModelAttribute ExamSchedulePageReq req) {
        return ApiResponse.success(examAdminService.schedulePage(req));
    }

    /** 查询某场考试的学员考试记录。 */
    @GetMapping("/schedules/{id}/attempts")
    @RequirePermission("exam:view")
    public ApiResponse<List<ExamAttemptEntity>> attempts(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.attempts(id));
    }

    /** 查询某次考试记录的逐题答案。 */
    @GetMapping("/attempts/{id}/answers")
    @RequirePermission("exam:view")
    public ApiResponse<List<ExamAnswerEntity>> attemptAnswers(@PathVariable Long id) {
        return ApiResponse.success(examAdminService.attemptAnswers(id));
    }

    /** 批改一道主观题。 */
    @PutMapping("/answers/{id}/review")
    @RequirePermission("exam:manage")
    public ApiResponse<Long> reviewAnswer(@PathVariable Long id,
                                          @Valid @RequestBody ExamReviewReq req,
                                          HttpServletRequest request) {
        return ApiResponse.success(examAdminService.reviewAnswer(id, req, currentUserId(request)));
    }

    /**
     * 获取当前登录老师 ID。
     */
    private Long currentUserId(HttpServletRequest request) {
        return Long.parseLong(String.valueOf(request.getAttribute("userId")));
    }
}
