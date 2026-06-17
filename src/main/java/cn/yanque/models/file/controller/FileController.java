package cn.yanque.models.file.controller;

import cn.yanque.common.annotation.SkipPermission;
import cn.yanque.common.api.ApiResponse;
import cn.yanque.models.file.pojo.vo.FileUploadRes;
import cn.yanque.models.file.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件接口。
 * 用登录态和签名拦截器保证调用者身份，业务权限后续由具体作业接口控制。
 */
@RestController
@RequestMapping("/api/file")
@SkipPermission
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 上传单个文件到阿里云 OSS。
     * bizType 用于分业务目录，例如 assignment、submission、answer。
     */
    @PostMapping("/upload")
    public ApiResponse<FileUploadRes> upload(@RequestParam MultipartFile file,
                                             @RequestParam(defaultValue = "common") String bizType,
                                             HttpServletRequest request) {
        return ApiResponse.success(fileService.upload(file, bizType, request));
    }

    /**
     * 生成临时预览地址。
     * objectKey 是上传接口返回的 url 字段。
     */
    @GetMapping("/preview")
    public ApiResponse<String> preview(@RequestParam String objectKey) {
        return ApiResponse.success("success", fileService.preview(objectKey));
    }

    /**
     * 生成临时下载地址。
     * objectKey 是上传接口返回的 url 字段。
     */
    @GetMapping("/download")
    public ApiResponse<String> download(@RequestParam String objectKey) {
        return ApiResponse.success("success", fileService.download(objectKey));
    }

    /**
     * 删除 OSS 文件。
     * objectKey 是上传接口返回的 url 字段。
     */
    @DeleteMapping
    public ApiResponse<Void> delete(@RequestParam String objectKey) {
        fileService.delete(objectKey);
        return ApiResponse.success();
    }
}
