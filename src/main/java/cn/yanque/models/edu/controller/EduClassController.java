package cn.yanque.models.edu.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;
import cn.yanque.models.edu.service.EduClassService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
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
 * 班级管理控制器
 * 提供班级的增删改查接口
 */
@RestController
@RequestMapping("/api/eduClass")
@Slf4j
@Tag(name = "EduClassController", description = "班级管理")
public class EduClassController {

    @Autowired
    private EduClassService eduClassService;

    /**
     * 添加班级
     * @param req 创建班级请求参数
     * @return 创建成功的班级信息
     */
    @PostMapping
    @Operation(description = "添加班级")
    @RequirePermission("class:add")
    public ApiResponse<ClassCreateRes> addClass(@Valid @RequestBody ClassCreateReq req) {
        return ApiResponse.success(eduClassService.addClass(req));
    }

    /**
     * 修改班级
     * @param id 班级ID
     * @param req 更新班级请求参数
     * @return 更新后的班级信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改班级")
    @RequirePermission("class:update")
    public ApiResponse<ClassUpdateRes> updateClass(@Parameter(description = "班级ID") @PathVariable Long id,
                                                   @Valid @RequestBody ClassUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduClassService.updateClass(req));
    }

    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除班级")
    @RequirePermission("class:delete")
    public ApiResponse<ClassDeleteRes> deleteClass(@Parameter(description = "班级ID") @PathVariable Long id) {
        return ApiResponse.success(eduClassService.deleteClass(id));
    }

    /**
     * 根据ID查询班级
     * @param id 班级ID
     * @return 班级详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询班级")
    @RequirePermission("class:view")
    public ApiResponse<ClassDetailRes> getClassById(@Parameter(description = "班级ID") @PathVariable Long id) {
        return ApiResponse.success(eduClassService.getClassById(id));
    }

    /**
     * 分页查询班级
     * @param req 分页查询参数
     * @return 分页班级列表
     */
    @GetMapping
    @Operation(description = "分页查询班级")
    @RequirePermission("class:view")
    public ApiResponse<PageResult<ClassPageRes>> pageClass(@Valid @ModelAttribute ClassPageReq req) {
        return ApiResponse.success(eduClassService.pageClass(req));
    }
}
