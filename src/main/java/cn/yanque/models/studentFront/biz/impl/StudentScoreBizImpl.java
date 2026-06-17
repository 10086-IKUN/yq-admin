package cn.yanque.models.studentFront.biz.impl;

import cn.yanque.models.studentFront.biz.StudentScoreBiz;
import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;
import cn.yanque.models.studentFront.service.StudentScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 学员端成绩业务实现类
 * 实现学员端成绩相关的业务逻辑
 */
@Component
public class StudentScoreBizImpl implements StudentScoreBiz {

    @Autowired
    private StudentScoreService studentScoreService;

    /**
     * 获取成绩列表
     * @return 成绩列表
     */
    @Override
    public List<StudentScoreRes> list() {
        return studentScoreService.list();
    }
}
