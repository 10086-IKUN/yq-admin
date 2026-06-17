package cn.yanque.models.homework.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.homework.mapper.HomeworkAssignmentMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAnswerPublishReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.homework.pojo.vo.res.HomeworkIdRes;
import cn.yanque.models.homework.service.HomeworkAssignmentService;
import cn.yanque.models.file.service.FileService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 作业发布服务实现。
 *
 * <p>这里负责维护 homework_assignment 表。
 * 文件本身由 FileService 操作 OSS，本类只负责保存或清空附件字段。</p>
 */
@Service
public class HomeworkAssignmentServiceImpl implements HomeworkAssignmentService {

    @Autowired
    private HomeworkAssignmentMapper homeworkAssignmentMapper;

    @Autowired
    private FileService fileService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes create(HomeworkAssignmentReq req, Long teacherId) {
        /*
         * 发布作业流程：
         * 1. 把前端表单转换成作业实体。
         * 2. 写入发布老师、发布时间。
         * 3. 新作业默认是已发布，答案默认未发布。
         * 4. 统计当前班级学员人数，保存为快照。
         *
         * 保存人数快照的原因：
         * 后续班级人数可能变化，但这次作业发布时应交人数应该保持稳定。
         */
        HomeworkAssignmentEntity entity = buildEntity(req);
        entity.setPublishTeacherId(teacherId);
        entity.setPublishDate(LocalDateTime.now());
        entity.setStatus("PUBLISHED");
        entity.setAnswerPublishStatus("UNPUBLISHED");
        entity.setAllowResubmit(Boolean.TRUE.equals(req.getAllowResubmit()));
        entity.setStudentCountSnapshot(homeworkAssignmentMapper.countStudentsByClassId(req.getClassId()));
        homeworkAssignmentMapper.insert(entity);
        return new HomeworkIdRes(entity.getId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes update(Long id, HomeworkAssignmentReq req) {
        /*
         * 编辑作业流程：
         * 1. 先确认作业存在。
         * 2. 把前端提交的新字段复制到实体。
         * 3. 再按 id 更新。
         *
         * 注意：
         * 前端已经做了“点确定才上传附件”的控制，
         * 所以后端收到的 attachmentUrl 就是最终要保存的附件地址。
         */
        ensureExists(id);
        HomeworkAssignmentEntity entity = buildEntity(req);
        entity.setId(id);
        entity.setAllowResubmit(Boolean.TRUE.equals(req.getAllowResubmit()));
        homeworkAssignmentMapper.updateById(entity);
        return new HomeworkIdRes(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes delete(Long id) {
        /*
         * 删除作业：
         * 这里调用 Mapper 的 deleteById。
         * 当前项目里的具体删除方式以 Mapper 实现为准，可能是逻辑删除，也可能是物理删除。
         */
        ensureExists(id);
        homeworkAssignmentMapper.deleteById(id);
        return new HomeworkIdRes(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes close(Long id, Long teacherId) {
        /*
         * 关闭作业：
         * 1. 确认作业存在。
         * 2. 把状态改为 CLOSED。
         * 3. 记录关闭老师和关闭时间。
         */
        ensureExists(id);
        homeworkAssignmentMapper.closeById(id, teacherId);
        return new HomeworkIdRes(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes publishAnswer(Long id, HomeworkAnswerPublishReq req, Long teacherId) {
        /*
         * 发布答案：
         * 只更新答案附件相关字段，不动作业标题、班级、课程等基础信息。
         *
         * 字段变化：
         * answer_attachment_name/url/type 保存答案附件。
         * answer_publish_status 改为 PUBLISHED。
         * answer_publish_time 记录发布时间。
         * answer_publish_teacher_id 记录发布老师。
         */
        ensureExists(id);
        HomeworkAssignmentEntity entity = new HomeworkAssignmentEntity();
        entity.setId(id);
        entity.setAnswerAttachmentName(req.getAnswerAttachmentName());
        entity.setAnswerAttachmentUrl(req.getAnswerAttachmentUrl());
        entity.setAnswerAttachmentType(req.getAnswerAttachmentType());
        entity.setAnswerPublishStatus("PUBLISHED");
        entity.setAnswerPublishTime(LocalDateTime.now());
        entity.setAnswerPublishTeacherId(teacherId);
        homeworkAssignmentMapper.publishAnswer(entity);
        return new HomeworkIdRes(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes deleteAttachment(Long id) {
        /*
         * 删除作业附件：
         * 1. 查询作业，拿到当前附件地址。
         * 2. 如果附件地址不为空，先删除 OSS 文件。
         * 3. 再清空数据库中的附件名称、地址、类型。
         *
         * 为什么这里可以直接删 OSS：
         * 前端只有在用户点击“确定”后才会调用这个接口。
         */
        HomeworkAssignmentEntity entity = ensureExists(id);
        if (!isBlank(entity.getAttachmentUrl())) {
            // 删除已有作业附件时，先删 OSS 文件，再清空数据库字段，避免页面继续显示失效附件。
            fileService.delete(entity.getAttachmentUrl());
        }
        homeworkAssignmentMapper.clearAttachment(id);
        return new HomeworkIdRes(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkIdRes deleteAnswerAttachment(Long id) {
        /*
         * 删除答案附件：
         * 1. 查询作业，拿到当前答案附件地址。
         * 2. 删除 OSS 文件。
         * 3. 清空答案附件字段。
         * 4. 把答案状态恢复为 UNPUBLISHED。
         *
         * 恢复未发布的原因：
         * 如果答案附件都没了，页面不应该继续显示“答案已发布”。
         */
        HomeworkAssignmentEntity entity = ensureExists(id);
        if (!isBlank(entity.getAnswerAttachmentUrl())) {
            // 答案附件删除后，同时把答案发布状态还原为未发布。
            fileService.delete(entity.getAnswerAttachmentUrl());
        }
        homeworkAssignmentMapper.clearAnswerAttachment(id);
        return new HomeworkIdRes(id);
    }

    @Override
    public HomeworkAssignmentRes detail(Long id) {
        // 详情接口复用存在性校验，不存在时直接抛 404。
        return toRes(ensureExists(id));
    }

    @Override
    public PageResult<HomeworkAssignmentRes> page(HomeworkAssignmentPageReq req) {
        /*
         * 分页查询：
         * PageHelper 只对紧跟着的第一条 SQL 生效。
         * 所以 startPage 必须紧挨着 selectPage，不要在中间插入其他查询。
         */
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<HomeworkAssignmentEntity> list = homeworkAssignmentMapper.selectPage(
                req.getKeyword(),
                req.getClassId(),
                req.getCourseId(),
                req.getStatus(),
                req.getAnswerPublishStatus()
        );
        PageInfo<HomeworkAssignmentEntity> pageInfo = new PageInfo<>(list);
        return new PageResult<>(
                pageInfo.getTotal(),
                req.getPageNum(),
                req.getPageSize(),
                list.stream().map(this::toRes).toList()
        );
    }

    private HomeworkAssignmentEntity ensureExists(Long id) {
        // 所有需要作业存在的方法都走这里，错误信息保持一致。
        HomeworkAssignmentEntity entity = homeworkAssignmentMapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(404, "作业不存在");
        }
        return entity;
    }

    private HomeworkAssignmentEntity buildEntity(HomeworkAssignmentReq req) {
        // 请求对象和实体字段基本一致，用 BeanUtils 减少手工赋值。
        HomeworkAssignmentEntity entity = new HomeworkAssignmentEntity();
        BeanUtils.copyProperties(req, entity);
        return entity;
    }

    private HomeworkAssignmentRes toRes(HomeworkAssignmentEntity entity) {
        // Entity 转响应对象，避免直接把数据库实体暴露给前端。
        HomeworkAssignmentRes res = new HomeworkAssignmentRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    private boolean isBlank(String value) {
        // 统一判断空字符串，避免到处写 trim 判空。
        return value == null || value.trim().isEmpty();
    }
}
