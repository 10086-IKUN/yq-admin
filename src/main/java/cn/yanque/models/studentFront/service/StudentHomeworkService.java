package cn.yanque.models.studentFront.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.entity.HomeworkAssignmentEntity;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;

/**
 * 学员端作业服务接口
 * 定义学员端作业相关的数据操作方法
 */
public interface StudentHomeworkService {

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req);

    /**
     * 根据ID获取作业
     * @param id 作业ID
     * @return 作业实体
     */
    HomeworkAssignmentEntity getById(Long id);
}
