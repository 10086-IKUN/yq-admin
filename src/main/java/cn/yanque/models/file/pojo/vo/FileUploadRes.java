package cn.yanque.models.file.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 文件上传结果。
 *
 * <p>注意：url 字段保存的是 OSS objectKey，不是永久公开访问地址。
 * 前端需要预览或下载时，应继续调用 /api/file/preview 或 /api/file/download 换取临时地址。</p>
 */
@Data
@Schema(description = "文件上传结果")
public class FileUploadRes {

    /** 原文件名，用于页面展示。 */
    @Schema(description = "原文件名")
    private String name;

    /** OSS objectKey，用于保存到业务表。 */
    @Schema(description = "OSS ObjectKey")
    private String url;

    /** 文件 MIME 类型。 */
    @Schema(description = "文件类型")
    private String type;

    /** 文件大小，单位：字节。 */
    @Schema(description = "文件大小")
    private Long size;

    /** 临时预览地址，上传完成后可立即预览。 */
    @Schema(description = "临时预览地址")
    private String previewUrl;
}
