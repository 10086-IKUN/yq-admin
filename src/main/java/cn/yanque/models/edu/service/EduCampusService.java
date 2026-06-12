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

/**
 * 校区服务接口
 * 定义校区管理的业务逻辑方法
 */
public interface EduCampusService {

    /**
     * 添加校区
     * @param req 创建校区请求参数
     * @return 创建成功的校区信息
     */
    CampusCreateRes addCampus(CampusCreateReq req);

    /**
     * 修改校区
     * @param req 更新校区请求参数
     * @return 更新后的校区信息
     */
    CampusUpdateRes updateCampus(CampusUpdateReq req);

    /**
     * 删除校区
     * @param id 校区ID
     * @return 删除结果
     */
    CampusDeleteRes deleteCampus(Long id);

    /**
     * 根据ID查询校区
     * @param id 校区ID
     * @return 校区详细信息
     */
    CampusDetailRes getCampusById(Long id);

    /**
     * 分页查询校区
     * @param req 分页查询参数
     * @return 分页校区列表
     */
    PageResult<CampusPageRes> pageCampus(CampusPageReq req);
}
