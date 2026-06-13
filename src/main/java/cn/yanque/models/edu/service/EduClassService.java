package cn.yanque.models.edu.service;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.pojo.vo.req.ClassCreateReq;
import cn.yanque.common.pojo.vo.req.ClassPageReq;
import cn.yanque.common.pojo.vo.req.ClassUpdateReq;
import cn.yanque.common.pojo.vo.req.ScheduleGenerateReq;
import cn.yanque.common.pojo.vo.req.SchedulePageReq;
import cn.yanque.common.pojo.vo.res.ClassCreateRes;
import cn.yanque.common.pojo.vo.res.ClassDeleteRes;
import cn.yanque.common.pojo.vo.res.ClassDetailRes;
import cn.yanque.common.pojo.vo.res.ClassPageRes;
import cn.yanque.common.pojo.vo.res.ClassUpdateRes;
import cn.yanque.common.pojo.vo.res.ScheduleGenerateRes;
import cn.yanque.common.pojo.vo.res.SchedulePageRes;

import java.util.Date;
import java.util.List;

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

    /**
     * 生成班级课表
     * @param classId 班级ID
     * @param req 生成参数（开班时间、授课老师）
     * @return 生成的课表记录数
     */
    ScheduleGenerateRes generateSchedule(Long classId, ScheduleGenerateReq req);

    /**
     * 分页查询班级课表
     * @param req 分页查询参数
     * @return 分页课表列表
     */
    PageResult<SchedulePageRes> pageSchedule(SchedulePageReq req);

    /**
     * 查询指定日期范围内已排课的老师ID
     * @param classId 当前班级ID（排除自身）
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 已排课的老师ID列表
     */
    List<Long> getBusyTeacherIds(Long classId, Date startDate, Date endDate);
}
