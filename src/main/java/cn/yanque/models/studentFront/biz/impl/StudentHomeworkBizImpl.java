package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;
import cn.yanque.models.studentFront.biz.StudentHomeworkBiz;
import cn.yanque.models.studentFront.service.StudentHomeworkService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学员端作业业务实现类
 * 实现学员端作业相关的业务逻辑
 */
@Component
public class StudentHomeworkBizImpl implements StudentHomeworkBiz {

    @Autowired
    private StudentHomeworkService studentHomeworkService;

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    @Override
    public PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req) {
        return studentHomeworkService.list(req);
    }

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业详情
     */
    @Override
    public HomeworkAssignmentRes detail(Long id) {
        HomeworkAssignmentEntity entity = studentHomeworkService.getById(id);
        if (entity == null) {
            throw new BusinessException(404, "作业不存在");
        }
        return convertToRes(entity);
    }

    /**
     * 实体转响应对象
     * @param entity 作业实体
     * @return 作业响应对象
     */
    private HomeworkAssignmentRes convertToRes(HomeworkAssignmentEntity entity) {
        HomeworkAssignmentRes res = new HomeworkAssignmentRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
