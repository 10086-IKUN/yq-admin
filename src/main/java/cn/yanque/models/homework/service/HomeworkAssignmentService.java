package cn.yanque.models.homework.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAnswerPublishReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.homework.pojo.vo.res.HomeworkIdRes;

/**
 * 管理端作业发布服务。
 *
 * <p>这个服务只处理“作业发布表 homework_assignment”的管理端能力：
 * 发布作业、编辑作业、关闭作业、发布答案、删除附件、分页查询。</p>
 */
public interface HomeworkAssignmentService {

    /**
     * 发布作业。
     *
     * @param req       作业表单数据，包含标题、班级、课程、附件等字段。
     * @param teacherId 当前登录老师 ID，用来记录发布人。
     * @return 新作业 ID。
     */
    HomeworkIdRes create(HomeworkAssignmentReq req, Long teacherId);

    /**
     * 编辑作业。
     *
     * @param id  作业 ID。
     * @param req 编辑后的作业表单数据。
     * @return 被编辑的作业 ID。
     */
    HomeworkIdRes update(Long id, HomeworkAssignmentReq req);

    /**
     * 删除作业。
     *
     * @param id 作业 ID。
     * @return 被删除的作业 ID。
     */
    HomeworkIdRes delete(Long id);

    /**
     * 关闭作业。
     *
     * @param id        作业 ID。
     * @param teacherId 当前登录老师 ID，用来记录关闭人。
     * @return 被关闭的作业 ID。
     */
    HomeworkIdRes close(Long id, Long teacherId);

    /**
     * 发布答案附件。
     *
     * @param id        作业 ID。
     * @param req       答案附件名称、地址、类型。
     * @param teacherId 当前登录老师 ID，用来记录答案发布人。
     * @return 被更新的作业 ID。
     */
    HomeworkIdRes publishAnswer(Long id, HomeworkAnswerPublishReq req, Long teacherId);

    /**
     * 删除作业附件。
     *
     * <p>会删除 OSS 文件，并清空作业附件字段。</p>
     *
     * @param id 作业 ID。
     * @return 被更新的作业 ID。
     */
    HomeworkIdRes deleteAttachment(Long id);

    /**
     * 删除答案附件。
     *
     * <p>会删除 OSS 文件、清空答案附件字段，并恢复答案未发布状态。</p>
     *
     * @param id 作业 ID。
     * @return 被更新的作业 ID。
     */
    HomeworkIdRes deleteAnswerAttachment(Long id);

    /**
     * 查询作业详情。
     *
     * @param id 作业 ID。
     * @return 作业详情。
     */
    HomeworkAssignmentRes detail(Long id);

    /**
     * 分页查询作业。
     *
     * @param req 查询条件和分页参数。
     * @return 分页结果。
     */
    PageResult<HomeworkAssignmentRes> page(HomeworkAssignmentPageReq req);
}
