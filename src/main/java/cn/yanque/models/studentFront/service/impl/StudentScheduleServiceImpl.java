package cn.yanque.models.studentFront.service.impl;

import cn.yanque.models.edu.schedule.mapper.EduClassScheduleMapper;
import cn.yanque.models.edu.schedule.pojo.entity.EduClassScheduleEntity;
import cn.yanque.models.edu.schedule.pojo.vo.res.SchedulePageRes;
import cn.yanque.models.studentFront.service.StudentScheduleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学员端课表服务实现类
 * 实现学员端课表相关的数据操作
 */
@Service
public class StudentScheduleServiceImpl implements StudentScheduleService {

    @Autowired
    private EduClassScheduleMapper eduClassScheduleMapper;

    /**
     * 获取个人课表
     * @return 课表列表
     */
    @Override
    public List<SchedulePageRes> list() {
        // TODO: 根据学员ID获取班级ID，再查询课表
        // 暂时返回空列表，需要根据实际业务逻辑实现
        return List.of();
    }
}
