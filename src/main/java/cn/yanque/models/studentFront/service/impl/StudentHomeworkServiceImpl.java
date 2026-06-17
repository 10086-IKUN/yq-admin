package cn.yanque.models.studentFront.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.mapper.HomeworkAssignmentMapper;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.studentFront.service.StudentHomeworkService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学员端作业服务实现类
 * 实现学员端作业相关的数据操作
 */
@Service
public class StudentHomeworkServiceImpl implements StudentHomeworkService {

    @Autowired
    private HomeworkAssignmentMapper homeworkAssignmentMapper;

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    @Override
    public PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<HomeworkAssignmentEntity> list = homeworkAssignmentMapper.selectPage(
                req.getKeyword(),
                req.getClassId(),
                req.getCourseId(),
                req.getStatus(),
                null  // answerPublishStatus
        );
        PageInfo<HomeworkAssignmentEntity> pageInfo = new PageInfo<>(list);

        List<HomeworkAssignmentRes> records = list.stream().map(entity -> {
            HomeworkAssignmentRes res = new HomeworkAssignmentRes();
            BeanUtils.copyProperties(entity, res);
            return res;
        }).toList();

        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
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
