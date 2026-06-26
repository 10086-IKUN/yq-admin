package cn.yanque.task;

import cn.yanque.models.message.service.SysMessageService;
import cn.yanque.models.studentTag.service.StudentTagService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DailyTask {

    @Autowired
    private StudentTagService studentTagService;

    @Autowired
    private SysMessageService sysMessageService;

    /**
     * 每天凌晨2点执行：计算学员标签 + 生成回访提醒
     */
    @Scheduled(cron = "0 0 2 * * ?")
    public void dailyTask() {
        log.info("开始执行每日定时任务...");

        try {
            // 1. 重新计算所有学员标签
            studentTagService.calculateAllTags();

            // 2. 生成今日回访提醒消息
            sysMessageService.generateVisitRemindMessages();

            log.info("每日定时任务执行完成");
        } catch (Exception e) {
            log.error("每日定时任务执行失败", e);
        }
    }
}
