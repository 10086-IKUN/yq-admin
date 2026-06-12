package cn.yanque.models.edu.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.common.pojo.entity.EduCourseDetailEntity;
import cn.yanque.common.pojo.vo.req.CourseDetailCreateReq;
import cn.yanque.common.pojo.vo.req.CourseDetailPageReq;
import cn.yanque.common.pojo.vo.req.CourseDetailUpdateReq;
import cn.yanque.common.pojo.vo.res.CourseDetailCreateRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDeleteRes;
import cn.yanque.common.pojo.vo.res.CourseDetailDetailRes;
import cn.yanque.common.pojo.vo.res.CourseDetailPageRes;
import cn.yanque.common.pojo.vo.res.CourseDetailUpdateRes;
import cn.yanque.models.edu.mapper.EduCourseDetailMapper;
import cn.yanque.models.edu.service.EduCourseDetailService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

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

    /**
     * 新增课程详情
     *
     * 执行流程：
     * 1. 创建Entity对象
     * 2. 手动设置各字段值（因为Req和Entity字段不完全一致）
     * 3. 设置创建时间和更新时间
     * 4. 调用Mapper插入数据库
     * 5. 插入后entity.getId()自动填充了数据库生成的ID
     * 6. 封装响应对象返回
     */
    @Override
    @Transactional(rollbackFor = Exception.class)  // 任何异常都回滚事务
    public CourseDetailCreateRes addCourseDetail(CourseDetailCreateReq req) {
        EduCourseDetailEntity entity = new EduCourseDetailEntity();
        entity.setCourseId(req.getCourseId());
        entity.setStageName(req.getStageName());
        entity.setDayNum(req.getDayNum());
        entity.setCourseContent(req.getCourseContent());
        entity.setCreatedAt(new Date());  // 创建时间=当前时间
        entity.setUpdatedAt(new Date());  // 更新时间=当前时间
        eduCourseDetailMapper.insert(entity);

        CourseDetailCreateRes res = new CourseDetailCreateRes();
        res.setId(entity.getId());  // 获取数据库生成的自增ID
        return res;
    }

    /**
     * 更新课程详情
     *
     * 执行流程：
     * 1. 先根据ID查询记录是否存在（防御性编程）
     * 2. 不存在则抛出异常
     * 3. 存在则更新各字段
     * 4. 调用Mapper执行更新
     * 5. 检查更新结果（rows=0表示记录已被删除）
     *
     * 注意：这里采用"先查后改"模式，而不是直接update
     * 好处是可以给出更准确的错误提示
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDetailUpdateRes updateCourseDetail(CourseDetailUpdateReq req) {
        // 1. 查询记录是否存在
        EduCourseDetailEntity entity = eduCourseDetailMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.CourseDetailNotExist;  // 记录不存在，抛出业务异常
        }

        // 2. 更新字段值
        entity.setCourseId(req.getCourseId());
        entity.setStageName(req.getStageName());
        entity.setDayNum(req.getDayNum());
        entity.setCourseContent(req.getCourseContent());
        entity.setUpdatedAt(new Date());  // 更新时间=当前时间

        // 3. 执行更新，检查结果
        int rows = eduCourseDetailMapper.updateById(entity);
        if (rows == 0) {
            // 更新0行说明记录在查询后被删除了（并发场景）
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
     * 将Entity转换为DetailRes VO
     *
     * BeanUtils.copyProperties: Spring提供的属性拷贝工具
     * - 自动拷贝同名同类型的属性
     * - 比手动setter更简洁
     * - 但性能略差，高并发场景可考虑手写转换
     */
    private CourseDetailDetailRes buildCourseDetailDetailRes(EduCourseDetailEntity entity) {
        CourseDetailDetailRes res = new CourseDetailDetailRes();
        BeanUtils.copyProperties(entity, res);  // 源->目标，自动拷贝同名属性
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
