package cn.yanque.models.edu.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.CampusCreateReq;
import cn.yanque.common.pojo.vo.req.CampusPageReq;
import cn.yanque.common.pojo.vo.req.CampusUpdateReq;
import cn.yanque.common.pojo.vo.res.CampusCreateRes;
import cn.yanque.common.pojo.vo.res.CampusDeleteRes;
import cn.yanque.common.pojo.vo.res.CampusDetailRes;
import cn.yanque.common.pojo.vo.res.CampusPageRes;
import cn.yanque.common.pojo.vo.res.CampusUpdateRes;
import cn.yanque.models.edu.service.EduCampusService;
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
 * 校区管理控制器
 * 提供校区的增删改查接口
 */
@RestController
@RequestMapping("/api/eduCampus")
@Slf4j
@Tag(name = "EduCampusController", description = "校区管理")
public class EduCampusController {

    @Autowired
    private EduCampusService eduCampusService;

    /**
     * 添加校区
     * @param req 创建校区请求参数
     * @return 创建成功的校区信息
     */
    @PostMapping
    @Operation(description = "添加校区")
    @RequirePermission("campus:add")
    public ApiResponse<CampusCreateRes> addCampus(@Valid @RequestBody CampusCreateReq req) {
        return ApiResponse.success(eduCampusService.addCampus(req));
    }

    /**
     * 修改校区
     * @param id 校区ID
     * @param req 更新校区请求参数
     * @return 更新后的校区信息
     */
    @PutMapping("{id}")
    @Operation(description = "修改校区")
    @RequirePermission("campus:update")
    public ApiResponse<CampusUpdateRes> updateCampus(@Parameter(description = "校区ID") @PathVariable Long id,
                                                     @Valid @RequestBody CampusUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(eduCampusService.updateCampus(req));
    }

    /**
     * 删除校区
     * @param id 校区ID
     * @return 删除结果
     */
    @DeleteMapping("{id}")
    @Operation(description = "删除校区")
    @RequirePermission("campus:delete")
    public ApiResponse<CampusDeleteRes> deleteCampus(@Parameter(description = "校区ID") @PathVariable Long id) {
        return ApiResponse.success(eduCampusService.deleteCampus(id));
    }

    /**
     * 根据ID查询校区
     * @param id 校区ID
     * @return 校区详细信息
     */
    @GetMapping("{id}")
    @Operation(description = "根据ID查询校区")
    @RequirePermission("campus:view")
    public ApiResponse<CampusDetailRes> getCampusById(@Parameter(description = "校区ID") @PathVariable Long id) {
        return ApiResponse.success(eduCampusService.getCampusById(id));
    }

    /**
     * 分页查询校区
     * @param req 分页查询参数
     * @return 分页校区列表
     */
    @GetMapping
    @Operation(description = "分页查询校区")
    @RequirePermission("campus:view")
    public ApiResponse<PageResult<CampusPageRes>> pageCampus(@Valid @ModelAttribute CampusPageReq req) {
        return ApiResponse.success(eduCampusService.pageCampus(req));
    }
}
