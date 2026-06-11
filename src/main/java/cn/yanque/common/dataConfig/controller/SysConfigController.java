package cn.yanque.common.dataConfig.controller;

import cn.yanque.common.annotation.RequirePermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.common.dataConfig.service.ConfigManageService;
import cn.yanque.common.pojo.vo.req.ConfigCreateReq;
import cn.yanque.common.pojo.vo.req.ConfigUpdateReq;
import cn.yanque.common.pojo.vo.res.ConfigCreateRes;
import cn.yanque.common.pojo.vo.res.ConfigDeleteRes;
import cn.yanque.common.pojo.vo.res.ConfigDetailRes;
import cn.yanque.common.pojo.vo.res.ConfigPageRes;
import cn.yanque.common.pojo.vo.res.ConfigUpdateRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/sysConfig")
@Slf4j
@Tag(name = "SysConfigController", description = "系统配置管理")
public class SysConfigController {

    @Autowired
    private ConfigManageService configManageService;

    @PostMapping
    @Operation(description = "添加配置")
    @RequirePermission("config:add")
    public ApiResponse<ConfigCreateRes> addConfig(@Valid @RequestBody ConfigCreateReq req) {
        return ApiResponse.success(configManageService.addConfig(req));
    }

    @PutMapping("{id}")
    @Operation(description = "修改配置")
    @RequirePermission("config:update")
    public ApiResponse<ConfigUpdateRes> updateConfig(@Parameter(description = "配置ID") @PathVariable Long id,
                                                     @Valid @RequestBody ConfigUpdateReq req) {
        req.setId(id);
        return ApiResponse.success(configManageService.updateConfig(req));
    }

    @DeleteMapping("{id}")
    @Operation(description = "删除配置")
    @RequirePermission("config:delete")
    public ApiResponse<ConfigDeleteRes> deleteConfig(@Parameter(description = "配置ID") @PathVariable Long id) {
        return ApiResponse.success(configManageService.deleteConfig(id));
    }

    @GetMapping("{id}")
    @Operation(description = "根据ID查询配置")
    @RequirePermission("config:view")
    public ApiResponse<ConfigDetailRes> getConfigById(@Parameter(description = "配置ID") @PathVariable Long id) {
        return ApiResponse.success(configManageService.getConfigById(id));
    }

    @GetMapping
    @Operation(description = "查询全部配置")
    @RequirePermission("config:view")
    public ApiResponse<List<ConfigPageRes>> listAll() {
        return ApiResponse.success(configManageService.listAll());
    }
}
