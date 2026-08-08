package cn.yanque.models.edu.student.resume.service.impl;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.edu.student.resume.pojo.StudentResumeDtos;
import cn.yanque.models.edu.student.resume.service.StudentResumeService;
import cn.yanque.models.file.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
@Slf4j
public class StudentResumeServiceImpl implements StudentResumeService {
    private static final ConcurrentMap<Long, Boolean> RUNNING_STUDENTS = new ConcurrentHashMap<>();
    private static final int DOWNLOAD_TIMEOUT_MILLIS = 60_000;

    @Autowired
    private EduStudentMapper studentMapper;

    @Autowired
    private FileService fileService;

    @Override
    public StudentResumeDtos.Info get(Long studentId) {
        EduStudentEntity student = requireStudent(studentId);
        String status = student.getResumeParseStatus();
        if (("PENDING".equals(status) || "PROCESSING".equals(status))
                && !RUNNING_STUDENTS.containsKey(studentId)) {
            // In-memory tasks are lost on restart. Requeue the unfinished record
            // when the frontend asks for its status.
            if ("PROCESSING".equals(status)) {
                studentMapper.markResumeParsePending(studentId);
            }
            submitParse(studentId);
            student = requireStudent(studentId);
        }
        return toInfo(student);
    }

    @Override
    public StudentResumeDtos.Info save(Long studentId, StudentResumeDtos.SaveReq req) {
        requireStudent(studentId);
        String objectKey = required(req.getObjectKey(), "简历文件不能为空");
        String fileName = required(req.getFileName(), "简历文件名不能为空");
        validateResumeFile(objectKey, fileName);
        if (studentMapper.updateResume(studentId, objectKey, fileName, req.getFileSize()) <= 0) {
            throw new BusinessException(404, "学员不存在");
        }
        submitParse(studentId);
        return toInfo(requireStudent(studentId));
    }

    @Override
    public StudentResumeDtos.Info retry(Long studentId) {
        EduStudentEntity student = requireResume(studentId);
        if ("PROCESSING".equals(student.getResumeParseStatus())
                && RUNNING_STUDENTS.containsKey(studentId)) {
            throw new BusinessException(400, "简历正在解析中，请稍后刷新");
        }
        studentMapper.markResumeParsePending(studentId);
        submitParse(studentId);
        return toInfo(requireStudent(studentId));
    }

    @Override
    public String previewUrl(Long studentId) {
        return fileService.preview(requireResume(studentId).getResumeObjectKey());
    }

    @Override
    public String downloadUrl(Long studentId) {
        return fileService.download(requireResume(studentId).getResumeObjectKey());
    }

    private void submitParse(Long studentId) {
        if (RUNNING_STUDENTS.putIfAbsent(studentId, Boolean.TRUE) != null) {
            return;
        }
        CompletableFuture.runAsync(() -> {
            try {
                parse(studentId);
            } catch (Throwable ex) {
                log.warn("resume parse failed, studentId={}", studentId, ex);
            } finally {
                RUNNING_STUDENTS.remove(studentId);
                try {
                    EduStudentEntity latest = studentMapper.selectById(studentId);
                    if (latest != null && "PENDING".equals(latest.getResumeParseStatus())) {
                        submitParse(studentId);
                    }
                } catch (Throwable ex) {
                    log.error("failed to inspect resume parse status, studentId={}", studentId, ex);
                }
            }
        });
    }

    private void parse(Long studentId) throws Exception {
        EduStudentEntity student = requireResume(studentId);
        String objectKey = student.getResumeObjectKey();
        if (studentMapper.markResumeParseProcessing(studentId, objectKey, new Date()) <= 0) {
            return;
        }
        try {
            String signedUrl = fileService.download(objectKey);
            String text;
            try (HttpResponse response = HttpRequest.get(signedUrl)
                    .setConnectionTimeout(10_000)
                    .setReadTimeout(DOWNLOAD_TIMEOUT_MILLIS)
                    .execute()) {
                if (response == null || !response.isOk()) {
                    throw new IllegalStateException("无法从文件存储下载简历，请检查文件是否仍然存在");
                }
                try (InputStream stream = response.bodyStream()) {
                    text = parseDocument(stream, student.getResumeFileName());
                }
            }
            String normalized = normalizeText(text);
            if (normalized == null || normalized.isBlank()) {
                throw new IllegalStateException("未识别到可用文字；如果是扫描版或图片版 PDF，请上传可复制文字的 PDF、DOC 或 DOCX 文件");
            }
            studentMapper.markResumeParseSuccess(studentId, objectKey, normalized, new Date());
            log.info("resume parse success, studentId={}", studentId);
        } catch (Throwable ex) {
            try {
                studentMapper.markResumeParseFailed(studentId, objectKey, friendlyError(ex), new Date());
            } catch (Throwable statusEx) {
                ex.addSuppressed(statusEx);
            }
            if (ex instanceof Exception exception) {
                throw exception;
            }
            if (ex instanceof Error error) {
                throw error;
            }
            throw new RuntimeException(ex);
        }
    }

    private String parseDocument(InputStream stream, String fileName) throws Exception {
        String lower = fileName == null ? "" : fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            try (PDDocument document = PDDocument.load(stream)) {
                return new PDFTextStripper().getText(document);
            }
        }
        if (lower.endsWith(".docx")) {
            try (XWPFDocument document = new XWPFDocument(stream)) {
                StringBuilder result = new StringBuilder();
                document.getParagraphs().forEach(paragraph -> appendLine(result, paragraph.getText()));
                document.getTables().forEach(table -> table.getRows().forEach(row ->
                        row.getTableCells().forEach(cell -> appendLine(result, cell.getText()))));
                return result.toString();
            }
        }
        if (lower.endsWith(".doc")) {
            try (HWPFDocument document = new HWPFDocument(stream)) {
                return document.getDocumentText();
            }
        }
        throw new IllegalArgumentException("仅支持 PDF、DOC、DOCX 格式的简历");
    }

    private void appendLine(StringBuilder result, String value) {
        if (value != null && !value.isBlank()) {
            result.append(value.trim()).append('\n');
        }
    }

    private String normalizeText(String value) {
        if (value == null) {
            return null;
        }
        return value.replace('\u00A0', ' ')
                .replaceAll("[ \\t\\x0B\\f\\r]+", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }

    private EduStudentEntity requireStudent(Long studentId) {
        EduStudentEntity student = studentId == null ? null : studentMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(404, "学员不存在");
        }
        return student;
    }

    private EduStudentEntity requireResume(Long studentId) {
        EduStudentEntity student = requireStudent(studentId);
        if (student.getResumeObjectKey() == null || student.getResumeObjectKey().isBlank()) {
            throw new BusinessException(404, "尚未上传简历");
        }
        return student;
    }

    private StudentResumeDtos.Info toInfo(EduStudentEntity student) {
        StudentResumeDtos.Info info = new StudentResumeDtos.Info();
        info.setStudentId(student.getId());
        info.setStudentName(student.getStudentName());
        info.setObjectKey(student.getResumeObjectKey());
        info.setFileName(student.getResumeFileName());
        info.setFileSize(student.getResumeFileSize());
        info.setUploadedAt(student.getResumeUploadedAt());
        info.setParseStatus(student.getResumeParseStatus());
        info.setParseErrorMessage(student.getResumeParseErrorMessage());
        info.setParsedAt(student.getResumeParsedAt());
        info.setResumeText(student.getResumeText());
        return info;
    }

    private void validateResumeFile(String objectKey, String fileName) {
        String normalizedKey = objectKey.replace('\\', '/').toLowerCase(Locale.ROOT);
        String normalizedName = fileName.toLowerCase(Locale.ROOT);
        boolean supported = normalizedName.endsWith(".pdf") || normalizedName.endsWith(".doc") || normalizedName.endsWith(".docx");
        if (objectKey.contains("..") || !normalizedKey.contains("/resume/") || !supported) {
            throw new BusinessException(400, "简历文件路径或格式不合法，仅支持 PDF、DOC、DOCX");
        }
    }

    private String required(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new BusinessException(400, message);
        }
        return value.trim();
    }

    private String friendlyError(Throwable ex) {
        String raw = ex.getMessage() == null ? "" : ex.getMessage().trim();
        String lower = raw.toLowerCase(Locale.ROOT);
        String result;
        if (lower.contains("password") || lower.contains("encrypted")) {
            result = "简历文件已加密或设置了打开密码，请移除密码后重新上传";
        } else if (lower.contains("header") || lower.contains("corrupt") || lower.contains("invalid") || lower.contains("format")) {
            result = "简历文件格式异常或文件已损坏，请确认文件能够正常打开后重新上传";
        } else if (lower.contains("download") || lower.contains("文件存储") || lower.contains("403") || lower.contains("404")) {
            result = "无法读取已上传的简历文件，文件可能不存在或访问地址已失效，请重新上传";
        } else if (lower.contains("timeout") || lower.contains("timed out")) {
            result = "简历下载或解析超时，请稍后点击重新解析";
        } else if (raw.isBlank()) {
            result = "简历解析失败，但解析器没有返回具体原因，请重新上传或联系管理员";
        } else {
            result = raw;
        }
        return result.substring(0, Math.min(result.length(), 1000));
    }
}
