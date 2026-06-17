package cn.yanque.models.studentFront.biz;

import cn.yanque.common.api.PageResult;
import cn.yanque.models.homework.pojo.vo.req.HomeworkAssignmentPageReq;
import cn.yanque.models.homework.pojo.vo.res.HomeworkAssignmentRes;

/**
 * 学员端作业业务接口
 * 定义学员端作业相关的业务逻辑方法
 */
public interface StudentHomeworkBiz {

    /**
     * 获取作业列表
     * @param req 分页查询参数
     * @return 分页作业列表
     */
    PageResult<HomeworkAssignmentRes> list(HomeworkAssignmentPageReq req);

    /**
     * 获取作业详情
     * @param id 作业ID
     * @return 作业详情
     */
    HomeworkAssignmentRes detail(Long id);
}
