package cn.yanque.models.loganalysis.controller;

import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.loganalysis.pojo.vo.req.LogDiagnosisReq;
import cn.yanque.models.loganalysis.service.PythonLogDiagnosisClient;
import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log-diagnosis")
public class LogDiagnosisController {

    private final PythonLogDiagnosisClient pythonClient;

    @PostMapping("/context")
    public ApiResponse<JSONObject> context(@RequestBody @Valid LogDiagnosisReq request) {
        return ApiResponse.success(pythonClient.context(request));
    }

    @PostMapping("/analyze")
    public ApiResponse<JSONObject> analyze(@RequestBody @Valid LogDiagnosisReq request) {
        return ApiResponse.success(pythonClient.analyze(request));
    }
}
