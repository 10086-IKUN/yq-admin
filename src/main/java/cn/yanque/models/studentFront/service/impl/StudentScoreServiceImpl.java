package cn.yanque.models.studentFront.service.impl;

import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import cn.yanque.models.studentFront.service.StudentScoreService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学员端成绩服务实现类
 * 实现学员端成绩相关的数据操作
 */
@Service
public class StudentScoreServiceImpl implements StudentScoreService {

    /**
     * 获取成绩列表
     * @return 成绩列表
     */
    @Override
    public List<StudentScoreRes> list() {
        // TODO: 根据学员ID查询成绩
        // 暂时返回空列表，需要根据实际业务逻辑实现
        return List.of();
    }
}
