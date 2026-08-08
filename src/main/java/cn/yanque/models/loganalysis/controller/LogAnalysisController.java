package cn.yanque.models.loganalysis.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.loganalysis.pojo.vo.req.LogSearchReq;
import cn.yanque.models.loganalysis.pojo.vo.res.LogSearchRes;
import cn.yanque.models.loganalysis.service.LogAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log-analysis")
public class LogAnalysisController {

    private final LogAnalysisService logAnalysisService;

    @PostMapping("/search")
    public ApiResponse<LogSearchRes> search(@RequestBody @Valid LogSearchReq request) {
        return ApiResponse.success(logAnalysisService.search(request));
    }
}
