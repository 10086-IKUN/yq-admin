package cn.yanque.models.studentFront.biz;

import cn.yanque.models.studentFront.pojo.vo.req.StudentLoginReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentLoginRes;


/**
 * 学生端登录业务接口。
 *
 * <p>控制器通过该接口完成学生登录，返回学生身份、JWT、签名密钥和学生端权限。</p>
 */
public interface StudentFrontBiz {

    StudentLoginRes login(StudentLoginReq req);
}
