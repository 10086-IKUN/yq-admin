package cn.yanque.models.loganalysis.pojo.vo.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LogDiagnosisReq {

    @NotBlank(message = "GUID 不能为空")
    @Size(max = 128, message = "GUID 长度不能超过 128")
    @Pattern(regexp = "[A-Za-z0-9_-]+", message = "GUID 格式不正确")
    private String guid;
}
