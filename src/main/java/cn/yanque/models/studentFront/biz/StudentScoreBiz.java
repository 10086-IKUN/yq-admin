package cn.yanque.models.studentFront.biz;

import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;

import java.util.List;

/**
 * 学员端成绩业务接口
 * 定义学员端成绩相关的业务逻辑方法
 */
public interface StudentScoreBiz {

    /**
     * 获取成绩列表
     * @return 成绩列表
     */
    List<StudentScoreRes> list();
}
