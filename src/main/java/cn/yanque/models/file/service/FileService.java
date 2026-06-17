package cn.yanque.models.file.service;

import cn.yanque.models.file.pojo.vo.FileUploadRes;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.multipart.MultipartFile;

/**
 * 通用文件服务。
 *
 * <p>当前主要服务作业模块：老师上传作业附件、答案附件，后续学员端也可以复用。
 * 这里统一封装 OSS 上传、预览、下载、删除，业务表只需要保存上传后返回的 objectKey。</p>
 */
public interface FileService {

    /**
     * 上传文件到 OSS。
     *
     * @param file    前端提交的文件对象。
     * @param bizType 业务类型，用来区分 OSS 目录，例如 assignment、answer、submission。
     * @param request 当前请求，用来读取登录拦截器写入的 userId 或 studentId，从而拼接上传人姓名目录。
     * @return 上传结果。url 字段不是永久访问地址，而是 OSS objectKey。
     */
    FileUploadRes upload(MultipartFile file, String bizType, HttpServletRequest request);

    /**
     * 生成文件预览地址。
     *
     * @param objectKey 上传接口返回的 objectKey；兼容旧数据中的完整 OSS URL。
     * @return 短期有效的预签名地址，浏览器会尽量以内联方式打开。
     */
    String preview(String objectKey);

    /**
     * 生成文件下载地址。
     *
     * @param objectKey 上传接口返回的 objectKey；兼容旧数据中的完整 OSS URL。
     * @return 短期有效的预签名地址，浏览器会按附件下载。
     */
    String download(String objectKey);

    /**
     * 删除 OSS 文件。
     *
     * <p>这里只删除 OSS 对象，不清空业务表字段。
     * 清空数据库字段由作业、答案等具体业务服务负责。</p>
     *
     * @param objectKey 上传接口返回的 objectKey；兼容旧数据中的完整 OSS URL。
     */
    void delete(String objectKey);
}
