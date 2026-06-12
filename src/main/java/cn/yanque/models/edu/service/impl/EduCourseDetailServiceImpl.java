package cn.yanque.models.edu.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.yanque.common.api.PageResult;
import cn.yanque.common.enums.StageNameEnum;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.pojo.entity.EduCourseDetailEntity;
import cn.yanque.common.pojo.entity.EduCourseEntity;
import cn.yanque.common.pojo.info.CourseDetailInfo;
import cn.yanque.common.pojo.vo.req.CourseDetailCreateReq;
import cn.yanque.common.pojo.vo.req.CourseDetailPageReq;
import cn.yanque.common.pojo.vo.req.CourseDetailUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseDetailCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDetailRes;
import cn.yanque.common.pojo.vo.res.CourseDetailPageRes;
import cn.yanque.common.pojo.vo.res.CourseDetailUpdateRes;
import cn.yanque.models.edu.mapper.EduCourseDetailMapper;
import cn.yanque.models.edu.mapper.EduCourseMapper;
import cn.yanque.models.edu.service.EduCourseDetailService;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * 课程详情Service实现类
 *
 * 核心设计模式：
 * 1. @Service: 标记为Spring Bean，自动注入到Controller
 * 2. @Transactional: 事务管理，写操作失败时自动回滚
 * 3. Entity -> VO转换: 使用BeanUtils.copyProperties简化对象拷贝
 * 4. 分页查询: 使用PageHelper实现物理分页
 *
 * 数据流向:
 * Controller(接收请求) -> Service(业务逻辑) -> Mapper(数据库操作)
 * Controller(返回响应) <- Service(转换VO)   <- Mapper(返回Entity)
 */
@Service
public class EduCourseDetailServiceImpl implements EduCourseDetailService {

    /** 自动注入Mapper，Spring会自动生成代理实现 */
    @Autowired
    private EduCourseDetailMapper eduCourseDetailMapper;

    @Autowired
    private EduCourseMapper eduCourseMapper;

    /**
     * 新增课程详情
     *
     * 执行流程：
     * 1. 校验该课程中天数是否已存在（整个课程天数必须唯一）
     * 2. 创建Entity对象并设置字段值
     * 3. 调用Mapper插入数据库
     * 4. 封装响应对象返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailCreateRes addCourseDetail(CourseDetailCreateReq req) {
        // 1. 校验天数是否已存在（整个课程中天数必须唯一）
        int count = eduCourseDetailMapper.countByCourseIdAndDayNum(req.getCourseId(), req.getDayNum(), null);
        if (count > 0) {
            throw BusinessException.DataError.newInstance("该课程中天数" + req.getDayNum() + "已存在");
        }

        // 2. 创建Entity并插入
        EduCourseDetailEntity entity = new EduCourseDetailEntity();
        entity.setCourseId(req.getCourseId());
        entity.setStageName(req.getStageName());
        entity.setDayNum(req.getDayNum());
        entity.setCourseContent(req.getCourseContent());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        eduCourseDetailMapper.insert(entity);

        CourseDetailCreateRes res = new CourseDetailCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 更新课程详情
     *
     * 执行流程：
     * 1. 查询记录是否存在
     * 2. 校验天数是否已存在（排除自身）
     * 3. 更新字段值并执行更新
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailUpdateRes updateCourseDetail(CourseDetailUpdateReq req) {
        // 1. 查询记录是否存在
        EduCourseDetailEntity entity = eduCourseDetailMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.CourseDetailNotExist;
        }

        // 2. 校验天数是否已存在（排除自身）
        int count = eduCourseDetailMapper.countByCourseIdAndDayNum(req.getCourseId(), req.getDayNum(), req.getId());
        if (count > 0) {
            throw BusinessException.DataError.newInstance("该课程中天数" + req.getDayNum() + "已存在");
        }

        // 3. 更新字段值
        entity.setCourseId(req.getCourseId());
        entity.setStageName(req.getStageName());
        entity.setDayNum(req.getDayNum());
        entity.setCourseContent(req.getCourseContent());
        entity.setUpdatedAt(new Date());

        int rows = eduCourseDetailMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.CourseDetailNotExist;
        }

        CourseDetailUpdateRes res = new CourseDetailUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除课程详情
     *
     * 执行流程：
     * 1. 先查询记录是否存在
     * 2. 存在则执行删除
     * 3. 检查删除结果
     *
     * 注意：删除操作也要加@Transactional，虽然这里只有一条SQL
     * 但保持一致性，便于后期扩展（如删除时同步其他数据）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailDeleteRes deleteCourseDetail(Long id) {
        // 1. 查询记录是否存在
        EduCourseDetailEntity entity = eduCourseDetailMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CourseDetailNotExist;
        }

        // 2. 执行删除
        int rows = eduCourseDetailMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.CourseDetailNotExist;
        }

        CourseDetailDeleteRes res = new CourseDetailDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询课程详情
     *
     * 注意：查询操作不需要@Transactional（只读事务可以不加）
     * 除非需要保证查询的一致性（如先查再改的场景）
     */
    @Override
    public CourseDetailDetailRes getCourseDetailById(Long id) {
        EduCourseDetailEntity entity = eduCourseDetailMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.CourseDetailNotExist;
        }
        return buildCourseDetailDetailRes(entity);  // Entity转换为VO
    }

    /**
     * 分页查询课程详情
     *
     * 分页原理（PageHelper）：
     * 1. PageHelper.startPage()设置分页参数
     * 2. 下一条SQL查询会自动添加LIMIT子句
     * 3. PageInfo封装分页结果（总条数、当前页数据等）
     *
     * 执行流程：
     * 1. 设置分页参数
     * 2. 执行查询（自动分页）
     * 3. 将Entity列表转换为VO列表
     * 4. 封装分页结果返回
     */
    @Override
    public PageResult<CourseDetailPageRes> pageCourseDetail(CourseDetailPageReq req) {
        // 1. 设置分页参数，必须在查询之前调用
        PageHelper.startPage(req.getPageNum(), req.getPageSize());

        // 2. 执行查询，返回的是分页后的数据
        List<EduCourseDetailEntity> list = eduCourseDetailMapper.selectByCourseId(req.getCourseId());
        PageInfo<EduCourseDetailEntity> pageInfo = new PageInfo<>(list);

        // 3. Entity列表转VO列表
        List<CourseDetailPageRes> records = list.stream()
                .map(this::buildCourseDetailPageRes)
                .toList();

        // 4. 封装分页结果
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 导入Excel课程详情
     *
     * 执行流程：
     * 1. 校验课程是否存在
     * 2. 查询数据库已有的课程详情（用于冲突校验）
     * 3. 读取Excel文件，转换为CourseDetailInfo列表
     * 4. 逐行校验数据（空值校验、阶段名称合法性、与数据库冲突校验、Excel内部重复校验）
     * 5. 批量插入数据库
     *
     * 规则：整个课程中天数必须唯一（不管哪个阶段）
     *
     * @param courseId 课程ID
     * @param file Excel文件
     */
    @Override
    public void importExcel(Long courseId, MultipartFile file) {
        // 1. 校验课程是否存在
        EduCourseEntity eduCourseEntity = eduCourseMapper.selectById(courseId);
        if (eduCourseEntity == null) {
            throw BusinessException.CourseNotExist;
        }

        // 2. 查询数据库已有的课程详情（用于冲突校验）
        List<EduCourseDetailEntity> existingList = eduCourseDetailMapper.selectByCourseId(courseId);
        Set<Integer> existingDayNumSet = new HashSet<>();
        for (EduCourseDetailEntity existing : existingList) {
            existingDayNumSet.add(existing.getDayNum());
        }

        // 3. 读取Excel文件
        List<CourseDetailInfo> dataList;
        try {
            dataList = EasyExcel.read(file.getInputStream(), CourseDetailInfo.class, null)
                    .sheet()
                    .doReadSync();
        } catch (IOException e) {
            throw BusinessException.DataError.newInstance("Excel文件读取失败");
        }

        // 4. 校验数据并转换为Entity
        List<EduCourseDetailEntity> entityList = validateAndConvertToEntities(dataList, courseId, existingDayNumSet);

        // 5. 批量插入数据库
        if (!entityList.isEmpty()) {
            eduCourseDetailMapper.insertBatch(entityList);
        }
    }

    /**
     * 校验Excel数据并转换为Entity列表
     *
     * @param dataList Excel读取的数据列表
     * @param courseId 课程ID
     * @param existingDayNumSet 数据库中已存在的天数集合
     * @return 校验通过后的Entity列表
     */
    private List<EduCourseDetailEntity> validateAndConvertToEntities(
            List<CourseDetailInfo> dataList, 
            Long courseId, 
            java.util.Set<Integer> existingDayNumSet) {
        
        List<EduCourseDetailEntity> entityList = new ArrayList<>();
        java.util.Set<Integer> excelDayNumSet = new java.util.HashSet<>();

        for (int i = 0; i < dataList.size(); i++) {
            CourseDetailInfo info = dataList.get(i);
            int rowNum = i + 1;

            // 空值校验
            if (StrUtil.isEmpty(info.getStageName()) || info.getDayNum() == null || StrUtil.isEmpty(info.getCourseContent())) {
                throw BusinessException.DataError.newInstance("第" + rowNum + "行数据有字段为空");
            }

            // 阶段名称合法性校验
            try {
                StageNameEnum.fromDesc(info.getStageName());
            } catch (IllegalArgumentException e) {
                throw BusinessException.DataError.newInstance("第" + rowNum + "行阶段名称'" + info.getStageName() + "'有误");
            }

            // 与数据库已有数据冲突校验
            if (existingDayNumSet.contains(info.getDayNum())) {
                throw BusinessException.DataError.newInstance("第" + rowNum + "行天数" + info.getDayNum() + "已存在");
            }

            // Excel内部重复校验
            if (!excelDayNumSet.add(info.getDayNum())) {
                throw BusinessException.DataError.newInstance("第" + rowNum + "行天数" + info.getDayNum() + "重复");
            }

            // 转换为Entity
            EduCourseDetailEntity entity = new EduCourseDetailEntity();
            BeanUtils.copyProperties(info, entity);
            entity.setCourseId(courseId);
            entity.setCreatedAt(new Date());
            entity.setUpdatedAt(new Date());
            entityList.add(entity);
        }

        return entityList;
    }

    /**
     * 将Entity转换为DetailRes VO
     *
     * BeanUtils.copyProperties: Spring提供的属性拷贝工具
     * - 自动拷贝同名同类型的属性
     * - 比手动setter更简洁
     * - 但性能略差，高并发场景可考虑手写转换
     */
    private CourseDetailDetailRes buildCourseDetailDetailRes(EduCourseDetailEntity entity) {
        CourseDetailDetailRes res = new CourseDetailDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    /**
     * 将Entity转换为PageRes VO
     * 与上面方法类似，只是目标类型不同
     */
    private CourseDetailPageRes buildCourseDetailPageRes(EduCourseDetailEntity entity) {
        CourseDetailPageRes res = new CourseDetailPageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
