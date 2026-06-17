package cn.yanque.models.studentFront.service;

import cn.yanque.models.studentFront.pojo.vo.res.StudentScoreRes;

import java.util.List;

/**
 * 学员端成绩服务接口
 * 定义学员端成绩相关的数据操作方法
 */
public interface StudentScoreService {

    /**
     * 获取成绩列表
     * @return 成绩列表
     */
    List<StudentScoreRes> list();
}
