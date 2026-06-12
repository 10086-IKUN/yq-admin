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

/**
 * 班级服务接口
 * 定义班级管理的业务逻辑方法
 */
public interface EduClassService {

    /**
     * 添加班级
     * @param req 创建班级请求参数
     * @return 创建成功的班级信息
     */
    ClassCreateRes addClass(ClassCreateReq req);

    /**
     * 修改班级
     * @param req 更新班级请求参数
     * @return 更新后的班级信息
     */
    ClassUpdateRes updateClass(ClassUpdateReq req);

    /**
     * 删除班级
     * @param id 班级ID
     * @return 删除结果
     */
    ClassDeleteRes deleteClass(Long id);

    /**
     * 根据ID查询班级
     * @param id 班级ID
     * @return 班级详细信息
     */
    ClassDetailRes getClassById(Long id);

    /**
     * 分页查询班级
     * @param req 分页查询参数
     * @return 分页班级列表
     */
    PageResult<ClassPageRes> pageClass(ClassPageReq req);
}
