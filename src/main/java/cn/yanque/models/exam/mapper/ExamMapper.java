package cn.yanque.models.exam.mapper;

import cn.yanque.models.exam.pojo.entity.ExamAnswerEntity;
import cn.yanque.models.exam.pojo.entity.ExamAttemptEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperEntity;
import cn.yanque.models.exam.pojo.entity.ExamPaperQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamQuestionEntity;
import cn.yanque.models.exam.pojo.entity.ExamScheduleEntity;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * 在线考试模块 Mapper。
 *
 * <p>考试模块的六张表关系紧密，当前统一放在一个 Mapper 中，
 * 便于事务内同时维护试卷、题目、考试记录和答案。</p>
 */
public interface ExamMapper {

    int insertQuestion(ExamQuestionEntity entity);

    int updateQuestion(ExamQuestionEntity entity);

    int deleteQuestion(@Param("id") Long id);

    ExamQuestionEntity selectQuestionById(@Param("id") Long id);

    List<ExamQuestionEntity> selectQuestionPage(@Param("keyword") String keyword,
                                                @Param("questionType") String questionType,
                                                @Param("status") Integer status);

    int insertPaper(ExamPaperEntity entity);

    int updatePaper(ExamPaperEntity entity);

    int deletePaper(@Param("id") Long id);

    ExamPaperEntity selectPaperById(@Param("id") Long id);

    List<ExamPaperEntity> selectPaperPage(@Param("keyword") String keyword,
                                          @Param("status") String status);

    int deletePaperQuestions(@Param("paperId") Long paperId);

    int insertPaperQuestion(ExamPaperQuestionEntity entity);

    List<ExamPaperQuestionEntity> selectPaperQuestions(@Param("paperId") Long paperId);

    int countPaperSchedules(@Param("paperId") Long paperId);

    int insertSchedule(ExamScheduleEntity entity);

    int updateSchedule(ExamScheduleEntity entity);

    int deleteSchedule(@Param("id") Long id);

    int publishAnswers(@Param("id") Long id);

    ExamScheduleEntity selectScheduleById(@Param("id") Long id);

    List<ExamScheduleEntity> selectSchedulePage(@Param("keyword") String keyword,
                                                @Param("classId") Long classId,
                                                @Param("status") String status);

    List<ExamAttemptEntity> selectAttemptsBySchedule(@Param("scheduleId") Long scheduleId);

    ExamAttemptEntity selectAttemptById(@Param("id") Long id);

    ExamAttemptEntity selectAttemptByStudent(@Param("scheduleId") Long scheduleId,
                                             @Param("studentId") Long studentId);

    int insertAttempt(ExamAttemptEntity entity);

    int insertAttemptAnswers(@Param("attemptId") Long attemptId,
                             @Param("paperId") Long paperId);

    List<ExamAnswerEntity> selectAnswersByAttempt(@Param("attemptId") Long attemptId);

    ExamAnswerEntity selectAnswerById(@Param("id") Long id);

    int updateAnswerContent(@Param("id") Long id,
                            @Param("attemptId") Long attemptId,
                            @Param("answerContent") String answerContent);

    int updateAnswerScore(@Param("id") Long id,
                          @Param("correct") Boolean correct,
                          @Param("score") BigDecimal score);

    int reviewAnswer(@Param("id") Long id,
                     @Param("score") BigDecimal score,
                     @Param("reviewComment") String reviewComment,
                     @Param("teacherId") Long teacherId);

    int submitAttempt(@Param("id") Long id,
                      @Param("objectiveScore") BigDecimal objectiveScore);

    int refreshAttemptScore(@Param("id") Long id);

    int markAttemptReviewed(@Param("id") Long id);

    List<ExamScheduleEntity> selectStudentExams(@Param("studentId") Long studentId);
}
