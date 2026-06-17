package cn.yanque.models.studentFront.biz;

import cn.yanque.models.studentFront.pojo.vo.req.StudentLoginReq;
import cn.yanque.models.studentFront.pojo.vo.res.StudentLoginRes;

public interface StudentFrontBiz {

    StudentLoginRes login(StudentLoginReq req);
}
