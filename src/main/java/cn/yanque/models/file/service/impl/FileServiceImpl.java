package cn.yanque.models.file.service.impl;

import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.file.config.OssProperties;
import cn.yanque.models.file.pojo.vo.FileUploadRes;
import cn.yanque.models.file.service.FileService;
import cn.yanque.models.system.user.mapper.SysUserMapper;
import cn.yanque.models.system.user.pojo.entity.SysUserEntity;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.ObjectMetadata;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

/**
 * OSS 文件服务实现。
 *
 * <p>核心设计：
 * 1. 数据库存 OSS objectKey，不存临时预览地址。
 * 2. 前端每次预览或下载时，再向后端换取短期预签名 URL。
 * 3. 文件路径中带业务类型、日期、上传人姓名，方便 OSS 控制台排查。</p>
 */
@Service
public class FileServiceImpl implements FileService {

    // 这里控制允许上传的文件后缀。新增格式时，只改这个集合即可。
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(
            "pdf", "doc", "docx", "xls", "xlsx", "ppt", "pptx",
            "md", "json", "txt", "png", "jpg", "jpeg", "zip",
            "mp3", "wav", "m4a", "aac", "ogg", "flac", "mp4", "webm");

    private static final Set<String> AUDIO_EXTENSIONS = Set.of(
            "mp3", "wav", "m4a", "aac", "ogg", "flac", "mp4", "webm");

    @Autowired
    private OssProperties ossProperties;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Override
    public FileUploadRes upload(MultipartFile file, String bizType, HttpServletRequest request) {
        /*
         * 上传入口流程：
         * 1. 校验文件不能为空。
         * 2. 校验 OSS 配置是否完整。
         * 3. 校验文件大小和后缀。
         * 4. 根据业务类型、上传人、日期生成 objectKey。
         * 5. 上传到 OSS，最后把 objectKey 返回给前端保存到业务表。
         */
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择上传文件");
        }
        validateOssConfig();
        validateFile(file);

        // objectKey 是 OSS 内部文件路径。数据库只存这个值，预览和下载时再生成临时访问地址。
        String originalName = sanitizeFileName(file.getOriginalFilename());
        String objectKey = buildObjectKey(bizType, resolveUploaderName(request), originalName);
        OSS ossClient = buildClient();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(resolveContentType(originalName, file.getContentType()));
            ossClient.putObject(ossProperties.getBucket(), objectKey, file.getInputStream(), metadata);
        } catch (IOException e) {
            throw new BusinessException(500, "读取上传文件失败");
        } finally {
            ossClient.shutdown();
        }

        FileUploadRes res = new FileUploadRes();
        res.setName(originalName);
        res.setUrl(objectKey);
        res.setType(file.getContentType());
        res.setSize(file.getSize());
        res.setPreviewUrl(preview(objectKey));
        return res;
    }

    @Override
    public String preview(String objectKey) {
        // 预览和下载都不直接返回永久地址，而是返回短期有效的签名地址。
        return generateSignedUrl(objectKey, false);
    }

    @Override
    public String download(String objectKey) {
        // download=true 会让浏览器按附件下载，不在页面里直接打开。
        return generateSignedUrl(objectKey, true);
    }

    @Override
    public void delete(String objectKey) {
        /*
         * 删除这里只做 OSS 文件删除。
         * 例如作业附件删除时，清空 homework_assignment.attachment_url
         * 是 HomeworkAssignmentServiceImpl 的职责。
         */
        if (isBlank(objectKey)) {
            throw new BusinessException(400, "文件地址不能为空");
        }
        validateOssConfig();
        // 兼容旧数据：旧记录可能存的是完整 URL，新记录存的是 objectKey。
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        OSS ossClient = buildClient();
        try {
            ossClient.deleteObject(ossProperties.getBucket(), normalizedObjectKey);
        } finally {
            ossClient.shutdown();
        }
    }

    private OSS buildClient() {
        // 每次操作单独创建 OSS 客户端，用完后在 finally 中关闭。
        return new OSSClientBuilder().build(
                ossProperties.getEndpoint(),
                ossProperties.getAccessKeyId(),
                ossProperties.getAccessKeySecret());
    }

    private void validateOssConfig() {
        // AK/SK 从环境变量读取，如果没配好，这里给出明确错误。
        if (isBlank(ossProperties.getEndpoint())
                || isBlank(ossProperties.getBucket())
                || isBlank(ossProperties.getAccessKeyId())
                || isBlank(ossProperties.getAccessKeySecret())) {
            throw new BusinessException(500, "OSS配置不完整");
        }
    }

    private void validateFile(MultipartFile file) {
        /*
         * 文件安全校验：
         * 前端限制只能提升体验，不能作为安全边界。
         * 所以后端必须再次校验大小和后缀。
         */
        String extension = getExtension(file.getOriginalFilename());
        long maxMb = AUDIO_EXTENSIONS.contains(extension)
                ? ossProperties.getMaxAudioFileSizeMb() : ossProperties.getMaxFileSizeMb();
        long maxBytes = maxMb * 1024L * 1024L;
        if (file.getSize() > maxBytes) {
            throw new BusinessException(400, "文件大小不能超过" + maxMb + "MB");
        }
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessException(400, "不支持的文件格式");
        }
    }

    private String buildObjectKey(String bizType, String uploaderName, String originalName) {
        /*
         * objectKey 是 OSS 中的文件路径，不是访问 URL。
         *
         * 示例：
         * homework/assignment/2026/06/17/张三/uuid-作业说明.md
         *
         * 这样做的好处：
         * 1. 能按业务类型区分文件。
         * 2. 能按日期快速定位。
         * 3. 能看到上传人，方便排查是谁上传的。
         */
        String safeBizType = sanitizePathPart(isBlank(bizType) ? "common" : bizType).toLowerCase(Locale.ROOT);
        String safeUploaderName = sanitizePathPart(isBlank(uploaderName) ? "unknown" : uploaderName);
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        // 路径包含业务类型、日期、上传人姓名，方便以后在 OSS 控制台里排查文件来源。
        return String.join("/",
                sanitizePathPart(ossProperties.getDirPrefix()),
                safeBizType,
                datePath,
                safeUploaderName,
                UUID.randomUUID() + "-" + originalName);
    }

    private String resolveUploaderName(HttpServletRequest request) {
        /*
         * 上传人姓名来源：
         * 1. 学员端请求会带 studentId，优先取学员姓名。
         * 2. 管理端请求会带 userId，优先取老师真实姓名。
         * 3. 真实姓名为空时，再用昵称；昵称也为空时用用户名。
         */
        Object studentId = request.getAttribute("studentId");
        if (studentId != null) {
            EduStudentEntity student = eduStudentMapper.selectById(Long.parseLong(String.valueOf(studentId)));
            return student == null ? null : student.getStudentName();
        }

        Object userId = request.getAttribute("userId");
        if (userId != null) {
            SysUserEntity user = sysUserMapper.selectById(Long.parseLong(String.valueOf(userId)));
            if (user == null) {
                return null;
            }
            if (!isBlank(user.getRealName())) {
                return user.getRealName();
            }
            if (!isBlank(user.getNickname())) {
                return user.getNickname();
            }
            return user.getUsername();
        }
        return null;
    }

    private String generateSignedUrl(String objectKey, boolean download) {
        /*
         * 生成预签名地址：
         * 1. 前端传来的可能是 objectKey，也可能是旧数据里的完整 URL。
         * 2. 先统一转成 objectKey。
         * 3. 再按 previewExpireMinutes 生成短期有效访问地址。
         * 4. preview 使用 inline，download 使用 attachment。
         */
        if (isBlank(objectKey)) {
            throw new BusinessException(400, "文件地址不能为空");
        }
        validateOssConfig();
        String normalizedObjectKey = normalizeObjectKey(objectKey);

        OSS ossClient = buildClient();
        try {
            Date expiration = new Date(System.currentTimeMillis() + ossProperties.getPreviewExpireMinutes() * 60L * 1000L);
            // Bucket 是公开读也可以用预签名。这样前端统一走后端拿地址，后面改私有 Bucket 不用改页面。
            GeneratePresignedUrlRequest request =
                    new GeneratePresignedUrlRequest(ossProperties.getBucket(), normalizedObjectKey, HttpMethod.GET);
            request.setExpiration(expiration);
            // 预览要强制 inline，避免 OSS 对象历史元数据是 attachment 时浏览器直接下载。
            // 下载按钮才使用 attachment。
            request.addQueryParameter("response-content-disposition", download ? "attachment" : "inline");
            URL url = ossClient.generatePresignedUrl(request);
            return url.toString();
        } finally {
            ossClient.shutdown();
        }
    }

    private String normalizeObjectKey(String value) {
        /*
         * 兼容历史数据：
         * 新数据只存 objectKey，例如 homework/assignment/xxx.md。
         * 旧数据可能存完整 URL，例如 https://bucket.endpoint/homework/assignment/xxx.md。
         * OSS SDK 删除和签名都需要 objectKey，所以完整 URL 要截取 path 部分。
         */
        String objectKey = value.trim();
        if (!objectKey.startsWith("http://") && !objectKey.startsWith("https://")) {
            return objectKey;
        }
        try {
            // 旧数据如果存了完整 OSS URL，只取 path 部分作为 objectKey。
            String path = URI.create(objectKey).getPath();
            return path != null && path.startsWith("/") ? path.substring(1) : path;
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "文件地址格式不正确");
        }
    }

    private String sanitizeFileName(String fileName) {
        // 文件名不能带路径分隔符，否则可能影响 OSS 路径结构。
        String safeName = fileName == null ? "file" : fileName.trim();
        safeName = safeName.replaceAll("[\\\\/:*?\"<>|]", "_");
        return safeName.isBlank() ? "file" : safeName;
    }

    private String sanitizePathPart(String value) {
        // 路径片段会参与 OSS 目录拼接，需要替换掉特殊字符。
        String safeValue = value == null ? "unknown" : value.trim();
        safeValue = safeValue.replaceAll("[\\\\/:*?\"<>|]", "_");
        safeValue = safeValue.replaceAll("\\s+", "_");
        return safeValue.isBlank() ? "unknown" : safeValue;
    }

    private String getExtension(String fileName) {
        // 统一小写后缀，方便和 ALLOWED_EXTENSIONS 比较。
        if (fileName == null || !fileName.contains(".")) {
            return "";
        }
        return fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
    }

    private String resolveContentType(String fileName, String originalContentType) {
        // Markdown、文本、图片等类型明确写入 OSS，浏览器预览时才更稳定。
        String ext = getExtension(fileName);
        return switch (ext) {
            case "md" -> "text/markdown; charset=utf-8";
            case "txt" -> "text/plain; charset=utf-8";
            case "html", "htm" -> "text/html; charset=utf-8";
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "application/javascript; charset=utf-8";
            case "json" -> "application/json; charset=utf-8";
            case "xml" -> "application/xml; charset=utf-8";
            case "pdf" -> "application/pdf";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "mp3" -> "audio/mpeg";
            case "wav" -> "audio/wav";
            case "m4a", "aac", "mp4" -> "audio/mp4";
            case "ogg" -> "audio/ogg";
            case "flac" -> "audio/flac";
            case "webm" -> "audio/webm";
            case "gif" -> "image/gif";
            case "svg" -> "image/svg+xml";
            default -> originalContentType != null ? originalContentType : "application/octet-stream";
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}
