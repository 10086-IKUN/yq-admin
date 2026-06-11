package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.CampusCreateReq;
import cn.yanque.common.pojo.vo.req.CampusPageReq;
import cn.yanque.common.pojo.vo.req.CampusUpdateReq;
import cn.yanque.common.pojo.vo.res.CampusCreateRes;
import cn.yanque.common.pojo.vo.res.CampusDeleteRes;
import cn.yanque.common.pojo.vo.res.CampusDetailRes;
import cn.yanque.common.pojo.vo.res.CampusPageRes;
import cn.yanque.common.pojo.vo.res.CampusUpdateRes;

public interface EduCampusService {

    CampusCreateRes addCampus(CampusCreateReq req);

    CampusUpdateRes updateCampus(CampusUpdateReq req);

    CampusDeleteRes deleteCampus(Long id);

    CampusDetailRes getCampusById(Long id);

    PageResult<CampusPageRes> pageCampus(CampusPageReq req);
}
