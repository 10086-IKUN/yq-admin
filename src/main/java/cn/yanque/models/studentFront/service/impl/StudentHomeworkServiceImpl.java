package cn.yanque.models.studentFront.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.mapper.HomeworkAssignmentMapper;
import cn.yanque.models.homework.mapper.HomeworkSubmissionMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.entity.HomeworkSubmissionEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.studentFront.service.StudentHomeworkService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 学员端作业服务实现类
 * 实现学员端作业相关的数据操作
 */
@Service
public class StudentHomeworkServiceImpl implements StudentHomeworkService {

    @Autowired
    private HomeworkAssignmentMapper homeworkAssignmentMapper;

    @Autowired
    private HomeworkSubmissionMapper homeworkSubmissionMapper;

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    @Override
    public PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req, String studentNo) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<HomeworkAssignmentEntity> list = homeworkAssignmentMapper.selectPage(
                req.getKeyword(),
                req.getClassId(),
                req.getCourseId(),
                req.getStatus(),
                null  // answerPublishStatus
        );
        PageInfo<HomeworkAssignmentEntity> pageInfo = new PageInfo<>(list);

        // 学员端列表需要显示“待提交 / 已提交 / 已批改”等状态。
        // 这里一次性查询当前学员的所有提交记录，再按 assignmentId 做映射，避免列表逐条查询数据库。
        Map<Long, HomeworkSubmissionEntity> submissionMap = homeworkSubmissionMapper.selectByStudentNo(studentNo)
                .stream()
                .collect(Collectors.toMap(
                        HomeworkSubmissionEntity::getAssignmentId,
                        Function.identity(),
                        (first, second) -> first
                ));

        List<HomeworkAssignmentRes> records = list.stream().map(entity -> {
            HomeworkAssignmentRes res = new HomeworkAssignmentRes();
            BeanUtils.copyProperties(entity, res);
            fillSubmissionStatus(res, submissionMap.get(entity.getId()));
            return res;
        }).toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 给作业响应对象补充当前学员的提交状态。
     *
     * <p>作业表只知道作业是否发布或关闭，不知道某个学员是否提交。
     * 所以这里把 homework_submission 中属于当前学员的记录合并进返回值，
     * 首页和作业列表就可以直接展示“已提交”“已批改”“需重交”。</p>
     *
     * @param res 作业响应对象
     * @param submission 当前学员对这份作业的提交记录，未提交时为空
     */
    private void fillSubmissionStatus(HomeworkAssignmentRes res, HomeworkSubmissionEntity submission) {
        if (submission == null) {
            res.setHasSubmitted(false);
            return;
        }

        res.setHasSubmitted(true);
        res.setSubmissionStatus(submission.getStatus());
        res.setSubmitTime(submission.getSubmitTime());
        res.setScore(submission.getScore());
        res.setReviewTime(submission.getReviewTime());
    }

    /**
     * 根据ID获取作业
     * @param id 作业ID
     * @return 作业实体
     */
    @Override
    public HomeworkAssignmentEntity getById(Long id) {
        return homeworkAssignmentMapper.selectById(id);
    }
}
