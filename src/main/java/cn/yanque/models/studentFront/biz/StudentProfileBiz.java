package cn.yanque.models.studentFront.biz;

import cn.yanque.models.studentFront.pojo.vo.req.StudentProfileUpdateReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentProfileRes;

/**
 * 学员端个人中心业务接口
 * 定义学员端个人中心相关的业务逻辑方法
 */
public interface StudentProfileBiz {

    /**
     * 获取个人信息
     * @return 学员信息
     */
    StudentProfileRes getProfile(Long studentId);

    /**
     * 修改个人信息
     * @param studentId 学员id
     * @param req 学员端个人资料修改请求
     */
    void updateProfile(Long studentId, StudentProfileUpdateReq req);
}
