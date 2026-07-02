package cn.yanque.models.message.service.impl;

import cn.yanque.models.edu.clazz.mapper.EduClassMapper;
import cn.yanque.models.edu.clazz.pojo.entity.EduClassEntity;
import cn.yanque.models.edu.student.mapper.EduStudentMapper;
import cn.yanque.models.edu.student.pojo.entity.EduStudentEntity;
import cn.yanque.models.message.mapper.SysMessageMapper;
import cn.yanque.models.message.pojo.entity.SysMessageEntity;
import cn.yanque.models.message.service.SysMessageService;
import cn.yanque.models.studentVisit.mapper.StudentVisitMapper;
import cn.yanque.models.studentVisit.pojo.entity.StudentVisitEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j

/**
 * 系统消息服务。
 *
 * <p>除普通消息的读写和已读处理外，还会根据学生回访计划生成班主任的回访提醒。</p>
 */
public class SysMessageServiceImpl implements SysMessageService {

    @Autowired
    private SysMessageMapper sysMessageMapper;

    @Autowired
    private StudentVisitMapper studentVisitMapper;

    @Autowired
    private EduStudentMapper eduStudentMapper;

    @Autowired
    private EduClassMapper eduClassMapper;

    @Override
    public void send(SysMessageEntity entity) {
        sysMessageMapper.insert(entity);
    }

    @Override
    public List<SysMessageEntity> list(Long userId, Integer isRead) {
        return sysMessageMapper.selectByUserId(userId, isRead);
    }

    @Override
    public int countUnread(Long userId) {
        return sysMessageMapper.countUnread(userId);
    }

    @Override
    public void markAsRead(Long id) {
        sysMessageMapper.markAsRead(id);
    }

    @Override
    public void markAllAsRead(Long userId) {
        sysMessageMapper.markAllAsRead(userId);
    }

    @Override
    public void generateVisitRemindMessages() {
        log.info("开始生成回访提醒消息...");

        // 查询所有有待回访任务的教师
        LocalDate today = LocalDate.now();

        // 查询待回访学生后按负责老师分组发送系统消息。
        List<EduStudentEntity> students = eduStudentMapper.selectAll();

        for (EduStudentEntity student : students) {
            try {
                // 查询该学员最新的待回访记录。
                StudentVisitEntity visit = studentVisitMapper.selectLatestPending(student.getId());
                if (visit == null || visit.getNextVisitTime().isAfter(today)) {
                    continue;
                }

                // 通过学员班级找到负责的班主任。
                EduClassEntity clazz = eduClassMapper.selectById(student.getClassId());
                if (clazz == null || clazz.getHeadTeacherId() == null) {
                    continue;
                }

                // 检查是否已发送过今日提醒
                // 简化处理：直接发送（实际可加去重逻辑）
                // 生成并发送班主任的回访提醒消息。
                SysMessageEntity message = new SysMessageEntity();
                message.setUserId(clazz.getHeadTeacherId());
                message.setTitle("回访提醒");
                message.setContent("学员 " + student.getStudentName() + " 需要今日回访");
                message.setMsgType("VISIT_REMIND");
                message.setRelatedId(visit.getId());
                sysMessageMapper.insert(message);

            } catch (Exception e) {
                log.error("生成回访提醒消息失败: studentId={}", student.getId(), e);
            }
        }

        log.info("回访提醒消息生成完成");
    }
}
