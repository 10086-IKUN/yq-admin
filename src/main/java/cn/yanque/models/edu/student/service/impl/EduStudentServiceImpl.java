package cn.yanque.models.edu.student.service.impl;

import cn.yanque.common.api.PageResult;
import cn.yanque.common.exception.BusinessException;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.edu.student.pojo.vo.req.StudentCreateReq;
import cn.yanque.models.edu.student.pojo.vo.req.StudentPageReq;
import cn.yanque.models.edu.student.pojo.vo.req.StudentUpdateReq;
import cn.yanque.models.edu.student.pojo.vo.res.StudentCreateRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentDeleteRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentDetailRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentPageRes;
import cn.yanque.models.edu.student.pojo.vo.res.StudentUpdateRes;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.service.EduStudentService;
import cn.yanque.models.sale.entity.SaleOrderEntity;
import cn.yanque.models.sale.entity.SaleProductEntity;
import cn.yanque.models.sale.service.SaleOrderService;
import cn.yanque.models.sale.service.SaleProductService;
import cn.yanque.models.system.config.service.SysConfigService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 学生服务实现类
 * 实现学生管理的业务逻辑，包括增删改查
 */
@Service
public class EduStudentServiceImpl implements EduStudentService {

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Autowired
    private SaleProductService saleProductService;

    @Autowired
    private SaleOrderService saleOrderService;


    /**
     * 添加学生
     * @param req 创建学生请求参数
     * @return 创建成功的学生ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentCreateRes addStudent(StudentCreateReq req) {
        SaleProductEntity product = saleProductService.getById(req.getProductId());
        if (product == null) {
            throw new BusinessException(400, "产品不存在");
        }

        EduStudentEntity entity = new EduStudentEntity();
        entity.setStudentCode(req.getStudentCode());
        entity.setStudentName(req.getStudentName());
        entity.setPhone(req.getPhone());
        entity.setPassword(req.getPassword() != null ? req.getPassword() : "123456");
        entity.setGraduationSession(req.getGraduationSession());
        entity.setSchool(req.getSchool());
        entity.setEducation(req.getEducation());
        // 学员学习方式跟随产品，避免前端手动选择后与产品类型不一致。
        entity.setStudyMode(product.getStudyMode());
        entity.setClassId(req.getClassId());
        entity.setProductId(req.getProductId());
        entity.setJoinTime(req.getJoinTime());
        entity.setCreatedAt(new Date());
        entity.setUpdatedAt(new Date());
        eduStudentMapper.insert(entity);

        // 管理端新增学员后，立即生成一笔报名订单。
        // 该订单不参与 20 分钟自动取消，学员登录后必须先完成支付。
        SaleOrderEntity order = new SaleOrderEntity();
        order.setStudentName(entity.getStudentName());
        order.setPhone(entity.getPhone());
        order.setProductId(entity.getProductId());
        order.setClassId(entity.getClassId());
        order.setStudentCode(entity.getStudentCode());
        order.setProductAmount(product.getProductPrice());
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setReceivableAmount(product.getProductPrice());
        order.setPaidAmount(BigDecimal.ZERO);
        order.setOrderStatus("PENDING");
        order.setOrderSource("ADMIN_CREATE");
        saleOrderService.createOrder(order);

        StudentCreateRes res = new StudentCreateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 修改学生
     * @param req 更新学生请求参数
     * @return 更新后的学生ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentUpdateRes updateStudent(StudentUpdateReq req) {
        EduStudentEntity entity = eduStudentMapper.selectById(req.getId());
        if (entity == null) {
            throw BusinessException.StudentNotExist;
        }

        entity.setStudentCode(req.getStudentCode());
        entity.setStudentName(req.getStudentName());
        entity.setPhone(req.getPhone());
        if (req.getPassword() != null) {
            entity.setPassword(req.getPassword());
        }
        entity.setGraduationSession(req.getGraduationSession());
        entity.setSchool(req.getSchool());
        entity.setEducation(req.getEducation());
        entity.setStudyMode(req.getStudyMode());
        entity.setClassId(req.getClassId());
        entity.setProductId(req.getProductId());
        entity.setJoinTime(req.getJoinTime());
        entity.setUpdatedAt(new Date());

        int rows = eduStudentMapper.updateById(entity);
        if (rows == 0) {
            throw BusinessException.StudentNotExist;
        }

        StudentUpdateRes res = new StudentUpdateRes();
        res.setId(entity.getId());
        return res;
    }

    /**
     * 删除学生
     * @param id 学生ID
     * @return 删除结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public StudentDeleteRes deleteStudent(Long id) {
        EduStudentEntity entity = eduStudentMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.StudentNotExist;
        }

        int rows = eduStudentMapper.deleteById(id);
        if (rows == 0) {
            throw BusinessException.StudentNotExist;
        }

        StudentDeleteRes res = new StudentDeleteRes();
        res.setId(id);
        return res;
    }

    /**
     * 根据ID查询学生详情
     * @param id 学生ID
     * @return 学生详细信息
     */
    @Override
    public StudentDetailRes getStudentById(Long id) {
        EduStudentEntity entity = eduStudentMapper.selectById(id);
        if (entity == null) {
            throw BusinessException.StudentNotExist;
        }
        return buildStudentDetailRes(entity);
    }

    /**
     * 分页查询学生
     * @param req 分页查询参数（关键词、学习模式、班级ID、产品ID）
     * @return 分页学生列表
     */
    @Override
    public PageResult<StudentPageRes> pageStudent(StudentPageReq req) {
        PageHelper.startPage(req.getPageNum(), req.getPageSize());
        List<EduStudentEntity> list = eduStudentMapper.selectPage(
                req.getKeyword(),
                req.getStudyMode(),
                req.getClassId(),
                req.getProductId()
        );
        PageInfo<EduStudentEntity> pageInfo = new PageInfo<>(list);

        List<StudentPageRes> records = list.stream().map(this::buildStudentPageRes).toList();
        return new PageResult<>(pageInfo.getTotal(), req.getPageNum(), req.getPageSize(), records);
    }

    /**
     * 构建学生详情响应对象
     * @param entity 学生实体
     * @return 学生详情
     */
    private StudentDetailRes buildStudentDetailRes(EduStudentEntity entity) {
        StudentDetailRes res = new StudentDetailRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }

    /**
     * 构建学生分页响应对象
     * @param entity 学生实体
     * @return 学生分页信息
     */
    private StudentPageRes buildStudentPageRes(EduStudentEntity entity) {
        StudentPageRes res = new StudentPageRes();
        BeanUtils.copyProperties(entity, res);
        return res;
    }
}
