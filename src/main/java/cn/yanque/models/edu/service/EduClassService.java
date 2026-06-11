package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;

public interface EduClassService {

    ClassCreateRes addClass(ClassCreateReq req);

    ClassUpdateRes updateClass(ClassUpdateReq req);

    ClassDeleteRes deleteClass(Long id);

    ClassDetailRes getClassById(Long id);

    PageResult<ClassPageRes> pageClass(ClassPageReq req);
}
